package astminer.parse.antlr.python

import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.hasLastLabel


class PythonFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    private val methodNode = "funcdef"

    override fun splitIntoFunctions(root: AntlrNode, filePath: String): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            (it).hasLastLabel(methodNode)
        }
        return methodRoots.map { AntlrPythonFunctionInfo(it, filePath) }
    }
}
