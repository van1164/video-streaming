import org.springframework.boot.gradle.tasks.bundling.BootJar


group = "com.van1164"
version = "0.0.1-SNAPSHOT"

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.638")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.test {
    useJUnitPlatform()
}
