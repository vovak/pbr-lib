package astminer.config

import astminer.common.model.Storage
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.ast.JsonAstStorage
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

/**
 * Config for storage that saved the results on the disk
 */
@Serializable
sealed class StorageConfig {
    abstract fun createStorage(outputDirectoryPath: String): Storage
}

/**
 * @see astminer.storage.ast.CsvAstStorage
 */
@Serializable
@SerialName("CsvAST")
class CsvAstStorageConfig : StorageConfig() {
    override fun createStorage(outputDirectoryPath: String) = CsvAstStorage(outputDirectoryPath)
}

/**
 * @see astminer.storage.ast.DotAstStorage
 */
@Serializable
@SerialName("DotAST")
class DotAstStorageConfig : StorageConfig() {
    override fun createStorage(outputDirectoryPath: String) = DotAstStorage(outputDirectoryPath)
}

/**
 * @see JsonAstStorage
 */
@Serializable
@SerialName("JsonAST")
class JsonAstStorageConfig : StorageConfig() {
    override fun createStorage(outputDirectoryPath: String) = JsonAstStorage(outputDirectoryPath)
}

/**
 * Config for [astminer.storage.path.Code2VecPathStorage]
 */
@Serializable
@SerialName("Code2vec")
data class Code2VecPathStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null,
) : StorageConfig() {
    @Transient
    private val pathBasedStorageConfig =
        PathBasedStorageConfig(maxPathLength, maxPathWidth, maxTokens, maxPaths, maxPathContextsPerEntity)

    override fun createStorage(outputDirectoryPath: String) =
        Code2VecPathStorage(outputDirectoryPath, pathBasedStorageConfig)
}
