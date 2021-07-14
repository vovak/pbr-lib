package astminer.parse.antlr.java

import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

class JavaFunctionSplitterTest {
    companion object {
        const val FILE_PATH = "src/test/resources/methodSplitting/testMethodSplitting.java"
        const val N_FUNCTIONS =10
        val functionSplitter = JavaFunctionSplitter()
        val parser = JavaParser()
    }

    var functionInfos: Collection<FunctionInfo<AntlrNode>> = listOf()

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
    fun testReturnVoid() {
        val methodVoid = functionInfos.find { it.name == "functionReturningVoid"  }
        assertNotNull(methodVoid)
        assertEquals( "void", methodVoid.returnType)
    }

    @Test
    fun testReturnInt() {
        val methodInt = functionInfos.find { it.name == "functionReturningInt"  }
        assertNotNull(methodInt)
        assertEquals( "int", methodInt.returnType)
    }

    @Test
    fun testReturnStrings() {
        val methodStrings = functionInfos.find { it.name == "functionReturningStrings"  }
        assertNotNull(methodStrings)
        assertEquals( "String[]", methodStrings.returnType)
    }

    @Test
    fun testReturnClass() {
        val methodClass = functionInfos.find { it.name == "functionReturningClass"  }
        assertNotNull(methodClass)
        assertEquals( "Class1", methodClass.returnType)
    }

    @Test
    fun testFunctionInClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass1"  }
        assertNotNull(methodClass)
        assertEquals( "Class1", methodClass.enclosingElement?.name)
    }

    @Test
    fun testFunctionInNestedClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass2"  }
        assertNotNull(methodClass)
        assertEquals( "Class2", methodClass.enclosingElement?.name)
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
        assertEquals(weirdParameter.name, "arr[]")
        assertEquals(weirdParameter.type, "int")
    }
}