package astminer.parse.javaparser

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy

class JavaparserFunctionInfo(override val root: JavaParserNode, override val filePath: String) :
    FunctionInfo<JavaParserNode> {
    companion object {
        const val METHOD_NAME = "SimpleName"
        const val PARAMETER = "Prm"
        const val PARAMETER_NAME = "SimpleName"
        const val ARRAY_TYPE = "ArTy"
        const val ARRAY_BRACKETS = "[]"
        const val PRIMITIVE_TYPE = "Prim"
        const val CLASS_OR_INTERFACE_TYPE = "Cls"
        const val CLASS_OR_INTERFACE_DECLARATION = "ClsD"
        const val CLASS_NAME = "SimpleName"
    }

    override val nameNode: JavaParserNode? =
        root.getChildOfType(METHOD_NAME)

    override val parameters: List<FunctionInfoParameter> =
        root.preOrder().filter { it.typeLabel == PARAMETER }
            .mapNotNull { try {assembleParameter(it)} catch (e: IllegalStateException) {null} }

    private fun assembleParameter(node: JavaParserNode): FunctionInfoParameter {
        return FunctionInfoParameter(type = getParameterType(node), name = getParameterName(node))
    }

    private fun getParameterType(node: JavaParserNode): String {
        val possibleType = node.children.find { it.typeLabel != PARAMETER_NAME }
            ?: throw IllegalStateException("Can't find parameter type")
        return when (possibleType.typeLabel) {
            ARRAY_TYPE -> getParameterType(possibleType) + ARRAY_BRACKETS
            PRIMITIVE_TYPE -> possibleType.originalToken
            CLASS_OR_INTERFACE_TYPE -> possibleType.getChildOfType(CLASS_NAME)?.originalToken
            else -> null
        } ?: throw IllegalStateException("Can't find parameter type")
    }

    private fun getParameterName(node: JavaParserNode): String {
        return node.getChildOfType(PARAMETER_NAME)?.originalToken
            ?: throw IllegalStateException("Can't find parameter name")
    }

    override val enclosingElement: EnclosingElement<JavaParserNode>?
        = root.findEnclosingElementBy { it.typeLabel == CLASS_OR_INTERFACE_DECLARATION }?.assembleEnclosingClass()

    private fun JavaParserNode.assembleEnclosingClass(): EnclosingElement<JavaParserNode> {
        val name = this.getChildOfType(CLASS_NAME)?.originalToken
        return EnclosingElement(
            type = EnclosingElementType.Class,
            name = name,
            root = this
        )
    }
}