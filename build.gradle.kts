plugins {
    application
    `java`
    id("io.papermc.paperweight.userdev") version "1.3.5"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    mavenLocal()
}

dependencies {
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.19.2-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}