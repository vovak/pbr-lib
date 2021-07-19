package astminer.parse.spoon

import astminer.common.getProjectFilesWithExtension
import astminer.parseFiles
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import kotlin.test.assertNotNull

internal class SpoonParserTest {
    val parser = SpoonJavaParser()

    @Test
    fun simpleExampleNotNull() {
        val file = File("src/test/resources/examples/1.java")
        val tree = parser.parseFile(file)
        assertNotNull(tree)
    }

    @Test
    fun testIntArrayCallParsing() {
        val file = File("src/test/resources/arrayCalls/IntArrayInitialization.java")

        val node = parser.parseInputStream(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testStringArrayCallParsing() {
        val file = File("src/test/resources/arrayCalls/StringArrayInitialization.java")

        val node = parser.parseInputStream(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testComplexArrayCallParsing() {
        val file = File("src/test/resources/arrayCalls/ComplexArrayInitialization.java")

        val node = parser.parseInputStream(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testDoubleArrayCallParsing() {
        val file = File("src/test/resources/arrayCalls/DoubleArrayInitialization.java")

        val node = parser.parseInputStream(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testComplexFile() {
        val file = File("src/test/resources/arrayCalls/ComplexTest.java")

        val node = parser.parseInputStream(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testProjectParsing() {
        val projectRoot = File("src/test/resources/arrayCalls")
        val trees = parser.parseFiles(getProjectFilesWithExtension(projectRoot, "java"))
        Assert.assertEquals("There is only 5 file with .java extension in 'testData/arrayCalls' folder",5, trees.size)
        trees.forEach { Assert.assertNotNull("Parse tree for a valid file should not be null", it) }
    }
}