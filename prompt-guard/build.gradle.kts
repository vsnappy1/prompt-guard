import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    jacoco
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech.maven.publish)
}

apply(from = "../gradle/jacoco.gradle.kts")

group = "dev.randos"
version = "0.1.0"

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = "prompt-guard",
        version = project.version.toString()
    )

    pom {
        name.set("PromptGuard")
        description.set(
            "A Kotlin-first toolkit for detecting sensitive data in privacy-aware AI workflows."
        )
        inceptionYear.set("2026")
        url.set("https://github.com/vsnappy1/prompt-guard")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("vsnappy1")
                name.set("Vishal Kumar")
                email.set("vsnappy1@gmail.com")
                url.set("https://github.com/vsnappy1")
            }
        }

        scm {
            url.set("https://github.com/vsnappy1/prompt-guard")
            connection.set("scm:git:https://github.com/vsnappy1/prompt-guard.git")
            developerConnection.set("scm:git:ssh://git@github.com/vsnappy1/prompt-guard.git")
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
