plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.7"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
}

group = "dev.chicoferreira"
version = "1.0.0"
description = "Simple and performant lifesteal plugin for managing player hearts"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    mavenCentral()

    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")

    compileOnly("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0")
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.2")
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("com.google.code.gson:gson:2.12.1")
}

runPaper.folia.registerTask()

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
        }
    }
}

bukkitPluginYaml {
    main = "dev.chicoferreira.lifestealer.Lifestealer"
    authors.add("chicoferreira")
    apiVersion = "1.20.6"
    softDepend.add("PlaceholderAPI")
    foliaSupported = true
    libraries.add("com.zaxxer:HikariCP:6.2.1")
    libraries.add("com.h2database:h2:2.3.232")
    libraries.add("org.mariadb.jdbc:mariadb-java-client:3.5.2")
    libraries.add("org.postgresql:postgresql:42.7.5")
    libraries.add("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0")
}