plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
dependencies {
    testImplementation(project(":basetestlib"))
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
}