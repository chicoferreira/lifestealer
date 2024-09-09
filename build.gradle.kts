plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
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
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.5.3")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    implementation("org.postgresql:postgresql:42.7.4")
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
            hangar("CommandAPI", "9.5.3")
        }
    }
}

bukkitPluginYaml {
    main = "dev.chicoferreira.lifestealer.Lifestealer"
    authors.add("chicoferreira")
    apiVersion = "1.21"
    depend.add("CommandAPI")
    softDepend.add("PlaceholderAPI")
    libraries.add("com.zaxxer:HikariCP:5.1.0")
    libraries.add("com.h2database:h2:2.3.232")
    libraries.add("org.mariadb.jdbc:mariadb-java-client:3.4.1")
    libraries.add("org.postgresql:postgresql:42.7.4")
}