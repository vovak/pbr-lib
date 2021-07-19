package astminer.parse.gumtree.java.srcML

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter
import astminer.parse.gumtree.GumTreeNode

class GumTreeSrcmlFunctionSplitter : TreeFunctionSplitter<GumTreeNode> {
    val FUNCTION_TYPE = "function"

    override fun splitIntoFunctions(root: GumTreeNode, filePath: String): Collection<FunctionInfo<GumTreeNode>> {
        return root.preOrder().filter { it.typeLabel == FUNCTION_TYPE }
            .mapNotNull { try {GumTreeJavaSrcmlFunctionInfo(it, filePath)} catch (e: IllegalStateException) {null} }
    }
}