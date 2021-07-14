package astminer.config

import kotlinx.serialization.Serializable

/**
 * Config which defines the pipeline
 * @see astminer.pipeline.Pipeline
 */
@Serializable
data class PipelineConfig(
    val inputDir: String,
    val outputDir: String,
    val parser: ParserConfig,
    val filters: List<FilterConfig> = emptyList(),
    val labelExtractor: LabelExtractorConfig,
    val storage: StorageConfig
)
