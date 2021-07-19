package astminer.parse.spoon

import astminer.common.model.*
import java.io.File

class SpoonHandler(file: File): LanguageHandler<SpoonNode>() {
    override val parseResult: ParseResult<SpoonNode> = SpoonJavaParser().parseFile(file)
    override val splitter: TreeFunctionSplitter<SpoonNode> = SpoonJavaMethodSplitter()
}

class SpoonHandlerFactory: HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<out Node> {
        return SpoonHandler(file)
    }
}