package astminer.filters

import astminer.common.createBamboo
import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FunctionFiltersTest {
    @Test
    fun `test ModifierFilter should exclude function if it has the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("b", "c")
        }
        assertFalse { ModifierFilter(excludedModifiers).validate(functionInfo) }
    }

    @Test
    fun `test ModifierFilter should not exclude function if it does not have the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("c", "d")
        }
        assertTrue { ModifierFilter(excludedModifiers).validate(functionInfo) }
    }

    @Test
    fun `test AnnotationFilter should exclude function if it has the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val annotations: List<String> = listOf("a", "c")
        }
        assertFalse { AnnotationFilter(excludedModifiers).validate(functionInfo) }
    }

    @Test
    fun `test AnnotationFilter should not exclude function if it does not have the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val annotations: List<String> = listOf("y", "x")
        }
        assertTrue { AnnotationFilter(excludedModifiers).validate(functionInfo) }
    }

    @Test
    fun `test ConstructorFilter should exclude constructor functions`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val isConstructor = true
        }
        assertFalse { ConstructorFilter.validate(functionInfo) }
    }

    @Test
    fun `test ConstructorFilter should not exclude non-constructor functions`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val isConstructor = false
        }
        assertTrue { ConstructorFilter.validate(functionInfo) }
    }

    @Test
    fun `test FunctionNameWordsNumberFilter for 50 should exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionNameWordsNumberFilter(50).validate(functionInfo) }
    }

    @Test
    fun `test WordsNumberFilter for 101 should not exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertTrue { FunctionNameWordsNumberFilter(101).validate(functionInfo) }
    }

    @Test
    fun `test WordsNumberFilter for 50 should exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val root = AntlrNode("", null,  "Word".repeat(100))
        }
        assertFalse { WordsNumberFilter(50).validate(functionInfo) }
    }

    @Test
    fun `test WordsNumberFilter for 2 should exclude function that has a child of 3 words`() {
        val root = AntlrNode("", null, "word")
        val child = AntlrNode("", root, "wordWordWord")
        root.replaceChildren(listOf(child))

        val functionInfo = object : FunctionInfo<Node> {
            override val root = root
        }
        assertFalse { WordsNumberFilter(2).validate(functionInfo) }
    }

    @Test
    fun `test TreeSizeFilter for 100 should exclude bamboo of length 101`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val root = createBamboo(101)
        }
        assertFalse { TreeSizeFilter(maxSize = 100).validate(functionInfo) }
    }

    @Test
    fun `test TreeSizeFilter for 10 should not exclude bamboo of length 5`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val root = createBamboo(5)
        }
        assertTrue { TreeSizeFilter(maxSize = 10).validate(functionInfo) }
    }

    @Test
    fun `test TreeSizeFilter for minSize 100 should exclude bamboo of length 5`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val root = createBamboo(5)
        }
        assertFalse { TreeSizeFilter(minSize = 100).validate(functionInfo) }
    }

    @Test
    fun `test TreeSizeFilter for (10, 100) should not exclude bambo of size 50 `() {
        val functionInfo = object : FunctionInfo<Node> {
            override val root = createBamboo(50)
        }
        assertTrue { TreeSizeFilter(10, 100).validate(functionInfo) }
    }
}