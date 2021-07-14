@file:JvmName("CppExample")

package astminer.examples

import astminer.config.*
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.pipeline.Pipeline
import java.io.File

// Retrieve paths from .cpp preprocessed files, using a fuzzyc2cpg parser.
fun allCppFiles() {
    val inputDir = File("src/test/resources/examples/cpp")
    val preprocessedDir = File("preprocessed")
    // TODO: preprocessing should once become a part of the pipeline
    val parser = FuzzyCppParser()
    parser.preprocessProject(inputDir, preprocessedDir)

    val config = PipelineConfig(
        inputDir = preprocessedDir.path,
        outputDir = "out_examples/allCppFiles",
        parser = ParserConfig(ParserType.Fuzzy, listOf(FileExtension.Cpp)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )

    Pipeline(config).run()
}
