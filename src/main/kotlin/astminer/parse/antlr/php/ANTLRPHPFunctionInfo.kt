package astminer.parse.antlr.php

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy

class ANTLRPHPFunctionInfo(override val root: AntlrNode, override val filePath: String) : FunctionInfo<AntlrNode> {
    override val returnType = getElementType(root)
    override val nameNode: AntlrNode? = root.getChildOfType(FUNCTION_NAME)

    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()

    companion object {
        const val PARAMETERS_LIST = "formalParameterList"
        const val PARAMETER = "formalParameter"
        const val TYPE = "typeHint"
        const val PARAMETER_NAME = "VarName"
        const val CLASS_MEMBER = "classStatement"
        const val FUNCTION_NAME = "identifier"
        const val CLASS_DECLARATION = "classDeclaration"
        const val VAR_DECLARATION = "variableInitializer"
        const val ELLIPSIS = "Ellipsis"
        const val EXPRESSION = "expression"
        const val ASSIGN_OP = "assignmentOperator"
        const val LAMBDA_TOKEN = "LambdaFn"
        const val FUNCTION_TOKEN = "Function_"
        const val REFERENCE = "Ampersand"
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        // Parameters in this grammar have following structure (children order may be wrong):
        //formal parameter list -> formal parameter -> Ampersand
        //                                        | -> type hint
        //                                        | -> ellipsis
        //                                        | -> var init -> var name
        //                                                    | -> equal
        //                                                    | -> default value

        // No parameters
        val parameterList = root.getChildOfType(PARAMETERS_LIST) ?: return emptyList()

        // Checking if function have only one parameter
        // without ellipsis, type hint or default value
        if (parameterList.hasLastLabel(PARAMETER_NAME) || parameterList.hasLastLabel(VAR_DECLARATION)) {
            return listOf(assembleParameter(parameterList))
        }

        // Otherwise find all parameters
        return parameterList.getItOrChildrenOfType(PARAMETER).mapNotNull {
            try { assembleParameter(it) } catch (e: IllegalStateException) { return@mapNotNull null }
        }
    }

    private fun assembleParameter(parameterNode: AntlrNode): FunctionInfoParameter {
        return FunctionInfoParameter(
            name = getParameterName(parameterNode),
            type = getElementType(parameterNode)
        )
    }

    private fun getParameterName(parameterNode: AntlrNode): String {
        // "...$args" in php equivalent to *args in python
        val isSplattedArg = parameterNode.getChildOfType(ELLIPSIS) != null

        val isPassedByReference = parameterNode.getChildOfType(REFERENCE) != null

        if (parameterNode.hasLastLabel(PARAMETER_NAME)) return parameterNode.originalToken
            ?: throw IllegalStateException("No name was found for a parameter")

        val varInit = parameterNode.getItOrChildrenOfType(VAR_DECLARATION).first()

        val name = varInit.getItOrChildrenOfType(PARAMETER_NAME).first().originalToken
            ?: throw IllegalStateException("No name was found for a parameter")

        return (if (isPassedByReference) "&" else "") + (if (isSplattedArg) "..." else "") + name
    }

    private fun getElementType(element: AntlrNode): String? {
        return element.getChildOfType(TYPE)?.originalToken
    }

    private fun collectEnclosingElement(): EnclosingElement<AntlrNode>? {
        val enclosing = root.findEnclosingElementBy { it.isPossibleEnclosing() } ?: return null
        return try {
            EnclosingElement(
                root = enclosing,
                name = getEnclosingElementName(enclosing),
                type = getEnclosingType(enclosing)
            )
        } catch (e: IllegalStateException) {
            null
        }
    }

    private fun getEnclosingType(enclosing: AntlrNode): EnclosingElementType {
        return when {
            enclosing.isMethod() -> EnclosingElementType.Method
            enclosing.isFunction() -> EnclosingElementType.Function
            enclosing.isClass() -> EnclosingElementType.Class
            enclosing.isAssignExpression() -> EnclosingElementType.VariableDeclaration
            else -> throw IllegalStateException("No type can be associated")
        }
    }

    private fun getEnclosingElementName(enclosing: AntlrNode) : String?{
        return when {
            enclosing.isFunction() || enclosing.isClass() -> enclosing.getChildOfType(FUNCTION_NAME)?.originalToken
            enclosing.isAssignExpression() -> enclosing.children.find { it.hasLastLabel(PARAMETER_NAME) }?.originalToken
            else -> throw IllegalStateException("No type can be associated")
        }
    }

    // No check for method because method is a function
    private fun AntlrNode.isPossibleEnclosing() = isFunction() || isClass() || isAssignExpression()

    private fun AntlrNode.isMethod() = isFunction() && (hasFirstLabel(CLASS_MEMBER))

    private fun AntlrNode.isFunction() = getChildOfType(LAMBDA_TOKEN) != null || getChildOfType(FUNCTION_TOKEN) != null

    private fun AntlrNode.isAssignExpression() = hasFirstLabel(EXPRESSION) && (getChildOfType(ASSIGN_OP) != null)

    private fun AntlrNode.isClass(): Boolean = hasLastLabel(CLASS_DECLARATION)
}