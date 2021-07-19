package astminer.parse.gumtree.java.JDT

import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator
import astminer.common.model.Parser
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.wrapGumTreeNode
import java.io.InputStream
import java.io.InputStreamReader

class GumTreeJavaJDTParser : Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode {
        val treeContext = JdtTreeGenerator().generate(InputStreamReader(content))
        return wrapGumTreeNode(treeContext)
    }
}

