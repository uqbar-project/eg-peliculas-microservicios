import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   id("org.springframework.boot") version "3.2.2"
   id("io.spring.dependency-management") version "1.1.4"
   kotlin("jvm") version "1.9.22"
   kotlin("plugin.spring") version "1.9.22"
   kotlin("plugin.jpa") version "1.9.22"
   jacoco
}

group = "org.uqbar"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
   mavenCentral()
}

dependencies {
   developmentOnly("org.springframework.boot:spring-boot-devtools")

   // básicos de cualquier proyecto Spring Boot
   implementation("org.springframework.boot:spring-boot-starter-web")
   implementation("org.springframework.boot:spring-boot-starter-hateoas")
   implementation("org.springframework.boot:spring-boot-starter-data-rest")
   implementation("org.springframework.boot:spring-boot-starter-validation")
   implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
   implementation("org.jetbrains.kotlin:kotlin-reflect")

   // conexión a la base de datos
   implementation("org.springframework.boot:spring-boot-starter-data-jpa")
   implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

   // testing
   testImplementation("org.springframework.boot:spring-boot-starter-test")
   testRuntimeOnly("org.junit.platform:junit-platform-launcher")

   // logging
   implementation("org.springframework.boot:spring-boot-starter-log4j2")
   modules {
      module("org.springframework.boot:spring-boot-starter-logging") {
         replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
      }
   }

   // security
   implementation("org.springframework.boot:spring-boot-starter-security")

   // graphql
   implementation("org.springframework.boot:spring-boot-starter-graphql")

   // microservicios
   implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")

   // testing
   testImplementation("org.springframework.boot:spring-boot-starter-test")
   testImplementation("org.mockito:mockito-core:5.10.0")
   testImplementation("io.projectreactor:reactor-test")
   testImplementation("org.springframework.graphql:spring-graphql-test")
   testImplementation("com.github.tomakehurst:wiremock-standalone:3.0.1")
}

tasks.withType<KotlinCompile> {
   kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = "21"
   }
}

tasks.withType<Test> {
   useJUnitPlatform()
}

tasks.test {
   finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
   dependsOn(tasks.test)
}

jacoco {
   toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
   classDirectories.setFrom(
      files(classDirectories.files.map {
         fileTree(it) {
            exclude("**/config/**", "**/entity/**", "**/*Application*.*", "**/ServletInitializer.*")
         }
      })
   )
   reports {
      xml.required.set(true)
      csv.required.set(false)
      html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
   }
}

tasks.register("runOnGitHub") {
   dependsOn("jacocoTestReport")
   group = "custom"
   description = "$ ./gradlew runOnGitHub # runs on GitHub Action"
}