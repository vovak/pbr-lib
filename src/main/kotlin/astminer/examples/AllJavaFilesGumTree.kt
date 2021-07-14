package astminer.examples

import astminer.config.*
import astminer.pipeline.Pipeline

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/gumTreeMethodSplitter/",
        outputDir = "out_examples/allJavaFilesGumTree",
        parser = ParserConfig(ParserType.GumTree, listOf(FileExtension.Java)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )
    Pipeline(config).run()
}
