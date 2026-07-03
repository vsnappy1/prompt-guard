plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

group = "dev.randos"
version = "0.1.0"

kotlin {
    jvmToolchain(11)
}

java {
    withSourcesJar()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = "prompt-guard"
            version = project.version.toString()
        }
    }
}
