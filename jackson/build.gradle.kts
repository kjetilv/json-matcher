@file:Suppress("UnstableApiUsage")

plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("6.0.1")
        }
        dependencies {
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.1")
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation("com.fasterxml.jackson.core:jackson-core:2.20.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")

    testImplementation("org.assertj:assertj-core:3.27.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.1")
}

