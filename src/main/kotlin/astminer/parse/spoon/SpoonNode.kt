package astminer.parse.spoon

import astminer.common.model.Node
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.CtUnaryOperator
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtNamedElement
import spoon.reflect.reference.CtReference

class SpoonNode(el: CtElement,override val parent: SpoonNode?): Node() {
    val roleInParent : String? = el.roleInParent?.toString()

    // Turning Ct<Something> -> <Something>
    override val typeLabel = el.javaClass.simpleName.substring(startIndex = 2)

    override val children = run {el.directChildren.map { SpoonNode(it, this) }}.toMutableList()

    override val originalToken = getValue(el)

    fun getValue(element: CtElement): String {
        return when {
            element is CtNamedElement -> element.simpleName
            element is CtReference -> element.simpleName
            element is CtLiteral<*> -> element.value.toString()
            element is CtBinaryOperator<*> -> element.kind.toString()
            element is CtUnaryOperator<*> -> element.kind.toString()
            element.directChildren.size == 0 -> element.toString()
            else -> ""
        }
    }

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf {it.typeLabel == typeLabel}
    }

    override fun preOrder(): List<SpoonNode> = super.preOrder().map { it as SpoonNode }
    override fun postOrder(): List<SpoonNode> = super.postOrder().map { it as SpoonNode }

    fun getChildWithRole(role: String) : SpoonNode? = children.find { it.roleInParent == role }
    fun getChildrenWithRole(role: String) : List<SpoonNode> = children.filter { it.roleInParent == role }
}