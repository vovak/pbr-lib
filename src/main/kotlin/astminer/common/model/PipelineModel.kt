package astminer.common.model

import java.io.Closeable


interface Filter

interface LabelExtractor

interface FileFilter : Filter {
    fun validate(parseResult: ParseResult<out Node>): Boolean
}

interface FunctionFilter : Filter {
    fun validate(functionInfo: FunctionInfo<out Node>): Boolean
}

interface FileLabelExtractor : LabelExtractor {
    fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node>?
}

interface FunctionLabelExtractor : LabelExtractor {
    fun process(functionInfo: FunctionInfo<out Node>): LabeledResult<out Node>?
}

/**
 * An AST subtree with a label and the path of the source file.
 * @property root The root of the AST subtree.
 * @property label Any label for this subtree.
 * @property filePath The path to the source file where the AST is from.
 */
data class LabeledResult<T : Node>(val root: T, val label: String, val filePath: String)

/**
 * Storage saved labeled results to disk in a specified format.
 * Storage might extract any data from labeled result.
 * For instance, it might extract paths from trees
 */
interface Storage : Closeable {
    val outputDirectoryPath: String

    fun store(labeledResult: LabeledResult<out Node>)

    fun store(labeledResults: Iterable<LabeledResult<out Node>>) {
        for (labeledResult in labeledResults) {
            store(labeledResult)
        }
    }
}
