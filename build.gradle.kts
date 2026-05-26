plugins {
    alias(libs.plugins.shadow)
}

group = "dev.dejvokep"
version = "1.3.7"

val shade by configurations.creating

dependencies {
    api("org.snakeyaml:snakeyaml-engine:2.9")

    compileOnly("org.jetbrains:annotations-java5:23.1.0")
    testCompileOnly("org.jetbrains:annotations-java5:23.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

    shade("org.snakeyaml:snakeyaml-engine:2.9")
}

tasks.shadowJar {
    archiveClassifier.set("")
    configurations = listOf(shade)
    relocate("org.snakeyaml.engine", "dev.dejvokep.boostedyaml.libs.org.snakeyaml.engine")
}

tasks.jar {
    archiveClassifier.set("unshaded")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
