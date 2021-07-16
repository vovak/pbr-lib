package astminer.parse.javaparser

import org.junit.Assert
import org.junit.Test
import java.io.File

internal class JavaParserParseWrapperTest {
    @Test
    fun testNodeIsNotNull() {
        val parser = JavaParserParseWrapper()
        val file = File("src/test/resources/examples/1.java")

        val node = parser.parseFile(file).root
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }
}