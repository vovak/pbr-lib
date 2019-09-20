package cli

import astminer.ast.CsvAstStorage
import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import java.io.File


class ProjectParser : CliktCommand() {

    /**
     * @param parser class that implements parsing
     * @param extension file extension to choose files for parsing
     */
    private data class SupportedLanguage(val parser: Parser<out Node>, val extension: String)

    /**
     * List of supported language extensions and corresponding parsers.
     */
    private val supportedLanguages = listOf(
        SupportedLanguage(JavaParser(), "java"),
        SupportedLanguage(FuzzyCppParser(), "c"),
        SupportedLanguage(FuzzyCppParser(), "cpp"),
        SupportedLanguage(PythonParser(), "py")
    )

    val extensions: List<String> by option(
        "--lang",
        help = "File extensions that will be parsed"
    ).split(",").default(supportedLanguages.map { it.extension })

    val projectRoot: String by option(
        "--project",
        help = "Path to the project that will be parsed"
    ).required()

    val outputDirName: String by option(
        "--output",
        help = "Path to directory where the output will be stored"
    ).required()

    private fun getParser(extension: String): Parser<out Node> {
        for (language in supportedLanguages) {
            if (extension == language.extension) {
                return language.parser
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
    }


    private fun parsing() {
        val outputDir = File(outputDirName)
        for (extension in extensions) {
            // Choose type of storage
            val storage = CsvAstStorage()
            val parser = getParser(extension)
            val roots = parser.parseWithExtension(File(projectRoot), extension)
            roots.forEach { parseResult ->
                val root = parseResult.root
                val filePath = parseResult.filePath
                root?.apply {
                    // Save AST as it is or process it to extract features / path-based representations
                    storage.store(root, label = filePath)
                }
            }
            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            // Save stored data on disk
            storage.save(outputDirForLanguage.path)
        }

    }

    override fun run() {
        parsing()
    }
}