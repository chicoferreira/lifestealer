plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.2"
}

group = "dev.chicoferreira"
version = "0.0.0-SNAPSHOT"
description = "Simple and performant lifesteal plugin for managing player hearts"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    mavenCentral()

    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.5.1")
}

tasks {
    test {
        useJUnitPlatform()
    }

    compileJava {
        options.release = 21
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    runServer {
        downloadPlugins {
            hangar("PlaceholderAPI", "2.11.6")
            hangar("CommandAPI", "9.5.1")
        }
    }
}

bukkitPluginYaml {
    main = "dev.chicoferreira.lifestealer.Lifestealer"
    authors.add("chicoferreira")
    apiVersion = "1.20"
    depend.add("CommandAPI")
}