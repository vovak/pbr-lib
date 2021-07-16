package astminer.parse.javaparser

import astminer.common.model.*
import java.io.File

class JavaParserParsedFile(file: File): LanguageHandler<JavaParserNode>() {
    override val parseResult: ParseResult<JavaParserNode> = JavaParserParseWrapper().parseFile(file)
    override val splitter: TreeFunctionSplitter<JavaParserNode> = JavaparserMethodSplitter()
}

object JavaParserParsedFileFactory: HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<out Node> = JavaParserParsedFile(file)
}