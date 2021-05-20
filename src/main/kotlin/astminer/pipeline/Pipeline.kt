package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.config.*
import astminer.parse.getHandlerFactory
import astminer.storage.Storage
import astminer.storage.TokenProcessor
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.path.Code2VecPathStorage
import java.io.File

class Pipeline(private val config: PipelineConfig) {
    private val inputDirectory = File(config.inputDir)
    private val outputDirectory = File(config.outputDir)

    private val branch = when (config) {
        is FilePipelineConfig -> FilePipelineBranch(config)
        is FunctionPipelineConfig -> FunctionPipelineBranch(config)
    }

    private fun createStorageDirectory(extension: String): File {
        val outputDirectoryForExtension = outputDirectory.resolve(extension)
        outputDirectoryForExtension.mkdir()
        return outputDirectoryForExtension
    }

    private fun createStorage(extension: String): Storage = with(config.storage) {
        val storagePath = createStorageDirectory(extension).path

        // TODO: should be removed this later and be implemented like filters and problems, once storage constructors have no side effects
        when (this) {
            is AstStorageConfig -> {
                val tokenProcessor = if (splitTokens) TokenProcessor.Split else TokenProcessor.Normalize
                when (format) {
                    AstStorageFormat.Csv -> CsvAstStorage(storagePath)
                    AstStorageFormat.Dot -> DotAstStorage(storagePath, tokenProcessor)
                }
            }
            is Code2VecPathStorageConfig -> {
                Code2VecPathStorage(storagePath, pathBasedStorageConfig)
            }
        }
    }

    fun run() {
        for (extension in config.parser.extensions) {
            val languageFactory = getHandlerFactory(extension, config.parser.type)

            val files = getProjectFilesWithExtension(inputDirectory, extension).asSequence()
            val labeledResults = files.map { languageFactory.createHandler(it) }.flatMap { branch.process(it) }

            createStorage(extension).use { storage ->
                storage.store(labeledResults.asIterable())
            }
        }
    }
}