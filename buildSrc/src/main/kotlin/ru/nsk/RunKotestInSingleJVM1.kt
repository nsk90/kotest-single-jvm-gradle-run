package ru.nsk // You can use any package name you like

import org.gradle.api.*
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.register
 import org.gradle.api.attributes.Usage
 import org.gradle.kotlin.dsl.named // If using Gradle 7+

abstract class RunKotestInSingleJVM1 : DefaultTask() {

    @get:InputFiles
    abstract val testClassesDirs: ConfigurableFileCollection

    @get:InputFiles
    abstract val kotestClasspath: ConfigurableFileCollection

    @TaskAction
    fun runTests() {
        project.javaexec {
            classpath = kotestClasspath + testClassesDirs
            //mainClass.set("ru.nsk.basetestlib.SingleJvmTestBoostrap") // our custom Boostrap class
                 mainClass.set("io.kotest.engine.launcher.MainKt") // use kotest Boostrap class
            //     args("--reporter", "teamcity") // to produce output for IDE integration
        }
    }
}

// Helper function to register the task (for cleaner build.gradle.kts)
fun registerRunKotestTask(project: Project) {
    val runKotestInSingleJvmTaskProvider = project.tasks.register<RunKotestInSingleJVM1>("runKotestInSingleJVM1")
    runKotestInSingleJvmTaskProvider.configure {
        val runKotestInSingleJvmTask = this

        // Set up dependencies on subprojects' test compilation tasks
        project.subprojects {
            val subproject = this

            // subproject.afterEvaluate {
            println("afterEvaluate $name")
            //val runKotestInSingleJvmTask = runKotestInSingleJvmTaskProvider.get()

            // myjavafeaturelib, app, myfeaturelibrary
            if (subproject.plugins.hasPlugin("com.android.application")) {
                println("android app module detected $subproject")

                val variant = "debug" // Assuming debug variant
                val classPath = subproject.configurations.getByName("${variant}UnitTestRuntimeClasspath")
                runKotestInSingleJvmTask.kotestClasspath.from(classPath)

                val compileTask = subproject.tasks.findByName("compile${variant.capitalized()}UnitTestKotlin")
                    ?: error("compile task not found")
                runKotestInSingleJvmTask.testClassesDirs.from(compileTask.outputs.files) // only debugUnitTest is necessary
                runKotestInSingleJvmTask.dependsOn(compileTask)
            } else if (subproject.plugins.hasPlugin("com.android.library")) {
                println("android library module detected $subproject")

                val variant = "debug" // Assuming debug variant
                val classPath = subproject.configurations.getByName("${variant}UnitTestRuntimeClasspath")
                // to fix ambitious resolution error we need specify artifact type explicitly
                runKotestInSingleJvmTask.kotestClasspath.from(classPath.incoming.artifactView {
                    attributes {
                        attribute(ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE, ArtifactTypeDefinition.JAR_TYPE)
                    }
                }.files)

                val compileTask = subproject.tasks.findByName("compile${variant.capitalized()}UnitTestKotlin")
                    ?: error("compile task not found")
                runKotestInSingleJvmTask.testClassesDirs.from(compileTask.outputs.files) // only debugUnitTest is necessary
                runKotestInSingleJvmTask.dependsOn(compileTask)
            } else if (subproject.plugins.hasPlugin("java-library")) {
                println("java module detected $subproject")

                val classPath = subproject.configurations.getByName("testRuntimeClasspath")
                runKotestInSingleJvmTask.kotestClasspath.from(classPath)

                val compileTask =
                    subproject.tasks.findByName("compileTestKotlin") ?: error("compile task not found")
                runKotestInSingleJvmTask.testClassesDirs.from(compileTask.outputs.files)
                runKotestInSingleJvmTask.dependsOn(compileTask)
            } else {
                println("unknown module type detected")
            }
        }
    }
}

