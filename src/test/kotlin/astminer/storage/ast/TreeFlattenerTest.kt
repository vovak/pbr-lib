package astminer.storage.ast

import astminer.common.DummyNode
import astminer.common.createBamboo
import astminer.common.createDummyTree
import astminer.common.createSmallTree
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

internal class TreeFlattenerTest {
    private lateinit var treeFlattener: TreeFlattener

    @Before
    fun init() {
        treeFlattener = TreeFlattener()
    }

    private data class EnumeratedResult(val id: Int, val typeLabel: String, val children: List<Int> = emptyList())

    private fun enumerate(node: DummyNode): List<EnumeratedResult> {
        val enumeratedNodes = treeFlattener.flatten(node)
        return enumeratedNodes.map { EnumeratedResult(it.id, it.node.typeLabel, it.children.map { child -> child.id }) }
    }

    @Test
    fun `test for 1 node`() {
        val node = DummyNode("a")
        val expected = listOf(EnumeratedResult(0, "a"))
        assertEquals(expected, enumerate(node))
    }

    @Test
    fun `test should be reusable`() {
        treeFlattener.flatten(DummyNode("something previous"))
        val node = DummyNode("a")
        val expected = listOf(EnumeratedResult(0, "a"))
        assertEquals(expected, enumerate(node))
    }

    @Test
    fun `test on small bamboo`() {
        val bamboo = createBamboo(10)
        val expected = List(10) { i ->
            EnumeratedResult(i, (i + 1).toString(), if (i == 9) emptyList() else listOf(i + 1))
        }
        assertEquals(expected, enumerate(bamboo))
    }

    @Test
    fun `test on big bamboo`() {
        val bamboo = createBamboo(1000)
        val expected = List(1000) { i ->
            EnumeratedResult(i, (i + 1).toString(), if (i == 999) emptyList() else listOf(i + 1))
        }
        assertEquals(expected, enumerate(bamboo))
    }

    @Test
    fun `test on very small dummy tree`() {
        val node = createSmallTree()
        val expected = listOf(
            EnumeratedResult(0, "1", listOf(1, 2)),
            EnumeratedResult(1, "2"),
            EnumeratedResult(2, "3", listOf(3)),
            EnumeratedResult(3, "4")
        )
        assertEquals(expected, enumerate(node))
    }

    @Test
    fun `test on small dummy tree`() {
        val node = createDummyTree()
        val expected = listOf(
            EnumeratedResult(0, "1", listOf(1, 5)),
            EnumeratedResult(1, "2", listOf(2, 3, 4)),
            EnumeratedResult(2, "4"),
            EnumeratedResult(3, "5"),
            EnumeratedResult(4, "6"),
            EnumeratedResult(5, "3", listOf(6, 7)),
            EnumeratedResult(6, "7"),
            EnumeratedResult(7, "8")
        )
        assertEquals(expected, enumerate(node))
    }
}
