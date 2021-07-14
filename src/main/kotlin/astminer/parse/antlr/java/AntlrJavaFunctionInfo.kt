package astminer.parse.antlr.java

import astminer.common.model.*
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy

class AntlrJavaFunctionInfo(override val root: AntlrNode, override val filePath: String) : FunctionInfo<AntlrNode> {
    override val nameNode: AntlrNode? = collectNameNode()
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val returnType: String? = collectReturnType()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingClass()

    companion object {
        private const val METHOD_RETURN_TYPE_NODE = "typeTypeOrVoid"
        private const val METHOD_NAME_NODE = "IDENTIFIER"

        private const val CLASS_DECLARATION_NODE = "classDeclaration"
        private const val CLASS_NAME_NODE = "IDENTIFIER"

        private const val METHOD_PARAMETER_NODE = "formalParameters"
        private const val METHOD_PARAMETER_INNER_NODE = "formalParameterList"
        private val METHOD_SINGLE_PARAMETER_NODES = listOf("formalParameter", "lastFormalParameter")
        private const val PARAMETER_RETURN_TYPE_NODE = "typeType"
        private const val PARAMETER_NAME_NODE = "variableDeclaratorId"
    }

    private fun collectNameNode(): AntlrNode? {
        return root.getChildOfType(METHOD_NAME_NODE)
    }

    private fun collectReturnType(): String? {
        val returnTypeNode = root.getChildOfType(METHOD_RETURN_TYPE_NODE)
        return returnTypeNode?.getTokensFromSubtree()
    }

    private fun collectEnclosingClass(): EnclosingElement<AntlrNode>? {
        val enclosingClassNode = root.findEnclosingElementBy { it.hasLastLabel(CLASS_DECLARATION_NODE) } ?: return null
        return EnclosingElement(
            type = EnclosingElementType.Class,
            name = enclosingClassNode.getChildOfType(CLASS_NAME_NODE)?.originalToken,
            root = enclosingClassNode
        )
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        val parametersRoot = root.getChildOfType(METHOD_PARAMETER_NODE)
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE) ?: return emptyList()

        if (innerParametersRoot.lastLabelIn(METHOD_SINGLE_PARAMETER_NODES)) {
            return listOf(getParameterInfo(innerParametersRoot))
        }

        return innerParametersRoot.children.filter {
            it.firstLabelIn(METHOD_SINGLE_PARAMETER_NODES)
        }.map { singleParameter -> getParameterInfo(singleParameter) }
    }

    private fun getParameterInfo(parameterNode: AntlrNode): FunctionInfoParameter {
        val returnTypeNode = parameterNode.getChildOfType(PARAMETER_RETURN_TYPE_NODE)
        val returnTypeToken = returnTypeNode?.getTokensFromSubtree()

        val parameterName = parameterNode.getChildOfType(PARAMETER_NAME_NODE)?.getTokensFromSubtree()
            ?: throw IllegalStateException("Parameter name wasn't found")

        return FunctionInfoParameter(parameterName, returnTypeToken)
    }
}

