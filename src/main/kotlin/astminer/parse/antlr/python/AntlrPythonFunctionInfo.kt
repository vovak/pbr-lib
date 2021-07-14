package astminer.parse.antlr.python

import astminer.common.model.*
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy

class AntlrPythonFunctionInfo(override val root: AntlrNode, override val filePath: String) : FunctionInfo<AntlrNode> {
    override val nameNode: AntlrNode? = collectNameNode()
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()

    companion object {
        private const val FUNCTION_NODE = "funcdef"
        private const val FUNCTION_NAME_NODE = "NAME"

        private const val CLASS_DECLARATION_NODE = "classdef"
        private const val CLASS_NAME_NODE = "NAME"

        private const val METHOD_PARAMETER_NODE = "parameters"
        private const val METHOD_PARAMETER_INNER_NODE = "typedargslist"
        private const val METHOD_SINGLE_PARAMETER_NODE = "tfpdef"
        private const val PARAMETER_NAME_NODE = "NAME"
        private const val PARAMETER_TYPE_NODE = "test"
        //It's seems strange but it works because actual type label will be
        //test|or_test|and_test|not_test|comparison|expr|xor_expr...
        // ..|and_expr|shift_expr|arith_expr|term|factor|power|atom_expr|atom|NAME

        private val POSSIBLE_ENCLOSING_ELEMENTS = listOf(CLASS_DECLARATION_NODE, FUNCTION_NODE)
        private const val BODY = "suite"
    }

    private fun collectNameNode(): AntlrNode? {
        return root.getChildOfType(FUNCTION_NAME_NODE)
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        val parametersRoot = root.getChildOfType(METHOD_PARAMETER_NODE)
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE) ?: return emptyList()

        val methodHaveOnlyOneParameter =
            innerParametersRoot.lastLabelIn(listOf(METHOD_SINGLE_PARAMETER_NODE, PARAMETER_NAME_NODE))
        if (methodHaveOnlyOneParameter) {
            return listOf(assembleMethodInfoParameter(innerParametersRoot))
        }

        return innerParametersRoot.getChildrenOfType(METHOD_SINGLE_PARAMETER_NODE).map { node ->
            assembleMethodInfoParameter(node)
        }
    }

    private fun assembleMethodInfoParameter(parameterNode: AntlrNode): FunctionInfoParameter {
        val parameterHaveNoDefaultOrType = parameterNode.hasLastLabel(PARAMETER_NAME_NODE)
        val parameterName = if (parameterHaveNoDefaultOrType) parameterNode.originalToken
                            else parameterNode.getChildOfType(PARAMETER_NAME_NODE)?.originalToken
        require(parameterName != null) { "Method name was not found" }

        val parameterType = parameterNode.getChildOfType(PARAMETER_TYPE_NODE)?.getTokensFromSubtree()

        return FunctionInfoParameter(
            name = parameterName,
            type = parameterType
        )
    }

    // TODO: refactor remove nested whens
    private fun collectEnclosingElement(): EnclosingElement<AntlrNode>? {
        val enclosingNode = root.findEnclosingElementBy { it.lastLabelIn(POSSIBLE_ENCLOSING_ELEMENTS) } ?: return null
        val type = when {
            enclosingNode.hasLastLabel(CLASS_DECLARATION_NODE) -> EnclosingElementType.Class
            enclosingNode.hasLastLabel(FUNCTION_NODE) -> {
                when {
                    enclosingNode.isMethod() -> EnclosingElementType.Method
                    else -> EnclosingElementType.Function
                }
            }
            else -> throw IllegalStateException("Enclosing node can only be function or class")
        }
        val name = when (type) {
            EnclosingElementType.Class -> enclosingNode.getChildOfType(CLASS_NAME_NODE)
            EnclosingElementType.Method, EnclosingElementType.Function -> enclosingNode.getChildOfType(FUNCTION_NAME_NODE)
            else -> throw IllegalStateException("Enclosing node can only be function or class")
        }?.originalToken
        return EnclosingElement(
            type = type,
            name = name,
            root = enclosingNode
        )
    }

    private fun Node.isMethod(): Boolean {
        val outerBody = parent
        if (outerBody?.typeLabel != BODY) return false

        val enclosingNode = outerBody.parent
        require(enclosingNode != null) { "Found body without enclosing element" }

        val lastLabel = decompressTypeLabel(enclosingNode.typeLabel).last()
        return lastLabel == CLASS_DECLARATION_NODE
    }
}