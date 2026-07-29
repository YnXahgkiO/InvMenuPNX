plugins {
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "back"
version = "1.0.1"

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    exclusiveContent {
        forRepository {
            ivy {
                name = "powerNukkitXReleases"
                url = uri("https://github.com/PowerNukkitX/PowerNukkitX/releases/download")
                patternLayout {
                    artifact("[revision]/[artifact].[ext]")
                }
                metadataSources {
                    artifact()
                }
            }
        }
        filter {
            includeModule("org.powernukkitx", "powernukkitx")
        }
    }
}

dependencies {
    // PowerNukkitX API — official 3.0.0 release artifact
    compileOnly("org.powernukkitx:powernukkitx:3.0.0")

    // JetBrains annotations (@Nullable, @NotNull) — used in the API
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveFileName.set("InvMenuPNX-${project.version}.jar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "com.github.zorao"
            artifactId = "InvMenuPNX"
            version = project.version.toString()
        }
    }
}
