package astminer.parse.gumtree

import astminer.common.model.ParseResult
import astminer.common.model.HandlerFactory
import astminer.common.model.LanguageHandler
import astminer.parse.gumtree.java.JDT.GumTreeJavaJDTParser
import astminer.parse.gumtree.java.JDT.GumTreeJavaJDTFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonParser
import java.io.File

object GumtreeJavaJDTHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreeNode> = GumtreeJavaJDTHandler(file)

    class GumtreeJavaJDTHandler(file: File) : LanguageHandler<GumTreeNode>() {
        override val splitter = GumTreeJavaJDTFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreeJavaJDTParser().parseFile(file)
    }
}

object GumtreePythonHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreeNode> = PythonGumTreeHandler(file)

    class PythonGumTreeHandler(file: File) :  LanguageHandler<GumTreeNode>() {
        override val splitter = GumTreePythonFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreePythonParser().parseFile(file)
    }
}