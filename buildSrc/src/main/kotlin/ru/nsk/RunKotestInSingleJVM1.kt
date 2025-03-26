package ru.nsk // You can use any package name you like

import org.gradle.api.*
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register //for gradle 8.x

abstract class RunKotestInSingleJVM1 : DefaultTask() { // Now a top-level class

    @get:InputFiles
    abstract val testClassesDirs: ConfigurableFileCollection

    @get:InputFiles
    abstract val kotestClasspath: ConfigurableFileCollection

    @TaskAction
    fun runTests() {

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
            classpath = kotestClasspath + testClassesDirs
            // + project.files(
                // "/Users/nsk/projects/JunitMultimoduletestApplication/app/build/tmp/kotlin-classes/debugUnitTest/"
            //)
            mainClass.set("ru.nsk.junitmultimoduletestapplication.SingleJvmTestBoostrap") // our custom Boostrap class
            //     mainClass.set("io.kotest.engine.launcher.MainKt") // use kotest Boostrap class
            //     args("--reporter", "teamcity") // to produce output for IDE integration
        }
    }
}

// Helper function to register the task (for cleaner build.gradle.kts)
fun registerRunKotestTask(project: Project) {
    val runKotestInSingleJvmTaskProvider = project.tasks.register<RunKotestInSingleJVM1>("runKotestInSingleJVM1")
    //sourceSets["test"].output.classesDirs

    runKotestInSingleJvmTaskProvider.configure {
        val runKotestInSingleJvmTask = this

        // Set up dependencies on subprojects' test compilation tasks
        project.subprojects {
            val subproject = this
            if (subproject.extensions.findByName("android") != null) {
                println("android module detected")

                val variant = "debug" // Assuming debug variant
                val classPath = subproject.configurations.getByName("${variant}UnitTestRuntimeClasspath")
                runKotestInSingleJvmTask.kotestClasspath.from(classPath)

                val compileTask = subproject.tasks.findByName("compile${variant.capitalized()}UnitTestKotlin") ?: error(
                    "compile task not found"
                )
                runKotestInSingleJvmTask.testClassesDirs.from(compileTask.outputs.files) // only debugUnitTest is necessary
                runKotestInSingleJvmTask.dependsOn(compileTask)
            } else if (subproject.extensions.findByName("java") != null) {
                println("java module detected")

                val classPath = subproject.configurations.getByName("testRuntimeClasspath")
                runKotestInSingleJvmTask.kotestClasspath.from(classPath)

                val compileTask = subproject.tasks.findByName("testClasses") ?: error("compile task not found")
                runKotestInSingleJvmTask.dependsOn(compileTask)
            }
        }
    }
}

