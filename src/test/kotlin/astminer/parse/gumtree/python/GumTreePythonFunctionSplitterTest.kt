package astminer.parse.gumtree.python

import astminer.checkExecutable
import astminer.common.model.FunctionInfo
import astminer.parse.gumtree.GumTreeNode
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GumTreePythonFunctionSplitterTest {
    private fun parse(filename: String): GumTreeNode =
        GumTreePythonParser().parseInputStream(File(filename).inputStream())

    private fun splitFunctions(filename: String): Collection<FunctionInfo<GumTreeNode>> =
        GumTreePythonFunctionSplitter().splitIntoFunctions(parse(filename), filename)

    private fun createPath(file: String) = "src/test/resources/gumTreeMethodSplitter/$file"

    @Before
    fun checkPythonParser() = Assume.assumeTrue(checkExecutable("pythonparser"))

    @Test
    fun methodsCountTest() {
        assertEquals(7, splitFunctions(createPath("1.py")).size)
        assertEquals(9, splitFunctions(createPath("2.py")).size)
        assertEquals(3, splitFunctions(createPath("3.py")).size)
        assertEquals(5, splitFunctions(createPath("4.py")).size)
    }

    @Test
    fun funcNamesTest() {
        val realNames = setOf(
            "no_args_func", "with_args_no_typed", "with_typed_args",
            "with_typed_return_no_args", "full_typed",
            "func_dif_args_typed_return", "complex_args_full_typed"
        )
        val functionInfos = splitFunctions(createPath("1.py"))
        val parsedNames = functionInfos.map { it.name }.toSet()
        assertEquals(realNames, parsedNames)
    }

    @Test
    fun methodInfoTest1TypedArgs() {
        val functionInfos = splitFunctions(createPath("1.py"))
        val functionInfo = functionInfos.firstOrNull { it.name == "complex_args_full_typed" }
        assertNotNull(functionInfo)
        with(functionInfo) {
            assertEquals("complex_args_full_typed", name)
            assertEquals(null, returnType)
            assertEquals(1, parameters.size)
            assertEquals(listOf("node"), parameters.map { it.name }.toList())
            assertEquals(listOf("JsonNodeType"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest2ManyArgs() {
        val functionInfos = splitFunctions(createPath("1.py"))
        val functionInfo = functionInfos.firstOrNull { it.name == "func_dif_args_typed_return" }
        assertNotNull(functionInfo)
        with(functionInfo) {
            assertEquals("func_dif_args_typed_return", name)
            assertEquals("Constant-int", returnType)
            assertEquals(6, parameters.size)
            assertEquals(listOf("a", "b", "c", "d", "e", "f"), parameters.map { it.name }.toList())
            assertEquals(emptyList(), parameters.mapNotNull { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest3EnclosingClass() {
        val functionInfos = splitFunctions(createPath("2.py"))
        val function = functionInfos.firstOrNull { it.name == "foo_typed" }
        assertNotNull(function)
        with(function) {
            assertEquals("foo_typed", name)
            assertEquals("A", enclosingElement?.name)
            assertEquals(null, returnType)
            assertEquals(3, parameters.size)
            assertEquals(listOf("self", "x", "y"), parameters.map { it.name }.toList())
            assertEquals(listOf(null, "int", "int"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest4EnclosingClass() {
        val functionInfos = splitFunctions(createPath("2.py"))
        val functionInfo = functionInfos.firstOrNull { it.name == "bar_typed" }
        assertNotNull(functionInfo)
        with(functionInfo) {
            assertEquals("bar_typed", name)
            assertEquals("C", enclosingElement?.name)
            assertEquals(null, returnType)
            assertEquals(2, parameters.size)
            assertEquals(listOf("self", "x"), parameters.map { it.name }.toList())
            assertEquals(listOf(null, "int"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest5AsyncDef() {
        val functionInfos = splitFunctions(createPath("3.py"))
        val functionInfo = functionInfos.firstOrNull { it.name == "async_schrecklich_typed" }
        assertNotNull(functionInfo)
        with(functionInfo) {
            assertEquals("async_schrecklich_typed", name)
            assertEquals("AsyncFunctionDef", root.typeLabel)
            assertEquals(null, enclosingElement?.name)
            assertEquals("Constant-int", returnType)
            assertEquals(4, parameters.size)
            assertEquals(listOf("event", "x", "args", "kwargs"), parameters.map { it.name }.toList())
            assertEquals(listOf("str", "int", null, null), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest6Doc() {
        val functionInfos = splitFunctions(createPath("3.py"))
        val functionInfo = functionInfos.firstOrNull { it.name == "async_simple_no_typed" }
        assertNotNull(functionInfo)
        with(functionInfo) {
            assertEquals("async_simple_no_typed", name)
            assertEquals("AsyncFunctionDef", root.typeLabel)
            assertEquals(null, enclosingElement?.name)
            assertEquals(
                "\n    async doc\n    ",
                root.getChildOfType("body")
                    ?.getChildOfType("Expr")
                    ?.getChildOfType("Constant-str")
                    ?.originalToken
            )
            assertEquals(4, parameters.size)
            assertEquals(
                listOf("gh", "original_issue", "branch", "backport_pr_number"),
                parameters.map { it.name }.toList()
            )
            assertEquals(listOf(null, null, null, null), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest7InnerFunc() {
        val functionInfos = splitFunctions(createPath("4.py"))
        val functionInfo = functionInfos.firstOrNull { it.name == "foo_2" }
        assertNotNull(functionInfo)
        with(functionInfo) {
            assertEquals("foo_2", name)
            assertEquals("foo_1", functionInfo.root.parent?.wrappedNode?.parent?.label)
            assertEquals(null, enclosingElement?.name)
            assertEquals("Constant-NoneType", returnType)
            assertEquals(1, parameters.size)
            assertEquals(listOf("c"), parameters.map { it.name }.toList())
            assertEquals(listOf(null), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest8InnerFunc() {
        val functionInfos = splitFunctions(createPath("4.py"))
        val functionInfo = functionInfos.firstOrNull { it.name == "bar_2" }
        assertNotNull(functionInfo)
        with(functionInfo) {
            assertEquals("bar_2", name)
            assertEquals("bar_1", functionInfo.root.parent?.wrappedNode?.parent?.label)
            assertEquals(null, enclosingElement?.name)
            assertEquals("Constant-int", returnType)
            assertEquals(2, parameters.size)
            assertEquals(listOf("d", "e"), parameters.map { it.name }.toList())
            assertEquals(listOf("int", "int"), parameters.map { it.type }.toList())
        }
    }
}
