package astminer.parse.gumtree.python

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy
import astminer.parse.gumtree.GumTreeNode

class GumTreePythonFunctionInfo(override val root: GumTreeNode, override val filePath: String) : FunctionInfo<GumTreeNode> {
    companion object {
        private object TypeLabels {
            const val classDefinition = "ClassDef"
            const val functionDefinition = "FunctionDef"
            const val asyncFunctionDefinition = "AsyncFunctionDef"
            const val nameLoad = "Name_Load"
            const val posOnlyArgs = "posonlyargs"
            const val kwOnlyArgs = "kwonlyargs"
            const val arguments = "arguments"
            const val vararg = "vararg"
            const val kwarg = "kwarg"
            const val args = "args"
            const val arg = "arg"

            const val body = "body"
            const val returnTypeLabel = "Return"
            const val passTypeLabel = "Pass"
            const val constantType = "Constant-"

            val methodDefinitions = listOf(functionDefinition, asyncFunctionDefinition)
            val funcArgsTypesNodes = listOf(args, posOnlyArgs, kwOnlyArgs)
        }
    }

    override val nameNode: GumTreeNode = root
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val enclosingElement: EnclosingElement<GumTreeNode>? = collectEnclosingClass()
    override val returnType: String? = getElementType(root)?.typeLabel

    private fun getElementType(node: GumTreeNode): GumTreeNode? {
        if (node.typeLabel == TypeLabels.arg) {
            return node.getChildOfType(TypeLabels.nameLoad)
        }
        // if return statement has "Constant-`Type`" return value => function type is `Type`
        if (TypeLabels.methodDefinitions.contains(node.typeLabel)) {
            return node.getChildOfType(TypeLabels.body)?.getChildOfType(TypeLabels.returnTypeLabel)?.let {
                it.children.firstOrNull { child ->
                    child.typeLabel.startsWith(TypeLabels.constantType)
                }
            }
        }
        return null
    }

    private fun collectEnclosingClass(): EnclosingElement<GumTreeNode>? {
        val enclosing = findEnclosingClass() ?: return null
        return EnclosingElement(
            type = EnclosingElementType.Class,
            name = enclosing.originalToken,
            root = enclosing
        )
    }

    private fun findEnclosingClass(): GumTreeNode? {
        return root.findEnclosingElementBy { it.typeLabel == TypeLabels.classDefinition }
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        val arguments = root.getChildrenOfType(TypeLabels.arguments).flatMap { it.children }
        val params = arguments.flatMap { node ->
            when (node.typeLabel) {
                in TypeLabels.funcArgsTypesNodes -> node.children
                    .filter { it.typeLabel == TypeLabels.arg }
                TypeLabels.vararg, TypeLabels.kwarg -> listOf(node)
                else -> emptyList()
            }
        }
        return params.map { node->
            FunctionInfoParameter(
                name = node.originalToken,
                type = getElementType(node)?.originalToken
            )
        }
    }
}