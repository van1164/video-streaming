plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.van1164"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":security"))
}

tasks.test {
    useJUnitPlatform()
}
