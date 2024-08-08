plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

group = "dev.chicoferreira"
version = "0.0.0-SNAPSHOT"
description = "Simple and performant lifesteal plugin for managing player hearts"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}

tasks {
    test {
        useJUnitPlatform()
    }

    compileJava {
        options.release = 17
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkitPluginYaml {
    main = "dev.chicoferreira.lifestealer.Lifestealer"
    authors.add("chicoferreira")
    apiVersion = "1.20"
}