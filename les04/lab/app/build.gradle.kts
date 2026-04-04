plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.spring.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.spring.context)
    implementation(libs.spring.aspects)
    implementation(libs.aspectj.weaver)
    implementation(libs.jakarta.annotation)
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
    workingDir = rootProject.projectDir
    jvmArgs("-Dfile.encoding=UTF-8")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
