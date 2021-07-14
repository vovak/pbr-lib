package astminer.parse.antlr.javascript

import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class JavaScriptFunctionSplitterTest {
    companion object {
        const val N_METHODS = 47
        const val testFilePath = "src/test/resources/methodSplitting/testMethodSplitting.js"
        val functionSplitter = JavaScriptFunctionSplitter()
        val parser = JavaScriptParser()
    }

    var functionInfos: Collection<FunctionInfo<AntlrNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree = parser.parseInputStream(File(testFilePath).inputStream())
        assertNotNull(testTree)
        functionInfos = functionSplitter.splitIntoFunctions(testTree, testFilePath)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_METHODS, functionInfos.size, "Test file contains $N_METHODS methods")
    }

    @Test
    fun testValidMethodInfo() {
        fun EnclosingElementType.getEnclosingElementType(): String {
            return when (this) {
                EnclosingElementType.Function -> "fun"
                EnclosingElementType.Class -> "class"
                EnclosingElementType.Method -> "method"
                EnclosingElementType.VariableDeclaration -> "var"
                else -> ""
            }
        }

        fun FunctionInfo<AntlrNode>.getJsonInfo(): String {
            return "info : {" +
                    "name : ${name}, " +
                    "args : ${parameters.joinToString(", ") { it.name }}, " +
                    "enclosing element : ${enclosingElement?.type?.getEnclosingElementType()}, " +
                    "enclosing element name : ${enclosingElement?.name}" +
                    "}"
        }

        val actualJsonInfos = functionInfos.map { it.getJsonInfo() }.sorted()

        val text = File(testFilePath).readText()
        val expectedJsonInfos = Regex("info : \\{.*\\}").findAll(text).toList().map { it.value }.sorted()

        assertEquals(expectedJsonInfos, actualJsonInfos)
    }
}