package astminer.parse.cpp

import astminer.checkExecutable
import astminer.common.getProjectFilesWithExtension
import astminer.examples.forFilesWithSuffix
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parse.fuzzy.cpp.FuzzyNode
import astminer.parseFiles
import org.junit.Assert
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.io.File

class FuzzyCppParserTest {

    @Before
    fun checkGPP() = Assume.assumeTrue(checkExecutable("g++"))

    @Test
    fun testInputStreamParsing() {
        val folder = File("src/test/resources/fuzzy/")
        val nodes = ArrayList<FuzzyNode>()
        var n = 0
        val parser = FuzzyCppParser()
        folder.forFilesWithSuffix(".cpp") { file ->
            n++
            parser.parseInputStream(file.inputStream()).let { nodes.add(it) }
        }
        Assert.assertEquals(n, nodes.size)
    }

    @Test
    fun testProjectParsing() {
        val folder = File("src/test/resources/fuzzy/")
        val parser = FuzzyCppParser()
        val nodes = parser.parseFiles(getProjectFilesWithExtension(folder, "cpp"))
        Assert.assertEquals(
                "There is only 3 file with .cpp extension in 'testData/examples' folder",
                3,
                nodes.size
        )
    }

    @Test
    fun testPreprocessingDefine() {
        val folder = File("src/test/resources/fuzzy")
        val preprocessedFolder = folder.resolve("preprocessed")
        preprocessedFolder.mkdir()
        val defineFileName = "preprocDefineTest.cpp"
        val parser = FuzzyCppParser()

        parser.preprocessFile(folder.resolve(defineFileName), preprocessedFolder)

        Assert.assertEquals(
                "'define' directives should be replaced",
                "for (int i = (0); i < (10); ++i) { }",
                preprocessedFolder.resolve(defineFileName).readInOneLine()
        )
        preprocessedFolder.deleteRecursively()
    }

    @Test
    fun testPreprocessingInclude() {
        val folder = File("src/test/resources/fuzzy")
        val preprocessedFolder = folder.resolve("preprocessed")
        preprocessedFolder.mkdir()
        val includeFileName = "preprocIncludeTest.cpp"
        val parser = FuzzyCppParser()

        parser.preprocessFile(folder.resolve(includeFileName), preprocessedFolder)

        Assert.assertEquals(
                "'include' directives should not be replaced",
                folder.resolve(includeFileName).readInOneLine(),
                preprocessedFolder.resolve(includeFileName).readInOneLine()
        )
        preprocessedFolder.deleteRecursively()
    }

    @Test
    fun testPreprocessingProject() {
        val projectRoot = File("src/test/resources/examples/cpp")
        val preprocessedRoot = File("src/test/resources/examples/preprocessed")
        preprocessedRoot.mkdir()
        val parser = FuzzyCppParser()

        parser.preprocessProject(projectRoot, preprocessedRoot)
        val nodes = parser.parseFiles(getProjectFilesWithExtension(projectRoot, "cpp"))

        Assert.assertEquals(
                "Parse tree for a valid file should not be null. There are 5 files in example project.",
                5,
                nodes.size
        )
        preprocessedRoot.deleteRecursively()
    }
}