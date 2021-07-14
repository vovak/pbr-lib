package astminer.examples

import astminer.config.*
import astminer.pipeline.Pipeline


fun allPythonFiles() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples",
        outputDir = "out_examples/allPythonFiles",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Python)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )

    Pipeline(config).run()
}
