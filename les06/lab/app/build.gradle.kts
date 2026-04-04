plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.spring.context)
    implementation(libs.spring.jdbc)
    implementation(libs.h2)
    implementation(libs.logback.classic)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "ru.bsuedu.cad.lab.App"
}

tasks.named<JavaExec>("run") {
    jvmArgs("-Dfile.encoding=UTF-8")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
