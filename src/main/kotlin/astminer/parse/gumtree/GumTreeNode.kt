package astminer.parse.gumtree

import astminer.common.model.Node
import com.github.gumtreediff.tree.ITree
import com.github.gumtreediff.tree.TreeContext

class GumTreeNode(val wrappedNode: ITree, val context: TreeContext,override var parent: GumTreeNode?) : Node() {
    override val typeLabel: String
        get() = context.getTypeLabel(wrappedNode)

    override val children: MutableList<GumTreeNode> by lazy {
        wrappedNode.children.map { GumTreeNode(it, context, this) }.toMutableList()
    }
    override val originalToken: String = wrappedNode.label

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun getChildOfType(typeLabel: String): GumTreeNode? =
        getChildrenOfType(typeLabel).firstOrNull()

    override fun getChildrenOfType(typeLabel: String): List<GumTreeNode> {
        val children = super.getChildrenOfType(typeLabel)
        return children.filterIsInstance<GumTreeNode>()
            .apply { if (size != children.size) throw TypeCastException("Node have children of different types") }
    }

    override fun preOrder(): List<GumTreeNode> = super.preOrder().map { it as GumTreeNode }
}

fun wrapGumTreeNode(treeContext: TreeContext): GumTreeNode {
    return GumTreeNode(treeContext.root, treeContext, null)
}