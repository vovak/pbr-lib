package astminer.labelextractor

import astminer.common.model.LabeledResult
import astminer.common.model.ParseResult
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FolderNameExtractorTest {
    companion object {
        private const val PATH = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private var dummyRoot = AntlrNode("", null, null)
    }

    @Test
    fun `test folder extractor returns null when folder is empty or not found`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, "")
        val labeledParseResult = FolderNameExtractor.process(nonEmptyParseResult)

        assertNull(labeledParseResult)
    }

    @Test
    fun `test folder extractor extracts folder when it is not empty`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH)
        val labeledParseResult = FolderNameExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, FOLDER, PATH), labeledParseResult)
    }
}
