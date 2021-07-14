package astminer.examples

import astminer.common.model.LabeledResult
import astminer.parse.antlr.java.JavaFunctionSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

//Retrieve paths from Java files, using a generated parser.
fun allJavaFiles() {
    val inputDir = "src/test/resources/examples/"

    val outputDir = "out_examples/allJavaFilesAntlr"
    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    File(inputDir).forFilesWithSuffix("11.java") { file ->
        val node = JavaParser().parseInputStream(file.inputStream())
        node.prettyPrint()
        JavaFunctionSplitter().splitIntoFunctions(node, file.path).forEach {
            println(it.name)
            println(it.returnType)
            println(it.enclosingElement?.name)
            it.parameters.forEach { parameter ->
                println("${parameter.name} ${parameter.type}")
            }
        }
        storage.store(LabeledResult(node, file.path, file.path))
    }

    storage.close()
}
