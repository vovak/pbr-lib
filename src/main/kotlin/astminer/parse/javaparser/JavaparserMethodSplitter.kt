package astminer.parse.javaparser

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter

class JavaparserMethodSplitter: TreeFunctionSplitter<JavaParserNode> {
    val METHOD_DECLARATION = "Mth"

    override fun splitIntoFunctions(root: JavaParserNode, filePath: String): Collection<FunctionInfo<JavaParserNode>> {
        val methods = mutableListOf<FunctionInfo<JavaParserNode>>()
        for (methodRoot in root.preOrder().filter { it.typeLabel == METHOD_DECLARATION }) {
            try {
                methods.add(JavaparserFunctionInfo(methodRoot, filePath))
            } catch (e: IllegalStateException) { }
        }
        return methods
    }
}