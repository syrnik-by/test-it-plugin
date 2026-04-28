enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    repositories {
        maven { url = uri ("local-repo") }
        mavenLocal()
        maven {
            url = uri("https://nexus-external/repository/plugins.gradle.org-proxy/")
        }
    }
}
rootProject.name = "testit-management"