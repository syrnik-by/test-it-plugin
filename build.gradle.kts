import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    id("org.jetbrains.intellij") version "1.4.0"
}

fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

group = libs.versions.pluginGroup.get()
version = libs.versions.pluginVersion.get()

repositories {
    mavenLocal()
//    intellijPlatform {
//        defaultRepositories()
//    }
    maven { url = uri("local-repo") }
    maven(url = "https://nexus-internal.headoffice.psbank.local/repository/at-maven-group/")
    maven(url = "https://nexus-external/repository/deprecated-repo.haulmont.com-proxy")
    maven(url = "https://nexus-external/repository/maven-public/")
    maven(url = "https://nexus-external/repository/maven-newathena-proxy")
    maven(url = "https://nexus-external/repository/repo1.maven.org-proxy/")


}

dependencies {

    api(libs.jakarta)
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.jsoup)
    implementation(libs.slf4j)
    implementation(libs.testit.api)
    implementation("com.squareup.okhttp3:okhttp:3.9.1")
    testImplementation(kotlin("test"))
}

intellij {
//    version.set(libs.versions.ideaVersion.get())
    localPath.set("/home/opt/intllijidea/idea-IC-241.17890.1/")
    plugins.set(emptyList())
}

tasks {

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set(libs.versions.pluginSinceBuild)
        untilBuild.set(libs.versions.pluginUntilBuild)
    }

    publishPlugin {
        token.set(environment("PUBLISH_TOKEN"))
    }

    signPlugin {
        certificateChain.set(environment("CERTIFICATE_CHAIN"))
        privateKey.set(environment("PRIVATE_KEY"))
        password.set(environment("PRIVATE_KEY_PASSWORD"))
    }

    withType<JavaCompile>().configureEach {
        sourceCompatibility = libs.versions.javaVersion.get()
        targetCompatibility = libs.versions.javaVersion.get()
        options.setIncremental(true)
        options.isFork = true
        options.encoding = libs.versions.javaEncoding.get()
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            apiVersion = "1.6"
            languageVersion = "1.6"
            incremental = true
        }
    }

    withType<Test>().configureEach {
        val logPropsFile = sourceSets.main.get().resources.find {
            it.name.endsWith("logging.properties")
        } as File

        if (logPropsFile.exists()) {
            systemProperty("java.util.logging.config.file", logPropsFile)
        }

        systemProperty(
            "java.util.logging.manager",
            "java.util.logging.LogManager"
        )
        systemProperty(
            "TEST_CI",
            System.getProperty("testCi")
        )
        reports {
            html.required.set(true)
            junitXml.required.set(false)
        }
        testLogging {
            events = setOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
            showCauses = false
            showStackTraces = false
            showStandardStreams = true
        }
        outputs.upToDateWhen { false }
        useJUnitPlatform()
    }
}


