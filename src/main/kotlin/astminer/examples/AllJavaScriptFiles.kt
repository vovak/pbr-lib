package astminer.examples

import astminer.config.*
import astminer.pipeline.Pipeline

fun allJavaScriptFiles() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples",
        outputDir = "out_examples/allJavaScriptFilesAntlr",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.JavaScript)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )

    Pipeline(config).run()
}
