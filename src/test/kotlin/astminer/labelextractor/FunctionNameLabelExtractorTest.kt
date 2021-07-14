package astminer.labelextractor

import astminer.common.DummyNode
import astminer.common.model.FunctionInfo
import astminer.common.model.LabeledResult
import astminer.common.model.Node
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FunctionNameLabelExtractorTest {
    companion object {
        private const val PATH = "random/folder/file.txt"
        private const val FUNCTION_NAME = "method"
    }

    lateinit var functionRoot: Node

    private val functionInfo: FunctionInfo<Node>
        get() = object : FunctionInfo<Node> {
            override val nameNode = functionRoot
            override val filePath = PATH
            override val root = functionRoot
        }

    @Before
    fun init() {
        val leafNodeWithRecursiveCall = DummyNode(FUNCTION_NAME)
        val emptyIntermediateNode = DummyNode("", mutableListOf(leafNodeWithRecursiveCall))
        functionRoot = DummyNode(FUNCTION_NAME, mutableListOf(emptyIntermediateNode))
    }

    @Test
    fun `test FunctionNameProblem extracts correct method name`() {
        val labeledResult = FunctionNameLabelExtractor.process(functionInfo)
        assertEquals(LabeledResult(functionRoot, FUNCTION_NAME, PATH), labeledResult)
    }

    @Test
    fun `test FunctionNameProblem hides function name node token with METHOD_NAME`() {
        FunctionNameLabelExtractor.process(functionInfo)
        assertEquals("METHOD_NAME", functionInfo.nameNode?.token)
    }

    @Test
    fun `test FunctionNameProblem hides function root token with METHOD_NAME if it is the name node`() {
        FunctionNameLabelExtractor.process(functionInfo)
        assertEquals("METHOD_NAME", functionInfo.root.token)
    }

    @Test
    fun `test function name problem should hide recursive call tokens with SELF`() {
        FunctionNameLabelExtractor.process(functionInfo)
        val recursiveCallNode = functionInfo.root.children.firstOrNull()?.children?.firstOrNull()
        assertEquals("SELF", recursiveCallNode?.token)
    }
}