package astminer.labelextractor

import astminer.common.model.LabeledResult
import astminer.common.model.ParseResult
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals

class FileNameExtractorTest {
    companion object {
        private const val FILE_NAME = "file.txt"
        private const val PATH = "random/folder/$FILE_NAME"
        private var dummyRoot = AntlrNode("", null, null)
    }

    @Test
    fun `test file path extractor returns the same root and file path and labels with file path`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH)
        val labeledParseResult = FileNameExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, FILE_NAME, PATH), labeledParseResult)
    }
}