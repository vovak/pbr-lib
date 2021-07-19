package astminer.parse.gumtree.java.JDT

import astminer.common.model.*
import astminer.parse.gumtree.GumTreeNode

class GumTreeJavaJDTFunctionSplitter : TreeFunctionSplitter<GumTreeNode> {
    private val methodDeclaration = "MethodDeclaration"

    override fun splitIntoFunctions(root: GumTreeNode, filePath: String): Collection<FunctionInfo<GumTreeNode>> {
        val methodRoots = root.preOrder().filter { it.typeLabel == methodDeclaration }
        return methodRoots.map { GumTreeJavaJDTFunctionInfo(it, filePath) }
    }
}