package astminer.pipeline.branch

import astminer.common.model.*
import astminer.labelextractor.*


/**
 * PipelineBranch for pipeline with function-level granularity (FunctionPipelineConfig).
 * Extracts functions from the parsed files.
 * Then tests functions with filters, processes them and extracts labels from each function.
 */
class FunctionPipelineBranch(
    filters: List<Filter>,
    private val labelExtractor: FunctionLabelExtractor
) : PipelineBranch {

    private val filters: List<FunctionFilter> = filters.map { filter ->
        filter as? FunctionFilter
            ?: throw IllegalFilterException("function", filter::class.simpleName)
    }

    private fun passesThroughFilters(functionInfo: FunctionInfo<out Node>) =
        filters.all { filter -> filter.validate(functionInfo) }

    override fun process(languageHandler: LanguageHandler<out Node>): List<LabeledResult<out Node>> =
        languageHandler.splitIntoFunctions()
            .filter { functionInfo -> passesThroughFilters(functionInfo) }
            .mapNotNull { functionInfo -> labelExtractor.process(functionInfo) }
}
