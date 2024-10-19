import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    application
    checkstyle
    id("io.freefair.lombok") version "8.6"
}

group = "org.home"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.home.Main")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.22.0")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.liquibase:liquibase-core:4.24.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_OUT
        )

        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
