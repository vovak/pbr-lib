package astminer.parse.gumtree.java.srcML

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy
import astminer.parse.gumtree.GumTreeNode

class GumTreeJavaSrcmlFunctionInfo(override val root: GumTreeNode, override val filePath: String) :
    FunctionInfo<GumTreeNode> {
    companion object {
        const val TYPE = "type"
        const val NAME = "name"
        const val ARRAY_BRACKETS = "index"
        const val PARAMETER = "parameter"
        const val VAR_DECLARATION = "decl"
        const val CLASS_DECLARATION = "class"
    }

    override val nameNode: GumTreeNode? = root.getChildOfType(NAME)

    override val returnType: String = root.extractType()

    override val parameters: List<FunctionInfoParameter> = root.preOrder().filter { it.typeLabel == PARAMETER }
        .mapNotNull { try {assembleParameter(it)} catch (e: IllegalStateException) {null} }

    private fun assembleParameter(node: GumTreeNode): FunctionInfoParameter {
        val parameter = node.getChildOfType(VAR_DECLARATION)
            ?: throw IllegalStateException("No variable found")
        val name = parameter.getChildOfType(NAME)?.originalToken
            ?: throw IllegalStateException("Parameter name was not found")
        val type = parameter.extractType()
        return FunctionInfoParameter(name, type)
    }

    override val enclosingElement: EnclosingElement<GumTreeNode>? =
        root.findEnclosingElementBy { it.typeLabel ==  CLASS_DECLARATION}?.assembleEnclosing()

    private fun GumTreeNode.assembleEnclosing(): EnclosingElement<GumTreeNode>? {
        return EnclosingElement(
            type = EnclosingElementType.Class,
            name = this.getChildOfType(NAME)?.originalToken ?: return null,
            root = this
        )
    }

    private fun GumTreeNode.extractType(): String {
        val typeNode = this.getChildOfType(TYPE) ?: throw IllegalStateException("No type found")
        return typeNode.preOrder().joinToString(separator = "")
        { node -> if (node.typeLabel == ARRAY_BRACKETS) { "[]" } else { node.originalToken } }
    }
}