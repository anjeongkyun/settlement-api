plugins {
    checkstyle
    id("org.ec4j.editorconfig") version "0.0.3"
    kotlin("jvm") version "1.8.20"
}

project(":unit-test") {
    dependencies {
        testImplementation(project(":domain-model"))
        testImplementation(project(":api"))
        testImplementation(project(":data-access"))
        testImplementation(project(":contracts"))

        testImplementation("org.springframework.boot:spring-boot-starter-web")
        testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb")

        testImplementation(
            group = "com.fasterxml.jackson.core",
            name = "jackson-core",
            version = "2.12.4"
        )
        testImplementation(
            group = "com.fasterxml.jackson.core",
            name = "jackson-databind",
            version = "2.12.4"
        )
        testImplementation(
            group = "com.fasterxml.jackson.core",
            name = "jackson-annotations",
            version = "2.12.4"
        )
        testImplementation(platform("org.junit:junit-bom:5.9.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")

        testImplementation("io.github.autoparams:autoparams:1.1.1")
        testImplementation("io.github.autoparams:autoparams-mockito:1.1.1")
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
