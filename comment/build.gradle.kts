import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.van1164"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}
val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true
dependencies {
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":security"))
    implementation(project(":main"))
}

tasks.test {
    useJUnitPlatform()
}
