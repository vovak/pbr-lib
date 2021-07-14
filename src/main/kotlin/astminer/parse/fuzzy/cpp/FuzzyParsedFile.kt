package astminer.parse

import astminer.common.model.ParsedFileFactory
import astminer.common.model.ParsedFile
import astminer.common.model.ParseResult
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parse.fuzzy.cpp.FuzzyFunctionSplitter
import astminer.parse.fuzzy.cpp.FuzzyNode
import java.io.File

object FuzzyCppParsedFile : ParsedFileFactory {
    override fun createHandler(file: File): ParsedFile<FuzzyNode> = CppFuzzyHandler(file)


    class CppFuzzyHandler(file: File) : ParsedFile<FuzzyNode>() {
        override val splitter = FuzzyFunctionSplitter()
        override val parseResult: ParseResult<FuzzyNode> = FuzzyCppParser().parseFile(file)
    }
}