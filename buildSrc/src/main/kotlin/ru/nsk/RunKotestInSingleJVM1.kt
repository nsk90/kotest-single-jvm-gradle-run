package ru.nsk

import org.gradle.api.*
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
// import org.gradle.kotlin.dsl.register
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension

abstract class RunKotestTestsInSingleJvm : DefaultTask() {

    @get:InputFiles
    abstract val rClassesDirs: ConfigurableFileCollection

    @get:InputFiles
    abstract val testClassesDirs: ConfigurableFileCollection

    @get:InputFiles
    abstract val kotestClasspath: ConfigurableFileCollection

    @TaskAction
    fun runTests() {
        project.javaexec {
            classpath = kotestClasspath + testClassesDirs + rClassesDirs
            // it.mainClass.set("ru.nsk.basetestlib.SingleJvmTestBoostrap") // our custom Boostrap class
            mainClass.set("io.kotest.engine.launcher.MainKt") // use kotest Boostrap class
            // it.args("--reporter", "teamcity") // to produce output for IDE integration
        }
    }
}

fun registerRunKotestInSingleJvmTask(project: Project) {
    val runKotestInSingleJvmTaskProvider =
        project.tasks.register("runKotestInSingleJvm", RunKotestTestsInSingleJvm::class.java)
    runKotestInSingleJvmTaskProvider.configure {
        val runKotestInSingleJvmTask = this
        project.subprojects {
            val subproject = this

            if (true || subproject.name == "baseapp") { // fixme remove, used for debug only

                // myjavafeaturelib, app, myfeaturelibrary
                if (subproject.plugins.hasPlugin("com.android.application")) {
                    println("android app module detected $subproject")
                    val androidExtension = subproject.extensions.getByType(AppExtension::class.java)
                    setupAndroidModule(true, subproject, runKotestInSingleJvmTask, androidExtension)
                } else if (subproject.plugins.hasPlugin("com.android.library")) {
                    println("android library module detected $subproject")

                    val androidExtension = subproject.extensions.getByType(LibraryExtension::class.java)
                    setupAndroidModule(false, subproject, runKotestInSingleJvmTask, androidExtension)
                } else if (subproject.plugins.hasPlugin("java-library")) {
                    println("java module detected $subproject")

                    val classPath = subproject.configurations.getByName("testRuntimeClasspath")
                    runKotestInSingleJvmTask.kotestClasspath.from(classPath)

                    val compileTask = subproject.tasks.findByName("compileTestKotlin")
                        ?: error("compile task not found")
                    runKotestInSingleJvmTask.testClassesDirs.from(compileTask.outputs.files)
                    runKotestInSingleJvmTask.dependsOn(compileTask)
                } else {
                    println("!!! Unknown module type detected ${subproject.name}")
                }
            }
        }
    }
}

private const val VARIANT = "debug" // Assuming debug variant
private const val VARIANT_CAPITALIZED = "Debug"
private const val FLAVOUR = "development"
private const val FLAVOUR_CAPITALIZED = "Development"

private fun setupAndroidModule(
    isApp: Boolean,
    subproject: Project,
    runKotestInSingleJvmTask: RunKotestTestsInSingleJvm,
    androidExtension: BaseExtension,
) {
    val (taskNameEntry, taskNameEntryCapitalized) =
        if (androidExtension.productFlavors.names.contains(FLAVOUR)) {
            "$FLAVOUR$VARIANT_CAPITALIZED" to "$FLAVOUR_CAPITALIZED$VARIANT_CAPITALIZED"
        } else {
            VARIANT to VARIANT_CAPITALIZED
        }

    val classPath = subproject.configurations.getByName("${taskNameEntry}UnitTestRuntimeClasspath")
    // to fix ambitious resolution error we need specify artifact type explicitly
    runKotestInSingleJvmTask.kotestClasspath.from(classPath.incoming.artifactView {
        attributes {
            val attributeContainer = this
            attributeContainer.attribute(
                ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE,
                ArtifactTypeDefinition.JAR_TYPE
            )
        }
    }.files, androidExtension.bootClasspath)

    val compileTask = subproject.tasks.findByName("compile${taskNameEntryCapitalized}UnitTestKotlin")
        ?: error("compile task not found")

//    subproject.tasks.all {
//        if (isApp) {
//
//            try {
//                println("app ${name} inputs: \n" + inputs.files.joinToString(separator = "\n") { it.path })
//            } catch (e: Exception) {
//                println("app ${name} inputs: not readable")
//            }
//
//            try {
//                println("app ${name} outputs: \n" + outputs.files.joinToString(separator = "\n") { it.path })
//            } catch (e: Exception) {
//                println("app ${name} outputs: not readable")
//            }
//        } else {
//            try {
//                println("library ${name} inputs: \n" + inputs.files.joinToString(separator = "\n") { it.path })
//            } catch (e: Exception) {
//                println("library ${name} inputs: not readable")
//
//            }
//
//            try {
//                println("library ${name} outputs: \n" + outputs.files.joinToString(separator = "\n") { it.path })
//            } catch (e: Exception) {
//                println("library ${name} outputs: not readable")
//            }
//        }
//    }

    runKotestInSingleJvmTask.testClassesDirs.from(compileTask.outputs.files)
    runKotestInSingleJvmTask.dependsOn(compileTask)


    if (isApp) {
        val resourcesTask = subproject.tasks.findByName("process${taskNameEntryCapitalized}Resources")
            ?: error("resources task not found")

        runKotestInSingleJvmTask.rClassesDirs.from(resourcesTask.outputs.files)
        runKotestInSingleJvmTask.dependsOn(resourcesTask)
    } else {

    }
}