package astminer.parse.antlr.javascript

import astminer.common.model.*
import astminer.common.preOrder
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.decompressTypeLabel

/**
 * Get all methods (in JavaScript there are divided into functions, arrow functions and methods) and information
 * about their names, enclosing elements and parameters.
 */
class JavaScriptFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    companion object {
        private const val METHOD_NODE = "methodDefinition"
        private const val ARROW_NODE = "ARROW"
        private const val FUNCTION_NODE = "Function"
    }

    override fun splitIntoFunctions(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        return root.preOrder().mapNotNull { node ->
            when {
                node.isArrowElement() -> JavaScriptArrowInfo(node as AntlrNode)
                node.isFunctionElement() -> JavaScriptFunctionInfo(node as AntlrNode)
                node.isMethodElement() -> JavaScriptMethodInfo(node as AntlrNode)
                else -> null
            }
        }
    }

    private fun Node.isArrowElement() = this.getChildOfType(ARROW_NODE) != null
    private fun Node.isFunctionElement() = this.getChildOfType(FUNCTION_NODE) != null
    private fun Node.isMethodElement() = decompressTypeLabel(this.getTypeLabel()).last() == METHOD_NODE
}