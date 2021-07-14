package astminer.pipeline

import astminer.config.*
import astminer.pipeline.util.verifyPathContextExtraction
import org.junit.Test
import java.io.File
import java.nio.file.Files

internal class Code2VecExtractionTest {
    private val testDataDir = File("src/test/resources")

    @Test
    fun `test code2vec path extraction from files generates correct folders and files`() {
        val extractedDataDir = Files.createTempDirectory("extractedData")

        val languages = listOf(FileExtension.Java, FileExtension.Python)

        val config = PipelineConfig(
            inputDir = testDataDir.path,
            outputDir = extractedDataDir.toAbsolutePath().toString(),
            parser = ParserConfig(ParserType.Antlr, languages),
            labelExtractor = FileNameExtractorConfig(),
            storage = Code2VecPathStorageConfig(8, 3)
        )
        Pipeline(config).run()

        verifyPathContextExtraction(extractedDataDir.toFile(), languages.map { it.fileExtension }, false)
    }
}
