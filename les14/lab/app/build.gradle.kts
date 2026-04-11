plugins {
    java
    war
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.spring.context)
    implementation(libs.spring.orm)
    implementation(libs.spring.data.jpa)
    implementation(libs.spring.webmvc)
    implementation(libs.spring.security.config)
    implementation(libs.spring.security.web)
    implementation(libs.thymeleaf.spring6)
    implementation(libs.hibernate.core)
    implementation(libs.hibernate.hikaricp)
    implementation(libs.jakarta.persistence.api)
    implementation(libs.hikari)
    implementation(libs.jackson.databind)
    implementation(libs.slf4j.api)
    implementation(libs.logback.core)
    implementation(libs.logback.classic)

    compileOnly(libs.jakarta.servlet.api)
    runtimeOnly(libs.h2)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
