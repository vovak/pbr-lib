package astminer.common.model

import java.io.File

/**
 * An interface for factories that create concrete instances
 * of the parsed file class.
 */
interface ParsedFileFactory {
    fun createHandler(file: File): ParsedFile<out Node>
}

/**
 * An abstract class whose descendants represent parsed files obtained from parsers.
 * These classes handle parsing a single file and working with it
 * (for example splitting into methods)
 */
abstract class ParsedFile<T: Node> {
    abstract val parseResult: ParseResult<T>
    protected abstract val splitter: TreeFunctionSplitter<T>

    fun splitIntoMethods(): Collection<FunctionInfo<out Node>> {
        val root = parseResult.root
        return splitter.splitIntoFunctions(root)
    }
}
