package astminer.pipeline.branch

import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import astminer.common.model.LabeledResult

/**
 * PipelineBranch is a part of the pipeline that can be completely different depending on the granularity (pipeline type)
 * It accepts parsed files (LanguageHandler) and returns labeled results.
 */
interface PipelineBranch {
    /**
     * Extracts labeled results from LanguageHandler
     * May mutate the AST.
     * Should have no other side-effects
     */
    fun process(languageHandler: LanguageHandler<out Node>): List<LabeledResult<out Node>>
}
