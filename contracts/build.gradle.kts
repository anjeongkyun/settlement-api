plugins {
    checkstyle
    id("org.ec4j.editorconfig") version "0.0.3"
    id("io.freefair.lombok") version "8.0.1"
    id("java-library")
    kotlin("jvm") version "1.8.20"
}

project(":contracts") {
    dependencies {
        implementation(
            group = "com.fasterxml.jackson.core",
            name = "jackson-annotations",
            version = "2.12.4"
        )
    }
    checkstyle {
        configFile = file("../.rules/checkstyle/checkstyle.xml")
        toolVersion = "8.40"
    }
    tasks.jar {
        enabled = true
        dependsOn("editorconfigCheck", "checkstyleMain")
    }
    tasks.bootJar {
        enabled = false
    }
}
