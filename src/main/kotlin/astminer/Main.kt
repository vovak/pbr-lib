package astminer

import astminer.common.model.FunctionInfoPropertyNotImplementedException
import astminer.config.PipelineConfig
import astminer.pipeline.Pipeline
import astminer.pipeline.branch.IllegalFilterException
import astminer.pipeline.branch.IllegalLabelExtractorException
import com.charleskorn.kaml.PolymorphismStyle
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger("Main")

class PipelineRunner : CliktCommand(name = "") {
    val config: File by argument("config", help = "Path to config").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    override fun run() {
        try {
            val config = yaml.decodeFromString<PipelineConfig>(config.readText())
            Pipeline(config).run()
        } catch (e: SerializationException) {
            report("There was a problem in the config", e)
        } catch (e: IllegalLabelExtractorException) {
            report("PipelineBranch for given label extractor not found", e)
        } catch (e: IllegalFilterException) {
            report("The chosen filter is not implemented for the chosen granularity", e)
        } catch (e: FunctionInfoPropertyNotImplementedException) {
            report(
                "The chosen parser does not implement the required properties. " +
                        "Consider implementing them or change the parser",
                e
            )
        }
    }

    private fun report(message: String, e: Exception) {
        logger.error(e) { message }
        println("$message:\n$e")
    }

    companion object {
        private const val POLYMORPHISM_PROPERTY_NAME = "name"

        private val yaml = Yaml(
            configuration = YamlConfiguration(
                polymorphismStyle = PolymorphismStyle.Property,
                polymorphismPropertyName = POLYMORPHISM_PROPERTY_NAME
            )
        )
    }

}

fun main(args: Array<String>) = PipelineRunner().main(args)
