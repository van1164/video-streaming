repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":security"))
    implementation(project(":main_module"))
    implementation(project(":comment"))
    implementation(project(":video"))

}

tasks.withType<Test> {
    useJUnitPlatform()
}