package astminer.examples

import astminer.common.model.LabeledResult
import astminer.common.model.FunctionInfo
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.parse.gumtree.java.GumTreeJavaFunctionSplitter
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File


private fun getCsvFriendlyMethodId(functionInfo: FunctionInfo<GumTreeNode>): String {
    val className = functionInfo.enclosingElement?.name ?: ""
    val methodName = functionInfo.name
    val parameterTypes = functionInfo.parameters.joinToString("|") { it.name }
    return "$className.$methodName($parameterTypes)"
}


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun allJavaMethods() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter"

    val outputDir = "out_examples/allJavaMethods"
    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    File(inputDir).forFilesWithSuffix(".java") { file ->
        //parse file
        val fileNode = GumTreeJavaParser().parseInputStream(file.inputStream())

        //extract method nodes
        val methodNodes = GumTreeJavaFunctionSplitter().splitIntoFunctions(fileNode, file.path)

        methodNodes.forEach { methodInfo ->
            //Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodInfo)}"
            val labelingResult = LabeledResult(fileNode, entityId, file.path)
            storage.store(labelingResult)
        }
    }

    storage.close()
}
