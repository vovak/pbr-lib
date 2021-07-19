package astminer.parse.gumtree.java.JDT

import astminer.common.getProjectFilesWithExtension
import astminer.parseFiles
import org.junit.Assert
import org.junit.Test
import java.io.*

class GumTreeJavaJDTParserTest {
    @Test
    fun testNodeIsNotNull() {
        val parser = GumTreeJavaJDTParser()
        val file = File("src/test/resources/examples/1.java")

        val node = parser.parseFile(file).root
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testProjectParsing() {
        val parser = GumTreeJavaJDTParser()
        val projectRoot = File("src/test/resources/examples")

        val trees = parser.parseFiles(getProjectFilesWithExtension(projectRoot, "java"))
        Assert.assertEquals("There is only 2 file with .java extension in 'testData/examples' folder",2, trees.size)
        trees.forEach { Assert.assertNotNull("Parse tree for a valid file should not be null", it) }
    }
}