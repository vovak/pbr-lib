package astminer.pipeline.branch

/**
 * This exception is thrown when label extractor's granularity is implemented incorrectly.
 */
class IllegalLabelExtractorException(problemName: String?) :
    IllegalStateException("Unknown label extractor `${problemName ?: "anonymous"}`")

/**
 * This exception is thrown when the given filter is not implemented for the given granularity
 */
class IllegalFilterException(granularity: String, filterName: String?):
        IllegalStateException("Unknown filter `${filterName ?: "anonymous"}` for $granularity granularity")
