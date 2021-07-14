package astminer.parse.antlr.python

import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PythonFunctionSplitterTest {
    companion object {
        const val FILE_PATH = "src/test/resources/methodSplitting/testMethodSplitting.py"
        const val N_FUNCTIONS = 17
        val functionSplitter = PythonFunctionSplitter()
        val parser = PythonParser()
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
    fun testFunctionNotInClass() {
        val functionClass = functionInfos.find { it.name == "fun_with_no_class"  }
        assertNotNull(functionClass)
        assertNull(functionClass.enclosingElement)
    }

    @Test
    fun testFunctionInClass() {
        val functionClass = functionInfos.find { it.name == "fun_in_class1"  }
        assertNotNull(functionClass)
        assertEquals(EnclosingElementType.Class, functionClass.enclosingElement?.type)
        assertEquals( "Class1", functionClass.enclosingElement?.name)
    }

    @Test
    fun testFunctionInNestedClass() {
        val functionClass = functionInfos.find { it.name == "fun_in_class2"  }
        assertNotNull(functionClass)
        assertEquals(EnclosingElementType.Class, functionClass.enclosingElement?.type)
        assertEquals( "Class2", functionClass.enclosingElement?.name)
    }

    @Test
    fun testNoParameters() {
        val functionNoParameters = functionInfos.find { it.name == "function_with_no_parameters"  }
        assertNotNull(functionNoParameters)
        assertEquals(0, functionNoParameters.parameters.size)
    }

    @Test
    fun testOneParameter() {
        val functionOneParameter = functionInfos.find { it.name == "function_with_one_parameter"  }
        assertNotNull(functionOneParameter)
        assertEquals(1, functionOneParameter.parameters.size)
        val parameter = functionOneParameter.parameters[0]
        assertEquals("p1", parameter.name)
    }

    @Test
    fun testOneTypedParameter() {
        val functionOneTypedParameter = functionInfos.find { it.name == "function_with_one_typed_parameter" }
        assertNotNull(functionOneTypedParameter)
        assertEquals(1, functionOneTypedParameter.parameters.size)
        val parameter = functionOneTypedParameter.parameters[0]
        assertEquals("p1", parameter.name)
        assertEquals("int", parameter.type)
    }

    @Test
    fun functionWithComplexParameter() {
        val functionOneTypedParameter = functionInfos.find { it.name == "function_with_complex_parameter" }
        assertNotNull(functionOneTypedParameter)
        assertEquals(1, functionOneTypedParameter.parameters.size)
        val parameter = functionOneTypedParameter.parameters[0]
        assertEquals("p1", parameter.name)
        assertEquals("List[int]", parameter.type)
    }

    @Test
    fun testThreeParameters() {
        val functionThreeParameters = functionInfos.find { it.name == "function_with_three_parameters"  }
        assertNotNull(functionThreeParameters)
        assertEquals(3, functionThreeParameters.parameters.size)
        val parameters = functionThreeParameters.parameters
        assertEquals("p1", parameters[0].name)

        assertEquals("p2", parameters[1].name)

        assertEquals("p3", parameters[2].name)
        assertEquals("int", parameters[2].type)
    }

    @Test
    fun testParameterInClass() {
        val functionOneParameter = functionInfos.find { it.name == "fun_with_parameter_in_class"  }
        assertNotNull(functionOneParameter)
        assertEquals(2, functionOneParameter.parameters.size)
        val parameter = functionOneParameter.parameters[1]
        assertEquals("p1", parameter.name)
    }

    @Test
    fun testTypedParameterInClass() {
        val functionOneTypedParameter = functionInfos.find { it.name == "fun_with_typed_parameter_in_class" }
        assertNotNull(functionOneTypedParameter)
        assertEquals(2, functionOneTypedParameter.parameters.size)
        val parameter = functionOneTypedParameter.parameters[1]
        assertEquals("p1", parameter.name)
        assertEquals("int", parameter.type)
    }

    @Test
    fun testEnclosingFunction() {
        val functionInsideFunction = functionInfos.find { it.name == "function_inside_function" }
        assertNotNull(functionInsideFunction)
        val enclosingElement = functionInsideFunction.enclosingElement

        assertNotNull(enclosingElement)
        assertEquals("function_containing_function", enclosingElement.name)
        assertEquals(EnclosingElementType.Function, enclosingElement.type)
    }

    @Test
    fun testEnclosingMethod() {
        val functionInsideMethod =  functionInfos.find { it.name == "function_inside_method" }
        assertNotNull(functionInsideMethod)
        val enclosingElement = functionInsideMethod.enclosingElement

        assertNotNull(enclosingElement)
        assertEquals("some_method", enclosingElement.name)
        assertEquals(EnclosingElementType.Method, enclosingElement.type)
    }

    @Test
    fun testEnclosingFunctionInsideMethod() {
        val funInsideFunInsideMethod =  functionInfos.find { it.name == "fun_inside_fun_inside_method" }
        assertNotNull(funInsideFunInsideMethod)
        val enclosingElement = funInsideFunInsideMethod.enclosingElement

        assertNotNull(enclosingElement)
        assertEquals("second_function_inside_method", enclosingElement.name)
        assertEquals(EnclosingElementType.Function, enclosingElement.type)
    }
}