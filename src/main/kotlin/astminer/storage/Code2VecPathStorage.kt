package astminer.storage

import astminer.common.model.PathContextId

class Code2VecPathStorage(
    outputDirectoryPath: String,
    config: CountingPathStorageConfig,
    tokenProcessor: TokenProcessor = code2vecTokenProcessor
) :
    CountingPathStorage(outputDirectoryPath, config, tokenProcessor) {

    override fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String {
        val joinedPathContexts = pathContextIds.joinToString(" ") { pathContextId ->
            "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
        }
        return "$label $joinedPathContexts"
    }
}