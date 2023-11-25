plugins {
    id("java")
    `maven-publish`
}

group = "com.github.kjetilv.json"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        withSourcesJar()
        languageVersion.set(JavaLanguageVersion.of(21))
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
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
