package astminer.examples

import astminer.common.numberOfLines
import astminer.featureextraction.*
import astminer.parse.gumtree.java.JDT.GumTreeJavaJDTParser
import java.io.File


fun parseAndCollectFeatures() {
    val parser = GumTreeJavaJDTParser()
    val features : List<TreeFeature<Any>> = listOf(Depth, NumberOfNodes, BranchingFactor, CompressiblePathLengths, Tokens, NodeTypes)

    val folderInput = "./testData/featureextraction"
    val folderOutput = "out_examples/featureextraction"

    val storage = TreeFeatureValueStorage(",")
    storage.storeFeatures(features)

    File(folderInput).forFilesWithSuffix("java") { fileInput ->
        val fileName = fileInput.name
        val nol = numberOfLines(fileInput)

        val tree = ParsedTree(parser.className(), parser.parseInputStream(fileInput.inputStream()), fileName, nol)
        storage.storeParsedTree(tree)
    }

    storage.save(folderOutput)
}

fun main() {
    parseAndCollectFeatures()
}