import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    idea
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "dev.codeerror"
version = "1.1"

apply(plugin = "com.github.johnrengelman.shadow")

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.4.0_352")
    implementation("com.sedmelluq:lavaplayer:1.3.78")
    implementation("org.slf4j:slf4j-simple:1.7.36")
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
        attributes("Main-Class" to "dev.codeerror.societybot.SocietyBot")
    }
}