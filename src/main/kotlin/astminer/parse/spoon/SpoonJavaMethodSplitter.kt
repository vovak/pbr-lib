package astminer.parse.spoon

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter

class SpoonJavaMethodSplitter: TreeFunctionSplitter<SpoonNode> {
    private val methodDeclaration = "MethodImpl"

    override fun splitIntoFunctions(root: SpoonNode, filePath: String): Collection<FunctionInfo<SpoonNode>> {
        return root.preOrder().filter { it.typeLabel == methodDeclaration }
            .mapNotNull { try {SpoonJavaFunctionInfo(it, filePath)} catch (e: IllegalStateException) {null} }
    }
}