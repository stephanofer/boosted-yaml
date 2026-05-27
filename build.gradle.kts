import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.4.1"
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()
description = providers.gradleProperty("description").get()

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()
}

val shade by configurations.creating

dependencies {
    api("org.snakeyaml:snakeyaml-engine:2.9")

    compileOnly("org.jetbrains:annotations-java5:23.1.0")
    testCompileOnly("org.jetbrains:annotations-java5:23.1.0")

    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    shade("org.snakeyaml:snakeyaml-engine:2.9")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(8)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("unshaded")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    configurations = listOf(shade)
    relocate("org.snakeyaml.engine", "dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.named("sourcesJar"))
            artifact(tasks.named("javadocJar"))
            artifact(tasks.named("jar"))
            artifact(tasks.named("shadowJar"))

            pom {
                name.set("BoostedYAML")
                description.set(project.description)
                url.set("https://github.com/stephanofer/boosted-yaml")

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("stephanofer")
                        name.set("stephanofer")
                    }
                }

                scm {
                    connection.set("scm:git:git@github.com:stephanofer/boosted-yaml.git")
                    developerConnection.set("scm:git:git@github.com:stephanofer/boosted-yaml.git")
                    url.set("https://github.com/stephanofer/boosted-yaml")
                }
            }
        }
    }
}
