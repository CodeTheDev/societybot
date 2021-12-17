import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    idea
    id("com.github.johnrengelman.shadow") version "7.1.1" apply false
}

group = "dev.codeerror"
version = "1.0"

apply(plugin = "com.github.johnrengelman.shadow")

repositories {
    mavenCentral()
    jcenter() // JCenter is Deprecated. Needed for jda-nas.
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.4.0_351")
    implementation("com.sedmelluq:lavaplayer:1.3.77")
    implementation("com.sedmelluq:jda-nas:1.1.0")
    implementation("org.slf4j:slf4j-simple:1.7.32")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "dev.codeerror.societybot.SocietyBot"
    }
}