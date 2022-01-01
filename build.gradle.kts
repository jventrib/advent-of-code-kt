plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

tasks {
    test {
        useJUnitPlatform()
    }

    wrapper {
        gradleVersion = "7.3"
    }

    dependencies {
        implementation("com.github.ajalt.mordant:mordant:2.0.0-beta4")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }
}
