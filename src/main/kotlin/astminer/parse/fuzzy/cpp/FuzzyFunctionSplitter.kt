package astminer.parse.fuzzy.cpp

import astminer.common.model.*

class FuzzyFunctionSplitter : TreeFunctionSplitter<FuzzyNode> {
    private val methodNode = "METHOD"

    override fun splitIntoFunctions(root: FuzzyNode, filePath: String): Collection<FunctionInfo<FuzzyNode>> {
        val methodRoots = root.preOrder().filter { it.typeLabel == methodNode }
        return methodRoots.map { FuzzyCppFunctionInfo(it, filePath) }
    }
}