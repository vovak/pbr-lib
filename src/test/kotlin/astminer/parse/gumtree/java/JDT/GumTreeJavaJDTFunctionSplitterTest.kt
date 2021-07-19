package astminer.parse.gumtree.java.JDT

import astminer.common.model.FunctionInfo
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.java.GumTreeJavaTest
import java.io.File

class GumTreeJavaJDTFunctionSplitterTest: GumTreeJavaTest {
    override fun createTree(filename: String): GumTreeNode =
        GumTreeJavaJDTParser().parseInputStream(File(filename).inputStream())

    override fun createAndSplitTree(filename: String): Collection<FunctionInfo<GumTreeNode>> =
        GumTreeJavaJDTFunctionSplitter().splitIntoFunctions(createTree(filename), filename)
}