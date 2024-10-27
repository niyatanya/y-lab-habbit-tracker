import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    war
    checkstyle
    id("io.freefair.lombok") version "8.6"
}

group = "org.home"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
    testImplementation("org.testcontainers:postgresql:1.19.0")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.liquibase:liquibase-core:4.24.0")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.apache.tomcat:tomcat-servlet-api:11.0.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
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

tasks.war {
    archiveFileName.set("habit-tracker.war")
    archiveBaseName.set("lessonthree")
    from("src/main/webapp")
}
