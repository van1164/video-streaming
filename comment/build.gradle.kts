import org.springframework.boot.gradle.tasks.bundling.BootJar


repositories {
    mavenCentral()
}
val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":security"))
    implementation(project(":video"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
