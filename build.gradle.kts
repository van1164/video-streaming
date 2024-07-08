import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("plugin.spring") version "1.9.22" apply false
	kotlin("jvm") version "1.9.22"
}

tasks.getByName("bootJar") {
	enabled = false
}

tasks.getByName("jar") {
	enabled = true
}


buildscript {
	repositories {
		mavenCentral()
	}
}

allprojects{
	group = "com.van1164"
	version = "0.0.1-SNAPSHOT"


	repositories {
		mavenCentral()
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}


	tasks.withType<JavaCompile>{
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "17"
		}
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

	dependencies {
		testImplementation("org.jetbrains.kotlin:kotlin-test")
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		implementation("org.springframework.boot:spring-boot-starter-webflux")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
		compileOnly("org.projectlombok:lombok")
		runtimeOnly("com.mysql:mysql-connector-j")
		annotationProcessor("org.projectlombok:lombok")

		testImplementation("io.projectreactor:reactor-test")

		implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
		implementation("io.asyncer:r2dbc-mysql:1.1.0")

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

		testImplementation("org.springframework.boot:spring-boot-starter-test")

		testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.0.20")
		testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation:1.0.20")

	}

}