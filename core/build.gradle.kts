@file:Suppress("UnstableApiUsage")

plugins {
    id("java")
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.10.1")
        }
        dependencies {
            runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
        }
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

publishing {
    publications {
        register<MavenPublication>("jsonMatcherPublication") {
            pom {
                name.set("json-matcher")
                description.set("json-matcher")
                url.set("https://github.com/kjetilv/json-matcher")

                licenses {
                    license {
                        name.set("GNU General Public License v3.0")
                        url.set("https://github.com/kjetilv/json-matcher/blob/main/LICENSE")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/kjetilv/json-matcher")
                    developerConnection.set("scm:git:https://github.com/kjetilv/json-matcher")
                    url.set("https://github.com/kjetilv/json-matcher")
                }
            }
            from(components["java"])
        }
    }
}
