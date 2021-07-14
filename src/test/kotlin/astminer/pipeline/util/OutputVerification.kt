package astminer.pipeline.util

import java.io.File
import kotlin.test.assertTrue

/**
 * Directory with extracted data should contain a directory for each specified language
 */
internal fun checkExtractedDir(extractedDataDir: File, languages: List<String>) {
    val metLanguages = mutableSetOf<String>()
    extractedDataDir.listFiles()?.forEach { file ->
        with(file) {
            assertTrue(isDirectory, "Extracted data directory should not contain file $name")
            assertTrue(languages.contains(file.name), "Unexpected directory $name")
            metLanguages.add(name)
        }
    }
    languages.forEach { language ->
        assertTrue(metLanguages.contains(language), "Did not find directory for $language")
    }
}

internal fun validPathContextsFile(name: String, batching: Boolean): Boolean {
    return if (batching) {
        name.startsWith("path_contexts_") && name.endsWith(".csv")
    } else {
        name == "path_contexts.csv"
    }
}

internal fun checkPathContextsDir(languageDir: File, batching: Boolean) {
    val expectedFiles = listOf("tokens.csv", "paths.csv", "node_types.csv")
    languageDir.listFiles()?.forEach { file ->
        with(file) {
            assertTrue(
                expectedFiles.contains(name) || validPathContextsFile(name, batching),
                "Unexpected file $name in ${languageDir.name}"
            )
        }
    }
}

internal fun verifyPathContextExtraction(extractedDataDir: File, languages: List<String>, batching: Boolean) {
    checkExtractedDir(extractedDataDir, languages)
    languages.forEach { language ->
        checkPathContextsDir(extractedDataDir.resolve(language), batching)
    }
}
