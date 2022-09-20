import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

ext {

}

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.12.RELEASE"
	kotlin("jvm") version "1.7.0"
	kotlin("plugin.spring") version "1.7.0"
}

group = "tw.elliot"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val mapstructVersion: String by project

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude("org.springframework.boot","spring-boot-starter-json")
	}
	implementation("org.springframework.boot:spring-boot-starter-validation")
	//implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.google.code.gson:gson")
	implementation("org.mapstruct:mapstruct:${mapstructVersion}")
	//implementation("org.projectlombok:lombok:1.18.20")
	//compileOnly("org.projectlombok:lombok")
	//developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
