import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.TestPlan
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.LoggingListener
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter
import java.net.URLClassLoader
import java.nio.file.Paths
import org.gradle.api.tasks.testing.Test as Test1
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import java.net.URL
import ru.nsk.RunKotestInSingleJVM1

buildscript {
    dependencies {
        classpath("org.junit.platform:junit-platform-launcher:1.9.3")
        //classpath("org.junit.jupiter:junit-jupiter-engine:5.11.4")
        classpath("io.kotest:kotest-runner-junit5-jvm:5.9.1") // Or your Kotest version
        //classpath("org.junit.jupiter:junit-jupiter-api:5.11.4")
       // classpath(libs.junit)

    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    //alias(libs.plugins.kotlin.compose) apply false
}

tasks.register<RunKotestInSingleJVM1>("runKotestInSingleJVM1") {
   // dependsOn("testClasses") // Ensure test classes are compiled
}

//tasks.register("runAllTestsInSingleJVM") {
//    group = "verification"
//    description = "Runs all unit tests from all modules in a single JVM"
//
//    println("body runAllTestsInSingleJVM")
//    println("subprojects.size ${subprojects.size}")
//
//    doLast {
//
//    }
//}