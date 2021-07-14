package astminer.config

import astminer.common.model.LabelExtractor
import astminer.labelextractor.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class LabelExtractorConfig {
    abstract val labelExtractorImpl: LabelExtractor
}

/**
 * @see FileNameExtractor
 */
@Serializable
@SerialName("file name")
class FileNameExtractorConfig : LabelExtractorConfig() {
    @Transient
    override val labelExtractorImpl = FileNameExtractor
}

/**
 * @see FolderNameExtractor
 */
@Serializable
@SerialName("folder name")
class FolderNameExtractorConfig : LabelExtractorConfig() {
    @Transient
    override val labelExtractorImpl = FolderNameExtractor
}

/**
 * @see FunctionNameLabelExtractor
 */
@Serializable
@SerialName("function name")
class FunctionNameExtractorConfig : LabelExtractorConfig() {
    @Transient
    override val labelExtractorImpl = FunctionNameLabelExtractor
}
