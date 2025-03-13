package ru.nsk // You can use any package name you like

import org.gradle.api.*
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.register //for gradle 8.x

abstract class RunKotestInSingleJVM1 : DefaultTask() { // Now a top-level class

    @get:InputFiles
    abstract val testClassesDirs: ConfigurableFileCollection

    @get:InputFiles
    abstract val kotestClasspath: ConfigurableFileCollection

    @TaskAction
    fun runTests() {

        println("hello runTests")
//            // Access the 'test' task of the 'app' module
//            val testTask = project(":app").tasks.named("testDebugUnitTest", Test::class.java).get()
//            testTask.testClassesDirs.map {
//                //    testClasses.add(it.absolutePath)
//            }

        //  println("configurations: " + kotestClasspath.joinToString { it.name })
        //  project.sourceSets.getByName("test").output.classesDirs
//        println("testRuntimeClasspath   " + project.configurations.getByName("testRuntimeClasspath"))
//
        project.javaexec {
            classpath = project.files(
                "/Users/nsk/projects/JunitMultimoduletestApplication/app/build/tmp/kotlin-classes/debugUnitTest/"
            ) + kotestClasspath + testClassesDirs
       //     mainClass.set("ru.nsk.junitmultimoduletestapplication.SingleJvmTestBoostrap") // our custom Boostrap class
            mainClass.set("io.kotest.engine.launcher.MainKt") // use kotest Boostrap class
            args("--reporter", "teamcity") // to produce output for IDE integration
        }
    }
}

// Helper function to register the task (for cleaner build.gradle.kts)
fun registerRunKotestTask(project: Project) {
    val runKotestInSingleJvmTask = project.tasks.register<RunKotestInSingleJVM1>("runKotestInSingleJVM1") {
        //sourceSets["test"].output.classesDirs
        // println("configurations1: " + project(":app").configurations.joinToString { it.name })
        // testClassesDirs.from(project(":app").sourceSets.getByName("test").output.classesDirs)
        kotestClasspath.from(project.project(":app").configurations.getByName("debugUnitTestRuntimeClasspath")) // fixme testRuntimeClasspath for java-library module
        //  dependsOn("testClasses")
    }

    // Set up dependencies on subprojects' test compilation tasks
    project.subprojects { // Iterate through all subprojects
        //afterEvaluate { // Important: Defer evaluation until after the subproject is configured
        tasks.whenTaskAdded { //when a task added to the subproject. this avoids problems is a task does not exist
            val task = this
            if (task.name == "testClasses" || // For Java/Kotlin modules
                task.name == "compileDebugUnitTestKotlin" || // For Android modules
                task.name == "compileReleaseUnitTestKotlin"  // fixme check all flavors
            ) {
                runKotestInSingleJvmTask.configure {
                    dependsOn(task)
                }
            }
        }
        // }
    }
}

