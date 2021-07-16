package astminer.parse.javaparser

import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.BeforeTest

internal class JavaparserMethodSplitterTest {
    companion object {
        const val FILE_PATH = "src/test/resources/methodSplitting/testMethodSplitting.java"
        const val N_FUNCTIONS = 10
        val functionSplitter = JavaparserMethodSplitter()
        val parser = JavaParserParseWrapper()
    }

    var functionInfos: Collection<FunctionInfo<JavaParserNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree =  parser.parseInputStream(File(FILE_PATH).inputStream())
        assertNotNull(testTree)
        functionInfos = functionSplitter.splitIntoFunctions(testTree, FILE_PATH)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_FUNCTIONS, functionInfos.size, "Test file contains $N_FUNCTIONS methods")
    }

    @Test
    fun testNoParameters() {
        val methodNoParameters = functionInfos.find { it.name == "functionWithNoParameters"  }
        assertNotNull(methodNoParameters)
        assertEquals(0, methodNoParameters.parameters.size)
    }

    @Test
    fun testOneParameter() {
        val methodOneParameter = functionInfos.find { it.name == "functionWithOneParameter"  }
        assertNotNull(methodOneParameter)
        assertEquals(1, methodOneParameter.parameters.size)
        val parameter = methodOneParameter.parameters[0]
        assertEquals("p1", parameter.name)
        assertEquals("int", parameter.type)
    }

    @Test
    fun testThreeParameters() {
        val methodThreeParameters = functionInfos.find { it.name == "functionWithThreeParameters"  }
        assertNotNull(methodThreeParameters)
        assertEquals(3, methodThreeParameters.parameters.size)
        val methodTypes = listOf("Class", "String[][]", "int[]")
        for (i in 0 until 3) {
            val parameter = methodThreeParameters.parameters[i]
            assertEquals("p${i + 1}", parameter.name)
            assertEquals(methodTypes[i], parameter.type)
        }
    }

    @Test
    fun testWeirdArrayParameter() {
        val methodWeirdArrayParameter = functionInfos.find { it.name == "functionWithStrangeArrayParameter" }
        assertNotNull(methodWeirdArrayParameter)
        assertEquals(1, methodWeirdArrayParameter.parameters.size)
        val weirdParameter = methodWeirdArrayParameter.parameters[0]
        // TODO: consider how name and type should be extracted in this case
        assertEquals("arr[]", weirdParameter.name)
        assertEquals("int[]", weirdParameter.type)
    }

    @Test
    fun testFunctionInClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass1"  }
        assertNotNull(methodClass)
        assertEquals(EnclosingElementType.Class, methodClass.enclosingElement?.type)
        assertEquals( "Class1", methodClass.enclosingElement?.name)
    }

    @Test
    fun testFunctionInNestedClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass2"  }
        assertNotNull(methodClass)
        assertEquals(EnclosingElementType.Class, methodClass.enclosingElement?.type)
        assertEquals( "Class2", methodClass.enclosingElement?.name)
    }
}