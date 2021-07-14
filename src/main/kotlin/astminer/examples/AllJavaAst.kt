package astminer.examples

import astminer.config.*
import astminer.pipeline.Pipeline

// Retrieve ASTs from Java files, using a generated parser.
fun allJavaAsts() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples/",
        outputDir = "out_examples/allJavaAstsAntlr",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Java)),
        labelExtractor = FileNameExtractorConfig(),
        storage = CsvAstStorageConfig(),
    )

    Pipeline(config).run()
}
