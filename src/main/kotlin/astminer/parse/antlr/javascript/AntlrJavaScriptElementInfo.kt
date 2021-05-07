package astminer.parse.antlr.javascript

import astminer.common.model.*
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy

/**
Base class for describing JavaScript methods, functions or arrow functions.
 */
abstract class AntlrJavaScriptElementInfo(override val root: AntlrNode) : FunctionInfo<AntlrNode> {
    companion object {
        private val ENCLOSING_ELEMENT_NODES =
            listOf("functionDeclaration", "variableDeclaration", "classDeclaration", "methodDefinition")
        private const val ENCLOSING_ELEMENT_NAME_NODE = "Identifier"

        private const val SINGLE_PARAMETER_NODE = "formalParameterArg"
        private const val PARAMETER_NAME_NODE = "Identifier"
    }

    protected fun collectEnclosingElement(): EnclosingElement<AntlrNode>? {
        val enclosingElement = root.findEnclosingElementBy {
            it.containsLabelIn(ENCLOSING_ELEMENT_NODES)
        } ?: return null
        return EnclosingElement(
            type = getEnclosingElementType(enclosingElement),
            name = getEnclosingElementName(enclosingElement),
            root = enclosingElement
        )
    }

    private fun AntlrNode.containsLabelIn(labels: List<String>): Boolean {
        return decompressTypeLabel(getTypeLabel()).intersect(labels).isNotEmpty()
    }

    private fun getEnclosingElementName(enclosingRoot: AntlrNode?): String? {
        return enclosingRoot?.getChildren()?.firstOrNull {
            it.hasLastLabel(ENCLOSING_ELEMENT_NAME_NODE)
        }?.getToken()
    }

    private fun getEnclosingElementType(enclosingRoot: AntlrNode): EnclosingElementType {
        return when (decompressTypeLabel(enclosingRoot.getTypeLabel()).last()) {
            "functionDeclaration" -> EnclosingElementType.Function
            "classDeclaration" -> EnclosingElementType.Class
            "methodDefinition" -> EnclosingElementType.Method
            "variableDeclaration" -> EnclosingElementType.VariableDeclaration
            else -> throw IllegalStateException("Couldn't derive enclosing element type")
        }
    }

    protected fun collectParameters(): List<FunctionInfoParameter> {
        val parametersRoot = getParametersRoot()
        return when {
            //No parameters found
            parametersRoot == null -> emptyList()

            //Have only one parameter, which is indicated only by its name
            parametersRoot.hasLastLabel(PARAMETER_NAME_NODE) -> listOf(
                FunctionInfoParameter(name = parametersRoot.getToken(), type = null)
            )

            //Have many parameters or one indicated not only by it's name
            else -> parametersRoot.getItOrChildrenOfType(SINGLE_PARAMETER_NODE).map {
                val nameNode = it.getChildOfType(PARAMETER_NAME_NODE) ?: it
                FunctionInfoParameter(name = nameNode.getToken(), type = null)
            }
        }
    }

    abstract fun getParametersRoot(): AntlrNode?
}

class JavaScriptArrowInfo(override val root: AntlrNode) : AntlrJavaScriptElementInfo(root) {
    companion object {
        private const val ARROW_NAME_NODE = "Identifier"
        private const val ARROW_PARAMETER_NODE = "arrowFunctionParameters"
        private const val ARROW_PARAMETER_INNER_NODE = "formalParameterList"
    }

    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val nameNode: AntlrNode? = root.getChildOfType(ARROW_NAME_NODE)

    override fun getParametersRoot(): AntlrNode? {
        val parameterRoot = root.getChildOfType(ARROW_PARAMETER_NODE)
        return parameterRoot?.getChildOfType(ARROW_PARAMETER_INNER_NODE) ?: parameterRoot
    }
}

class JavaScriptMethodInfo(override val root: AntlrNode) : AntlrJavaScriptElementInfo(root) {
    companion object {
        private val METHOD_GETTERS_SETTERS = listOf("getter", "setter")
        private const val METHOD_NAME_NODE = "identifierName"
        private const val METHOD_PARAMETER_NODE = "formalParameterList"
    }

    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val nameNode: AntlrNode? = collectNameNode()

    private fun collectNameNode(): AntlrNode? {
        val methodNameParent = root.getChildren().firstOrNull {
            METHOD_GETTERS_SETTERS.contains(it.getTypeLabel())
        } ?: root

        return methodNameParent.getChildren().firstOrNull {
            decompressTypeLabel(it.getTypeLabel()).contains(METHOD_NAME_NODE)
        }
    }

    override fun getParametersRoot(): AntlrNode? = root.getChildOfType(METHOD_PARAMETER_NODE)
}

class JavaScriptFunctionInfo(override val root: AntlrNode) : AntlrJavaScriptElementInfo(root) {
    companion object {
        private const val FUNCTION_NAME_NODE = "Identifier"
        private const val FUNCTION_PARAMETER_NODE = "formalParameterList"
    }

    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val nameNode: AntlrNode? = root.getChildOfType(FUNCTION_NAME_NODE)

    override fun getParametersRoot(): AntlrNode? = root.getChildOfType(FUNCTION_PARAMETER_NODE)
}