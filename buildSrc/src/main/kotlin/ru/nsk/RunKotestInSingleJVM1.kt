package ru.nsk

import org.gradle.api.*
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.*
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.AppExtension

abstract class RunKotestTestsInSingleJvm : DefaultTask() {

    @get:InputFiles
    abstract val testsClasspath: ConfigurableFileCollection

    @TaskAction
    fun runTests() {
        project.javaexec {
            classpath = testsClasspath
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

            when {
                subproject.plugins.hasPlugin("com.android.application") -> setupAndroidModule(
                    AndroidModuleType.ANDROID_APPLICATION,
                    subproject,
                    runKotestInSingleJvmTask
                )
                subproject.plugins.hasPlugin("com.android.library") -> setupAndroidModule(
                    AndroidModuleType.ANDROID_LIBRARY,
                    subproject,
                    runKotestInSingleJvmTask
                )
                subproject.plugins.hasPlugin("java-library") -> {
                    val classPath = subproject.configurations.getByName("testRuntimeClasspath")
                    runKotestInSingleJvmTask.testsClasspath.from(classPath)

                    val compileTask = subproject.tasks.findByName("compileTestKotlin")
                        ?: error("compile task not found")
                    runKotestInSingleJvmTask.testsClasspath.from(compileTask.outputs.files)
                    runKotestInSingleJvmTask.dependsOn(compileTask)
                }
                else -> error("Unknown module type detected ${subproject.name}")
            }
        }
    }
}

private const val VARIANT = "debug" // Assuming debug variant
private const val VARIANT_CAPITALIZED = "Debug"
private const val FLAVOUR = "development"
private const val FLAVOUR_CAPITALIZED = "Development"

private enum class AndroidModuleType {
    ANDROID_APPLICATION,
    ANDROID_LIBRARY,
}

private fun setupAndroidModule(
    androidModuleType: AndroidModuleType,
    subproject: Project,
    runKotestInSingleJvmTask: RunKotestTestsInSingleJvm,
) {
    val androidExtension = when (androidModuleType) {
        AndroidModuleType.ANDROID_APPLICATION -> subproject.extensions.getByType(AppExtension::class.java)
        AndroidModuleType.ANDROID_LIBRARY -> subproject.extensions.getByType(LibraryExtension::class.java)
    }

    val (taskNameEntry, taskNameEntryCapitalized) =
        if (androidExtension.productFlavors.names.contains(FLAVOUR)) {
            "$FLAVOUR$VARIANT_CAPITALIZED" to "$FLAVOUR_CAPITALIZED$VARIANT_CAPITALIZED"
        } else {
            VARIANT to VARIANT_CAPITALIZED
        }

    val classPath = subproject.configurations.getByName("${taskNameEntry}UnitTestRuntimeClasspath")
    // to fix ambitious resolution error we need specify artifact type explicitly
    runKotestInSingleJvmTask.testsClasspath.from(classPath.incoming.artifactView {
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

    runKotestInSingleJvmTask.testsClasspath.from(compileTask.outputs.files)
    runKotestInSingleJvmTask.dependsOn(compileTask)

    val resourcesTaskName = when (androidModuleType) {
        AndroidModuleType.ANDROID_APPLICATION -> "process${taskNameEntryCapitalized}Resources"
        AndroidModuleType.ANDROID_LIBRARY -> "generate${taskNameEntryCapitalized}UnitTestStubRFile"
    }
    val resourcesTask = subproject.tasks.findByName(resourcesTaskName)
        ?: error("resources task not found")
    runKotestInSingleJvmTask.testsClasspath.from(resourcesTask.outputs.files)
    runKotestInSingleJvmTask.dependsOn(resourcesTask)
}