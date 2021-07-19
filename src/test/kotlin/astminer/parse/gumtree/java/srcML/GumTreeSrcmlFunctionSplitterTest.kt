package astminer.parse.gumtree.java.srcML

import astminer.common.model.FunctionInfo
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.java.GumTreeJavaTest
import java.io.File

internal class GumTreeSrcmlFunctionSplitterTest: GumTreeJavaTest {
    override fun createTree(filename: String): GumTreeNode =
       GumTreeJavaSrcmlParser().parseFile(File(filename)).root

    override fun createAndSplitTree(filename: String): Collection<FunctionInfo<GumTreeNode>> =
        GumTreeSrcmlFunctionSplitter().splitIntoFunctions(createTree(filename), filename)
}