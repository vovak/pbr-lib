package astminer.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This config is used to select the parsers that should be used
 * If given type = "antlr" and extensions = ["py", "java"]
 * then 2 ANTLR parsers will be used (java antler parser and python antlr parser)
 * @param name Type of the parser
 * @param extensions File extensions that should be parsed
 */
@Serializable
data class ParserConfig(
    val name: ParserType,
    val extensions: List<FileExtension>
)

@Serializable
enum class ParserType {
    @SerialName("antlr") Antlr,
    @SerialName("gumtree") GumTree,
    @SerialName("fuzzy") Fuzzy,
    @SerialName("javaparser") JavaParser
}

@Serializable
enum class FileExtension(val fileExtension: String) {
    @SerialName("py") Python("py"),
    @SerialName("java") Java("java"),
    @SerialName("js") JavaScript("js"),
    @SerialName("c") C("c"),
    @SerialName("cpp") Cpp("cpp"),
    @SerialName("php") PHP("php")
}
