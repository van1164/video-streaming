tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.638")
}
