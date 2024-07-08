tasks.getByName("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":security"))
    implementation(project(":video"))
}

