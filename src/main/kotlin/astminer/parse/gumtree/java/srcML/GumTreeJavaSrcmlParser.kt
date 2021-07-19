package astminer.parse.gumtree.java.srcML

import astminer.common.model.Parser
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.wrapGumTreeNode
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.srcml.SrcmlJavaTreeGenerator
import java.io.InputStream
import java.io.InputStreamReader

class GumTreeJavaSrcmlParser: Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode {
        val treeContext = SrcmlJavaTreeGenerator().generate(InputStreamReader(content))
        return wrapGumTreeNode(treeContext)
    }
}
