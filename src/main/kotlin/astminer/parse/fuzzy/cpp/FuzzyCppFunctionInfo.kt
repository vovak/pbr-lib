package astminer.parse.fuzzy.cpp

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy

class FuzzyCppFunctionInfo(override val root: FuzzyNode, override val filePath: String): FunctionInfo<FuzzyNode> {
    companion object {
        private const val METHOD_NAME_NODE = "NAME"
        private const val METHOD_RETURN_NODE = "METHOD_RETURN"
        private const val METHOD_RETURN_TYPE_NODE = "TYPE_FULL_NAME"

        private const val CLASS_DECLARATION_NODE = "TYPE_DECL"
        private const val CLASS_NAME_NODE = "NAME"

        private const val METHOD_PARAMETER_NODE = "METHOD_PARAMETER_IN"
        private const val PARAMETER_NAME_NODE = "NAME"
        private const val PARAMETER_TYPE_NODE = "TYPE_FULL_NAME"
    }

    override val returnType: String? = collectReturnType()
    override val enclosingElement: EnclosingElement<FuzzyNode>? = collectEnclosingClass()
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val nameNode: FuzzyNode? = collectNameNode()

    private fun collectNameNode(): FuzzyNode? {
        return root.getChildOfType(METHOD_NAME_NODE) as FuzzyNode?
    }

    private fun collectReturnType(): String? {
        return root.getChildOfType(METHOD_RETURN_NODE)?.getChildOfType(METHOD_RETURN_TYPE_NODE)?.originalToken
    }

    private fun collectEnclosingClass(): EnclosingElement<FuzzyNode>? {
        val enclosingClass = findEnclosingClass() ?: return null
        val enclosingClassName = findEnclosingClassName(enclosingClass) ?: return null
        return EnclosingElement(
            root = enclosingClass,
            type = EnclosingElementType.Class,
            name = enclosingClassName
        )
    }

    private fun findEnclosingClass(): FuzzyNode? {
        return root.findEnclosingElementBy { it.typeLabel == CLASS_DECLARATION_NODE }
    }

    private fun findEnclosingClassName(enclosingClass: FuzzyNode): String? {
        return enclosingClass.getChildOfType(CLASS_NAME_NODE)?.originalToken
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        val parameters = root.getChildrenOfType(METHOD_PARAMETER_NODE)
        return parameters.map { param ->
            val type = param.getChildOfType(PARAMETER_TYPE_NODE)?.originalToken
            val name = param.getChildOfType(PARAMETER_NAME_NODE)?.originalToken ?: ""
            FunctionInfoParameter(name, type)
        }
    }
}