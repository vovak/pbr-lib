package astminer.parse.gumtree.java

import astminer.common.model.*
import astminer.parse.gumtree.GumTreeNode

class GumTreeJavaFunctionSplitter : TreeFunctionSplitter<GumTreeNode> {
    private val methodDeclaration = "MethodDeclaration"

    override fun splitIntoFunctions(root: GumTreeNode, filePath: String): Collection<FunctionInfo<GumTreeNode>> {
        val methodRoots = root.preOrder().filter { it.typeLabel == methodDeclaration }
        return methodRoots.map { GumTreeJavaFunctionInfo(it, filePath) }
    }
}