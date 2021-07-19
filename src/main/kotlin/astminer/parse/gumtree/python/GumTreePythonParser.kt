package astminer.parse.gumtree.python

import astminer.common.model.Parser
import astminer.parse.ParsingException
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.wrapGumTreeNode
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.python.PythonTreeGenerator
import com.github.gumtreediff.tree.TreeContext
import java.io.InputStream
import java.io.InputStreamReader

class GumTreePythonParser : Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode = try {
        val context = PythonTreeGenerator().generate(InputStreamReader(content))
        wrapGumTreeNode(context)
    } catch (e: Exception) {
        throw ParsingException("GumTree", "Python", e.message)
    }
}