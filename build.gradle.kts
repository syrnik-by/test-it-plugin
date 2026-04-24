import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellij)
}

group = libs.versions.pluginGroup.get()
version = libs.versions.pluginVersion.get()

repositories {
    mavenLocal()
    mavenCentral()
}

intellij {
    version.set(libs.versions.idea) // например 2022.3
    plugins.set(listOf("java", "kotlin"))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(libs.jakarta)
    implementation(libs.jsoup)
    implementation(libs.slf4j)
    implementation(libs.testit.api)
    implementation(libs.okhttp)
    testImplementation(kotlin("test"))
}

tasks {
    wrapper {
        gradleVersion = libs.versions.gradle.get()
    }

    patchPluginXml {
        sinceBuild.set(libs.versions.pluginSinceBuild)
        untilBuild.set(libs.versions.pluginUntilBuild)
        pluginDescription.set(
            "The Test IT Management plugin is a powerful tool for managing test cases. " +
                    "It provides an ability to browse work items hierarchies, generate unit tests for selected scenarios."
        )
        changeNotes.set("Initial port to old intellij gradle plugin")
    }

    publishPlugin {
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }

    signPlugin {
        certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    withType<JavaCompile>().configureEach {
        sourceCompatibility = libs.versions.javaVersion
        targetCompatibility = libs.versions.javaVersion
        options.encoding = libs.versions.javaEncoding
        options.isFork = true
    }

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            apiVersion = KotlinVersion.KOTLIN_1_7
            languageVersion = KotlinVersion.KOTLIN_1_9
            incremental = true
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
            showStandardStreams = true
        }
        outputs.upToDateWhen { false }
    }
}
