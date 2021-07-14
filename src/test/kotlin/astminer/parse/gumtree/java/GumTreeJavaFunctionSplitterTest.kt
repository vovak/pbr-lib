package astminer.parse.gumtree.java

import astminer.common.model.FunctionInfo
import astminer.parse.gumtree.GumTreeNode
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

private fun createTree(filename: String): GumTreeNode =
    GumTreeJavaParser().parseInputStream(File(filename).inputStream())

private fun createAndSplitTree(filename: String): Collection<FunctionInfo<GumTreeNode>> =
    GumTreeJavaFunctionSplitter().splitIntoFunctions(createTree(filename), filename)

class GumTreeJavaFunctionSplitterTest {
    @Test
    fun testMethodExtraction1() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/1.java")

        assertEquals(1, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("fun", name)
            assertEquals("void", returnType)
            assertEquals("SingleFunction", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters.map { it.name })
            assertEquals(listOf("String[]", "int"), parameters.map { it.type })
        }

    }

    @Test
    fun testMethodExtraction2() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/2.java")

        assertEquals(1, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("main", name)
            assertEquals("void", returnType)
            assertEquals("InnerClass", enclosingElement?.name)
            assertEquals(listOf("args"), parameters.map { it.name })
            assertEquals(listOf("String[]"), parameters.map { it.type })
        }
    }

    @Test
    fun testMethodExtraction3() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/3.java")

        assertEquals(2, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("main", name)
            assertEquals("void", returnType)
            assertEquals("InnerClass", enclosingElement?.name)
            assertEquals(listOf("args"), parameters.map { it.name })
            assertEquals(listOf("String[]"), parameters.map { it.type })
        }
        with(functionInfos.last()) {
            assertEquals("fun", name)
            assertEquals("void", returnType)
            assertEquals("SingleMethodInnerClass", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters.map { it.name })
            assertEquals(listOf("String[]", "int"), parameters.map { it.type })
        }
    }

    @Test
    fun testMethodExtraction4() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/4.java")

        assertEquals(1, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("fun", name)
            assertEquals("int", returnType)
            assertEquals("SingleFunction", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters.map { it.name })
            assertEquals(listOf("int", "SingleFunction"), parameters.map { it.type })
        }
    }

    //TODO: add more tests
}