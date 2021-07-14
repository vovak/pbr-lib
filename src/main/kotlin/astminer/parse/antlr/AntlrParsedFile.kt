package astminer.parse.antlr

import astminer.common.model.*
import astminer.parse.antlr.java.JavaFunctionSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.javascript.JavaScriptFunctionSplitter
import astminer.parse.antlr.javascript.JavaScriptParser
import astminer.parse.antlr.php.PHPFunctionSplitter
import astminer.parse.antlr.php.PHPParser
import astminer.parse.antlr.python.PythonFunctionSplitter
import astminer.parse.antlr.python.PythonParser
import java.io.File

object AntlrJavaParsedFileFactory : ParsedFileFactory {
    override fun createHandler(file: File) = AntlrJavaHandler(file)

    class AntlrJavaHandler(file: File) : ParsedFile<AntlrNode>() {
        override val parseResult: ParseResult<AntlrNode> = JavaParser().parseFile(file)
        override val splitter = JavaFunctionSplitter()
    }
}

object AntlrPythonParsedFileFactory : ParsedFileFactory {
    override fun createHandler(file: File) = AntlrPythonHandler(file)

    class AntlrPythonHandler(file: File) : ParsedFile<AntlrNode>() {
        override val parseResult: ParseResult<AntlrNode> = PythonParser().parseFile(file)
        override val splitter = PythonFunctionSplitter()
    }
}

object AntlrJavascriptParsedFileFactory : ParsedFileFactory {
    override fun createHandler(file: File) = AntlrJavascriptHandler(file)

    class AntlrJavascriptHandler(file: File) : ParsedFile<AntlrNode>() {
        override val parseResult: ParseResult<AntlrNode> = JavaScriptParser().parseFile(file)
        override val splitter = JavaScriptFunctionSplitter()
    }
}

object AntlrPHPParsedFileFactory: ParsedFileFactory {
    override fun createHandler(file: File): ParsedFile<out Node> = AntlrPHPHandler(file)

    class AntlrPHPHandler(file: File): ParsedFile<AntlrNode>() {
        override val parseResult: ParseResult<AntlrNode> = PHPParser().parseFile(file)
        override val splitter: TreeFunctionSplitter<AntlrNode> = PHPFunctionSplitter()
    }
}