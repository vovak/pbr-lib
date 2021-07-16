import tanvd.kosogor.proxy.shadowJar

group = "io.github.vovak"
version = "0.6.4"

plugins {
    id("java")
    kotlin("jvm") version "1.5.10" apply true
    id("antlr")
    id("application")
    id("org.jetbrains.dokka") version "1.4.32"
    id("me.champeau.gradle.jmh") version "0.5.0"
    id("maven-publish")
    id("tanvd.kosogor") version "1.0.10" apply true
    kotlin("plugin.serialization") version "1.4.32"
}

defaultTasks("run")

repositories {
    mavenCentral()
}

dependencies {
    // ===== Parsers =====
    antlr("org.antlr:antlr4:4.7.1")
    // https://mvnrepository.com/artifact/com.github.gumtreediff
    api("com.github.gumtreediff", "core", "2.1.2")
    api("com.github.gumtreediff", "client", "2.1.2")
    api("com.github.gumtreediff", "gen.jdt", "2.1.2")
    api("com.github.gumtreediff", "gen.python", "2.1.2")
    // https://github.com/javaparser/javaparser
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.22.1")

    // https://mvnrepository.com/artifact/io.shiftleft/fuzzyc2cpg
    api("io.shiftleft", "fuzzyc2cpg_2.13", "1.2.30")

    // ===== Main =====
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt.clikt", "clikt", "3.2.0")
    implementation("com.charleskorn.kaml:kaml:0.33.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")

    // ===== Logging =====
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation("io.github.microutils:kotlin-logging:1.5.9")


    // ===== Test =====
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test-junit"))

    // ===== JMH =====
    jmhImplementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0")
    jmhImplementation("org.openjdk.jmh:jmh-core:1.21")
    jmhImplementation("org.openjdk.jmh:jmh-generator-annprocess:1.21")
}

val generatedSourcesPath = "src/main/generated"
sourceSets["main"].java.srcDir(file(generatedSourcesPath))
idea.module.generatedSourceDirs.add(file(generatedSourcesPath))

tasks.generateGrammarSource {
    // maxHeapSize = "64m"
    arguments = arguments + listOf("-package", "me.vovak.antlr.parser")
    // Keep a copy of generated sources
    doLast {
        println("Copying generated grammar lexer/parser files to main directory.")
        copy {
            from("$buildDir/generated-src/antlr/main")
            into("$generatedSourcesPath/me/vovak/antlr/parser")
        }
        file("$buildDir/generated-src/antlr").deleteRecursively()
    }
    // Run when source dir has changed or was removed
    outputs.dir(generatedSourcesPath)
}

tasks.clean {
    doLast {
        file(generatedSourcesPath).deleteRecursively()
    }
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
    kotlinOptions.jvmTarget = "11"
}
tasks.compileJava {
    dependsOn(tasks.generateGrammarSource)
    targetCompatibility = "11"
    sourceCompatibility = "11"
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

jmh {
    duplicateClassesStrategy = DuplicatesStrategy.WARN
    profilers = listOf("gc")
    resultFormat = "CSV"
    isZip64 = true
    failOnError = true
    forceGC = true
    warmupIterations = 1
    iterations = 4
    fork = 2
    jvmArgs = listOf("-Xmx32g")
    benchmarkMode = listOf("AverageTime")
    resultsFile = file("build/reports/benchmarks.csv")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/astminer/astminer")
            credentials {
                username = System.getenv("PUBLISH_USER")?.takeIf { it.isNotBlank() } ?: ""
                password = System.getenv("PUBLISH_PASSWORD")?.takeIf { it.isNotBlank() } ?: ""
            }
        }
    }
}

application.mainClassName = "astminer.MainKt"
shadowJar {
    jar {
        archiveName = "astminer.jar"
    }
}.apply {
    task.archiveClassifier.set("")
}

tasks.withType<Test> {
    // Kotlin DSL workaround from https://github.com/gradle/kotlin-dsl-samples/issues/836#issuecomment-384206237
    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                println(
                    "${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, " +
                    "${result.failedTestCount} failures, ${result.skippedTestCount} skipped)"
                )
            }
        }
    })
}