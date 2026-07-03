import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    `maven-publish`
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.randos"
version = "0.1.0"

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

dependencies {
    testImplementation(libs.junit)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

ktlint {
    reporters {
        reporter(ReporterType.HTML)
    }
}
