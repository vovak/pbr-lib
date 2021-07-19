package astminer.parse.spoon

import astminer.common.model.ParseResult
import astminer.common.model.Parser
import org.apache.commons.io.FileUtils.copyInputStreamToFile
import spoon.Launcher
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import kotlin.io.path.Path
import java.nio.file.FileAlreadyExistsException

class SpoonJavaParser : Parser<SpoonNode> {
    // TODO try to run on different platforms
    private val tempPath = Path("./spoonTmpCanBeDeletedAfterParsing")
    private val suffix = ".java"

    private fun createTempDirectory() {
        try { Files.createDirectory(tempPath)} catch (e: FileAlreadyExistsException) {}
    }

    override fun parseInputStream(content: InputStream): SpoonNode {
        createTempDirectory()
        val tempFile = kotlin.io.path.createTempFile(suffix = suffix, directory = tempPath).toFile()
        copyInputStreamToFile(content, tempFile)

        val launcher = Launcher()
        launcher.addInputResource(tempFile.path)
        val model = try { launcher.buildModel() } catch (e: Exception) { throw e }
        finally { tempFile.delete() }

        return SpoonNode(model.unnamedModule, null)
    }

    override fun parseFile(file: File): ParseResult<SpoonNode> {
        val launcher = Launcher()
        launcher.addInputResource(file.path)
        val model = launcher.buildModel()
        return ParseResult(SpoonNode(model.unnamedModule, null), file.path)
    }
}