package astminer.pipeline

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.config.FilePipelineConfig
import astminer.config.FunctionPipelineConfig
import astminer.config.PipelineConfig

/**
 * This function must have no side effects
 */
fun getPipeline(pipelineConfig: PipelineConfig): Pipeline<*> {
    return when (pipelineConfig) {
        is FilePipelineConfig -> getFilePipeline(pipelineConfig)
        is FunctionPipelineConfig -> getFunctionPipeline(pipelineConfig)
    }
}

private fun getFilePipeline(filePipelineConfig: FilePipelineConfig): Pipeline<ParseResult<out Node>> =
    with(filePipelineConfig) {
        val frontend = FilePipelineFrontend(inputDir, parser.type, parser.extensions)
        val storageCreator = StorageCreator(storage, outputDir)
        Pipeline(frontend, filters, problem, emptyList(), storageCreator)
    }

private fun getFunctionPipeline(functionPipelineConfig: FunctionPipelineConfig): Pipeline<FunctionInfo<out Node>> =
    with(functionPipelineConfig) {
        val frontend = FunctionPipelineFrontend(inputDir, parser.type, parser.extensions)
        val storageCreator = StorageCreator(storage, outputDir)
        Pipeline(frontend, filters, problem, emptyList(), storageCreator)
    }