package astminer.parse.javaparser

import astminer.common.model.Parser
import com.github.javaparser.StaticJavaParser
import java.io.InputStream

class JavaParserParseWrapper : Parser<JavaParserNode> {
    override fun parseInputStream(content: InputStream): JavaParserNode {
        val tree = StaticJavaParser.parse(content)
        return JavaParserNode(tree, null)
    }
}