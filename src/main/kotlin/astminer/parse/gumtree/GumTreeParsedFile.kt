package astminer.parse.gumtree

import astminer.common.model.ParseResult
import astminer.common.model.ParsedFileFactory
import astminer.common.model.ParsedFile
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.parse.gumtree.java.GumTreeJavaFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonParser
import java.io.File

object GumtreeJavaParsedFileFactory : ParsedFileFactory {
    override fun createHandler(file: File): ParsedFile<GumTreeNode> = JavaGumtreeHandler(file)

    class JavaGumtreeHandler(file: File) : ParsedFile<GumTreeNode>() {
        override val splitter = GumTreeJavaFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreeJavaParser().parseFile(file)
    }
}

object GumtreePythonParsedFileFactory : ParsedFileFactory {
    override fun createHandler(file: File): ParsedFile<GumTreeNode> = PythonGumTreeHandler(file)

    class PythonGumTreeHandler(file: File) :  ParsedFile<GumTreeNode>() {
        override val splitter = GumTreePythonFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreePythonParser().parseFile(file)
    }
}