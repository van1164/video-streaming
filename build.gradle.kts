import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

allprojects{
	group = "com.van1164"
	version = "0.0.1-SNAPSHOT"
	repositories {
		mavenCentral()
	}
}

subprojects {
	apply {
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("org.jetbrains.kotlin.plugin.spring")
		plugin("org.jetbrains.kotlin.jvm")
		plugin("kotlin")
		plugin("kotlin-kapt")
	}
//	apply(plugin = "org.springframework.boot")
//	apply(plugin = "io.spring.dependency-management")
//	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
//	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
//	apply(plugin = "kotlin")
//	apply(plugin = "kotlin-kapt")


//	configurations {
//		compileOnly {
//			extendsFrom(configurations.annotationProcessor.get())
//		}
//	}
	java.sourceCompatibility = JavaVersion.VERSION_17
	java.targetCompatibility = JavaVersion.VERSION_17
	dependencies {
		testImplementation("org.jetbrains.kotlin:kotlin-test")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.springframework.boot:spring-boot-starter-webflux")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
		compileOnly("org.projectlombok:lombok")
		runtimeOnly("com.mysql:mysql-connector-j")
		annotationProcessor("org.projectlombok:lombok")

		testImplementation("io.projectreactor:reactor-test")
		implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
		implementation("com.github.jasync-sql:jasync-r2dbc-mysql:2.2.0")
		implementation("net.bramp.ffmpeg:ffmpeg:0.8.0")

		implementation("io.github.microutils:kotlin-logging:1.12.0")

		//security
		implementation("org.springframework.boot:spring-boot-starter-security")
		testImplementation("org.springframework.security:spring-security-test")

		//oAuth2
		implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
		implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")

		//thymleaf
		implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.2.3")

		//swagger
		implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
		implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.5.0")

		testImplementation("org.springframework.boot:spring-boot-starter-test") {
			exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		}
		testImplementation("org.junit.jupiter:junit-jupiter-api")
		testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict -Dfile.encoding=UTF-8"
			jvmTarget = "17"
		}
	}

	tasks.withType<Test> {
		systemProperty("file.encoding","UTF-8")
		useJUnitPlatform()
	}

}

