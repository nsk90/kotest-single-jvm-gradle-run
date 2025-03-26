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

dependencies {
    implementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    implementation("org.junit.platform:junit-platform-launcher:1.11.4") // to run tests fom jvm
    implementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
}