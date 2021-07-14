package astminer.common

import astminer.common.model.LabeledResult
import astminer.common.model.Node
import astminer.common.model.ParseResult


class DummyNode(
    override val typeLabel: String,
    override val children: MutableList<DummyNode> = mutableListOf()
) : Node() {

    //TODO("not implemented")
    override val parent: Node? = null

    override val originalToken: String = typeLabel

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    fun toParseResult() = ParseResult(this, "")

    fun labeledWith(label: String) = LabeledResult(this, label, "")
}

/**
 * Returns a small tree.
 * Diagram:
 *         1
 *       /   \
 *     /      \
 *    2        3
 *  / | \     / \
 * 4  5  6   7   8
 *
 */
fun createDummyTree(): DummyNode {
    val node4 = DummyNode("4", mutableListOf())
    val node5 = DummyNode("5", mutableListOf())
    val node6 = DummyNode("6", mutableListOf())
    val node7 = DummyNode("7", mutableListOf())
    val node8 = DummyNode("8", mutableListOf())

    val node2 = DummyNode("2", mutableListOf(node4, node5, node6))
    val node3 = DummyNode("3", mutableListOf(node7, node8))

    return DummyNode("1", mutableListOf(node2, node3))
}

/**
 * Returns a small tree.
 * Diagram:
 *      1
 *    /  \
 *   2    3
 *         \
 *          4
 */
fun createSmallTree(): DummyNode {
    val node4 = DummyNode("4", mutableListOf())
    val node3 = DummyNode("3", mutableListOf(node4))
    val node2 = DummyNode("2", mutableListOf())
    val node1 = DummyNode("1", mutableListOf(node2, node3))

    return node1
}

/**
 * Creates a bamboo
 * Diagram for [size] 3:
 * 1
 *  \
 *   2
 *    \
 *     3
 */
fun createBamboo(size: Int): DummyNode {
    var root = DummyNode(size.toString(), mutableListOf())
    for (i in 1 until size) {
        root = DummyNode((size - i).toString(), mutableListOf(root))
    }
    return root
}
