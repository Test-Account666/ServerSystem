import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import io.papermc.paperweight.util.path
import org.apache.tools.ant.filters.ReplaceTokens
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.plugins.maven.shade)
    alias(libs.plugins.paperweight.userdev)
    alias(libs.plugins.git.version)
}

group = "me.testaccount666"
val cleanVersion = "4.1.0"

@Suppress("UNCHECKED_CAST")
val gitVersion = extra["gitVersion"] as groovy.lang.Closure<String>
version = "${cleanVersion}-" + gitVersion().dropWhile { it != '-' }.drop(1).replace("dirty", getFormattedDate())

fun getFormattedDate(): String {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd.HH-mm").format(LocalDateTime.now())
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

val generateVersionInfo = tasks.register("generateVersionInfo") {
    val template = file("src/main/resources/templates/VersionInfo.kt")
    val outputDir = layout.buildDirectory.dir("generated/kotlin")

    inputs.file(template)
    outputs.dir(outputDir)

    doLast {
        val outputFile = outputDir.get().file("me/testaccount666/serversystem/utils/VersionInfo.kt").asFile
        outputFile.parentFile.mkdirs()
        template.copyTo(outputFile, true)
        outputFile.writeText(outputFile.readText().replace("@CLEAN_VERSION@", cleanVersion))
    }
}

kotlin {
    compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict", "-jvm-target=21") }

    sourceSets.main { kotlin.srcDir(generateVersionInfo) }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven("https://repo.essentialsx.net/releases")
    maven("https://repo.papermc.io/repository/maven-public")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public")
    maven("https://gitlab.com/api/v4/projects/80077577/packages/maven")
}

dependencies {
    paperweightDevelopmentBundle(libs.paperdevbundle)
    implementation(libs.paperktx) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
    }
    compileOnly(libs.kotlin.stdlib)
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.paperapi)
    compileOnly(libs.clip.placeholderapi)
    compileOnly(libs.milkbowl.vaultapi)
    compileOnly(libs.essentialsx.essentialsx) { exclude("**", "**") }
    compileOnly(libs.tr7zw.item.nbt.api.plugin)
    compileOnly(libs.classgraph)
    compileOnly(libs.bytebuddy.byte.buddy)
    compileOnly(libs.zaxxer.hikaricp)
    compileOnly(libs.h2)
    compileOnly(libs.netty.all)
    compileOnly(libs.mojang.authlib)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(kotlin("test"))
}

paperweight { reobfArtifactConfiguration.set(ReobfArtifactConfiguration.MOJANG_PRODUCTION) }

tasks.build { dependsOn(tasks.shadowJar) }

tasks.shadowJar {
    relocate("me.testaccount666.paperktx", "me.testaccount666.serversystem.libs.paperktx")
    archiveFileName.set("ServerSystem.jar")

    val libsDirectory = layout.buildDirectory.path.resolve("libs")

    doFirst {
        libsDirectory.toFile().listFiles().filter { it.name.endsWith(".jar") }.forEach(File::delete)
    }

    doLast {
        archiveFile.get().asFile.copyTo(libsDirectory.resolve("ServerSystem-${version}.jar").toFile(), true)
        println(gitVersion())
    }
}

tasks.processResources {
    exclude("templates/**")
    filter(
        ReplaceTokens::class,
        mapOf(
            "tokens" to mapOf(
                "PROJECT_VERSION" to project.version,
                "CLEAN_VERSION" to cleanVersion,
                "KOTLIN_VERSION" to libs.versions.kotlin.get()
            )
        )
    )
}

tasks.withType<Test>(Test::useJUnitPlatform)
