plugins {
    checkstyle
    id("org.ec4j.editorconfig") version "0.0.3"
    kotlin("jvm") version "1.8.20"
}

project(":api") {
    dependencies {
        implementation(project(":contracts"))
        implementation(project(":data-access"))
        implementation(project(":domain-model"))

        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")

        implementation(
            group = "com.fasterxml.jackson.core",
            name = "jackson-core",
            version = "2.13.3"
        )
        implementation(
            group = "com.fasterxml.jackson.core",
            name = "jackson-databind",
            version = "2.13.3"
        )
        implementation(
            group = "com.fasterxml.jackson.core",
            name = "jackson-annotations",
            version = "2.12.4"
        )
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
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
        enabled = true
    }
}
