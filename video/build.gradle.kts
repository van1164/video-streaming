
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":util"))
    implementation(project(":user"))
    implementation(project(":security"))
    implementation(project(":main_module"))
    implementation(project(":comment"))

}

tasks.withType<Test> {
    useJUnitPlatform()
}