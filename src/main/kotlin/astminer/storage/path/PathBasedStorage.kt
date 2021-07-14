package astminer.storage.path

import astminer.common.model.LabeledResult
import astminer.common.model.*
import astminer.common.storage.*
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import astminer.common.model.Storage
import java.io.File
import java.io.PrintWriter

// TODO: finish the documentation
/**
 * Config for CountingPathStorage which contains all hyperparameters for path extraction.
 * @property maxPathLength The maximum length of a single path (based on the formal math definition of path length)
 * @property maxPathWidth The maximum width of a single path (based on the formal math definition of path width)
 * @property maxTokens ??
 * @property maxPaths ??
 * @property maxPathContextsPerEntity The maximum number of path contexts that should be extracted from LabeledParseResult.
 * In other words, the maximum number of path contexts to save from each file/method (depending on granularity)
 */
data class PathBasedStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null
)

/**
 * Base class for all path storages. Extracts paths from given LabellingResult and stores it in a specified format.
 * @property outputDirectoryPath The path to the output directory.
 * @property config The config that contains hyperparameters for path extraction.
 */
abstract class PathBasedStorage(
    final override val outputDirectoryPath: String,
    private val config: PathBasedStorageConfig,
) : Storage {

    private val pathMiner = PathMiner(PathRetrievalSettings(config.maxPathLength, config.maxPathWidth))

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val orientedNodeTypesMap: RankedIncrementalIdStorage<OrientedNodeType> = RankedIncrementalIdStorage()
    private val pathsMap: RankedIncrementalIdStorage<List<Long>> = RankedIncrementalIdStorage()

    private val pathsFile: File
    private val labeledPathContextIdsWriter: PrintWriter

    init {
        File(outputDirectoryPath).mkdirs()
        pathsFile = File("$outputDirectoryPath/path_contexts.csv")
        pathsFile.createNewFile()
        labeledPathContextIdsWriter = PrintWriter(pathsFile)
    }

    abstract fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String

    private fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<String>) {
        val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
            val isNumberOfTokensValid = config.maxTokens == null ||
                    tokensMap.getIdRank(it.startTokenId) <= config.maxTokens &&
                    tokensMap.getIdRank(it.endTokenId) <= config.maxTokens
            val isNumberOfPathsValid = config.maxPaths == null || pathsMap.getIdRank(it.pathId) <= config.maxPaths

            isNumberOfTokensValid && isNumberOfPathsValid
        }

        labeledPathContextIdsWriter.println(pathContextIdsToString(pathContextIdsString, labeledPathContextIds.label))
    }

    private fun storePathContext(pathContext: PathContext): PathContextId {
        val startTokenId = tokensMap.record(pathContext.startToken)
        val endTokenId = tokensMap.record(pathContext.endToken)
        val orientedNodesIds = pathContext.orientedNodeTypes.map { orientedNodeTypesMap.record(it) }
        val pathId = pathsMap.record(orientedNodesIds)
        return PathContextId(startTokenId, pathId, endTokenId)
    }

    private fun retrievePaths(node: Node) = if (config.maxPathContextsPerEntity != null) {
        pathMiner.retrievePaths(node).take(config.maxPathContextsPerEntity)
    } else {
        pathMiner.retrievePaths(node)
    }

    private fun retrieveLabeledPathContexts(labeledResult: LabeledResult<out Node>): LabeledPathContexts<String> {
        val paths = retrievePaths(labeledResult.root)
        return LabeledPathContexts(labeledResult.label, paths.map { astPath ->
            toPathContext(astPath) { it.token.replace("\n", "\\n") }
        })
    }

    /**
     * Extract paths from [labeledResult] and store them in the specified format.
     */
    override fun store(labeledResult: LabeledResult<out Node>) {
        val labeledPathContexts = retrieveLabeledPathContexts(labeledResult)
        val labeledPathContextIds = LabeledPathContextIds(
            labeledPathContexts.label,
            labeledPathContexts.pathContexts.map { storePathContext(it) }
        )
        dumpPathContexts(labeledPathContextIds)
    }

    override fun close() {
        dumpIdStorageToCsv(
            tokensMap,
            "token",
            tokenToCsvString,
            File("$outputDirectoryPath/tokens.csv"),
            config.maxTokens
        )
        dumpIdStorageToCsv(
            orientedNodeTypesMap,
            "node_type",
            orientedNodeToCsvString,
            File("$outputDirectoryPath/node_types.csv")
        )
        dumpIdStorageToCsv(
            pathsMap,
            "path",
            pathToCsvString,
            File("$outputDirectoryPath/paths.csv"),
            config.maxPaths
        )

        labeledPathContextIdsWriter.close()
    }
}
