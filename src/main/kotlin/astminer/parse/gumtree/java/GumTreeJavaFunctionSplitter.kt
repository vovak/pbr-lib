package astminer.parse.gumtree.java

import astminer.common.model.*
import astminer.common.preOrder
import astminer.parse.gumtree.GumTreeNode

class GumTreeJavaFunctionSplitter : TreeFunctionSplitter<GumTreeNode> {
    private val methodDeclaration = "MethodDeclaration"

    override fun splitIntoFunctions(root: GumTreeNode): Collection<FunctionInfo<GumTreeNode>> {
        val methodRoots = root.preOrder().filter { it.getTypeLabel() == methodDeclaration }
        return methodRoots.map { GumTreeJavaFunctionInfo(it as GumTreeNode) }
    }
}