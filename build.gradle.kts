
plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "org.monkey"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

// See https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3.6")
    type.set("PY")
    plugins.set(listOf("Pythonid"))
    downloadSources.set(false)
}

tasks {
    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        version.set("${project.version}")
        sinceBuild.set("233")
        untilBuild.set("242.*")
    }
}