import ru.nsk.registerRunKotestTask

buildscript {
    dependencies {
       // classpath("org.junit.platform:junit-platform-launcher:1.9.3")
        //classpath("org.junit.jupiter:junit-jupiter-engine:5.11.4")
       //classpath("io.kotest:kotest-runner-junit5-jvm:5.9.1") // Or your Kotest version
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

registerRunKotestTask()


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