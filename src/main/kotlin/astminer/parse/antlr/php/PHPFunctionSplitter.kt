package astminer.parse.antlr.php

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter
import astminer.parse.antlr.AntlrNode

class PHPFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    companion object {
        const val LAMBDA_TOKEN = "LambdaFn"
        const val FUNCTION_TOKEN = "Function_"
    }

    override fun splitIntoFunctions(root: AntlrNode, filePath: String): Collection<FunctionInfo<AntlrNode>> {
        return root.preOrder().filter { it.typeLabel == LAMBDA_TOKEN || it.typeLabel == FUNCTION_TOKEN }
            .mapNotNull { node -> node.parent?.let {statement -> ANTLRPHPFunctionInfo(statement, filePath) } }
    }
}