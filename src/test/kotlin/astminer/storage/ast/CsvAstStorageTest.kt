package astminer.storage.ast

import astminer.common.createBamboo
import astminer.common.createDummyTree
import astminer.common.createSmallTree
import org.junit.Assert
import org.junit.Test

class CsvAstStorageTest {
    private fun generateCorrectAstStringForBamboo(from: Int, to: Int): String {
        if (from == to) {
            return "$from $from{}"
        }
        val child = generateCorrectAstStringForBamboo(from + 1, to)
        return "$from $from{$child}"
    }

    @Test
    fun testAstString() {
        val root = createSmallTree()
        val storage = CsvAstStorage(".")
        storage.store(root.labeledWith("entityId"))

        Assert.assertEquals(storage.astString(root), "1 1{2 2{}3 3{4 4{}}}")
    }

    @Test
    fun `test ast string for bigger tree`() {
        val root = createDummyTree()
        val storage = CsvAstStorage(".")
        storage.store(root.labeledWith("entityId"))

        val expected = "1 1{2 2{3 3{}4 4{}5 5{}}6 6{7 7{}8 8{}}}"
        Assert.assertEquals(expected, storage.astString(root))
    }

    @Test
    fun `test ast string for small bamboo`() {
        val bamboo = createBamboo(10)
        val storage = CsvAstStorage(".")
        storage.store(bamboo.labeledWith("entityId"))

        val expected = generateCorrectAstStringForBamboo(1, 10)
        Assert.assertEquals(expected, storage.astString(bamboo))
    }

    @Test
    fun `test ast string for big bamboo`() {
        val bamboo = createBamboo(100)
        val storage = CsvAstStorage(".")
        storage.store(bamboo.labeledWith("entityId"))

        val expected = generateCorrectAstStringForBamboo(1, 100)
        Assert.assertEquals(expected, storage.astString(bamboo))
    }

}