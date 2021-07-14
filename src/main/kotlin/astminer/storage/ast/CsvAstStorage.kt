package astminer.storage.ast

import astminer.common.model.LabeledResult
import astminer.common.model.Node
import astminer.common.storage.RankedIncrementalIdStorage
import astminer.common.storage.dumpIdStorageToCsv
import astminer.common.storage.nodeTypeToCsvString
import astminer.common.storage.tokenToCsvString
import astminer.common.model.Storage
import java.io.File
import java.io.PrintWriter

/**
 * Stores multiple ASTs by their roots and saves them in .csv format.
 * Output consists of 3 .csv files: with node types, with tokens and with ASTs.
 */
class CsvAstStorage(override val outputDirectoryPath: String) : Storage {

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val nodeTypesMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()

    private val astsOutputStream: PrintWriter

    init {
        File(outputDirectoryPath).mkdirs()
        val astsFile = File("$outputDirectoryPath/asts.csv")
        astsFile.createNewFile()
        astsOutputStream = PrintWriter(astsFile)
        astsOutputStream.write("id,ast\n")
    }

    override fun store(labeledResult: LabeledResult<out Node>) {
        for (node in labeledResult.root.preOrder()) {
            tokensMap.record(node.token)
            nodeTypesMap.record(node.typeLabel)
        }
        dumpAst(labeledResult.root, labeledResult.label)
    }

    override fun close() {
        dumpTokenStorage(File("$outputDirectoryPath/tokens.csv"))
        dumpNodeTypesStorage(File("$outputDirectoryPath/node_types.csv"))

        astsOutputStream.close()
    }

    private fun dumpTokenStorage(file: File) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(nodeTypesMap, "node_type", nodeTypeToCsvString, file)
    }

    private fun dumpAst(root: Node, id: String) {
        astsOutputStream.write("$id,${astString(root)}\n")
    }

    internal fun astString(node: Node): String {
        return "${tokensMap.getId(node.token)} ${nodeTypesMap.getId(node.typeLabel)}{${
            node.children.joinToString(separator = "", transform = ::astString)
        }}"
    }
}
