package astminer.parse.antlr.java

import astminer.common.*
import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.hasLastLabel

class JavaMethodSplitter : TreeFunctionSplitter<AntlrNode> {
    private val methodNodeType = "methodDeclaration"

    override fun splitIntoFunctions(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            (it as AntlrNode).hasLastLabel(methodNodeType)
        }
        return methodRoots.map { AntlrJavaFunctionInfo(it as AntlrNode) }
    }
}