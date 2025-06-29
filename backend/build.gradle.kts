plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.voroby"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/p-vorobyev/*")
        credentials {
            username = System.getenv("GIT_HUB_LOGIN")
            password = System.getenv("GIT_HUB_TOKEN")
        }
    }
}

dependencies {
    val lombokVersion = "1.18.30"
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("dev.voroby:spring-boot-starter-telegram:1.17.0")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.bootJar {
    archiveFileName = "backend.jar"
}
