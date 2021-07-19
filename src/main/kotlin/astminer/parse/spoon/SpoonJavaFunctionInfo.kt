package astminer.parse.spoon

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy

class SpoonJavaFunctionInfo(override val root: SpoonNode, override val filePath: String): FunctionInfo<SpoonNode> {
    companion object {
        const val PARAMETER_ROLE = "parameter"
        const val TYPE_ROLE = "type"
        const val CLASS_DECLARATION_TYPE = "ClassImpl"
    }

    override val nameNode: SpoonNode = root

    override val parameters: List<FunctionInfoParameter> =
        root.preOrder().filter { it.roleInParent == PARAMETER_ROLE }.map { assembleParameter(it) }

    private fun assembleParameter(parameterNode: SpoonNode): FunctionInfoParameter {
        val type = parameterNode.getChildWithRole(TYPE_ROLE)?.originalToken
        val name = parameterNode.originalToken
        return FunctionInfoParameter(name, type)
    }

    override val returnType: String? = root.getChildWithRole(TYPE_ROLE)?.originalToken

    override val enclosingElement: EnclosingElement<SpoonNode>? =
        root.findEnclosingElementBy { it.typeLabel == CLASS_DECLARATION_TYPE }?.assembleEnclosingClass()

    private fun SpoonNode.assembleEnclosingClass(): EnclosingElement<SpoonNode> {
        return EnclosingElement(
            type = EnclosingElementType.Class,
            name = this.originalToken,
            root = this
        )
    }
}
