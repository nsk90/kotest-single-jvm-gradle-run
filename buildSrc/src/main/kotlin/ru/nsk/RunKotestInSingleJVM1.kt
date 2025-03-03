package ru.nsk // You can use any package name you like

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.testing.Test
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.LoggingListener
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.File
import java.io.PrintWriter
import java.net.URL
import java.net.URLClassLoader
import org.gradle.internal.classloader.VisitableURLClassLoader
import java.nio.file.Paths

abstract class RunKotestInSingleJVM1 : DefaultTask() { // Now a top-level class

    @TaskAction
    fun runTests() {
        println("doLast runAllTestsInSingleJVM")

        System.setProperty("kotest.framework.classpath.scanning.autoscan.disable", "true");

        val buildscriptClasspath = project.buildscript.configurations.getByName("classpath")
        // println("buildscriptClasspath " + buildscriptClasspath.joinToString { it.absolutePath })

        val kotestEngine = buildscriptClasspath.find { it.name.contains("kotest-framework-engine-jvm") }
        if (kotestEngine == null) {
            println("Kotest Engine not found on buildscript classpath")
        } else {
            println("Kotest Engine detected")
        }

        val testClasses = mutableListOf<String>()

        testClasses.add("/Users/nsk/projects/JunitMultimoduletestApplication/app/build/tmp/kotlin-classes/debugUnitTest/")

//            // Access the 'test' task of the 'app' module
//            val testTask = project(":app").tasks.named("testDebugUnitTest", Test::class.java).get()
//            testTask.testClassesDirs.map {
//                //    testClasses.add(it.absolutePath)
//            }

        val urls = mutableListOf<URL>()
        urls.addAll(testClasses.map { File(it).toURI().toURL() })

        // first the classloader tries to delegate class loading to its parent,
        // only if it fails it tries to load the class itself
        println("Thread.currentThread().contextClassLoader " + Thread.currentThread().contextClassLoader)
        println("this::class.java.classLoader " + this::class.java.classLoader)
        println("are system classloaders the same instance ${this::class.java.classLoader === Thread.currentThread().contextClassLoader}")

        // use internal gradle api
        val sysURLClassLoader = Thread.currentThread().contextClassLoader as VisitableURLClassLoader
        urls.forEach {
            sysURLClassLoader.addURL(it)
        }
        RunTests().run()

    }
}

private class RunTests {
    fun run() {

        try {
            val testClass = Class.forName("ru.nsk.junitmultimoduletestapplication.KotestExampleUnitTest")
            println("1testClass " + testClass)
        } catch (e: Throwable) {
            println("cant load class 1 KotestExampleUnitTest")
        }

        try {
            val launcherFactory = this.javaClass.classLoader.loadClass("org.junit.platform.launcher.core.LauncherFactory")
            println("launcherFactory's classLoader " + launcherFactory.classLoader)
            println("URLClassLoader  " + this.javaClass.classLoader)
            println("LauncherFactory  " + LauncherFactory::class.java.classLoader)
        } catch (e: Throwable) {
            println("cant load class  LauncherFactory")
            throw e
        }


        val request: LauncherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                //*selectClasspathRoots(testClasses.map { Paths.get(it) }.toSet()).toTypedArray(),
                //selectClass(classLoader, "ru.nsk.junitmultimoduletestapplication.ExampleUnitTest"),
                selectClass("ru.nsk.junitmultimoduletestapplication.KotestExampleUnitTest"),
                //   selectPackage("ru.nsk.junitmultimoduletestapplication"),
            )
            .build()

        val listener = SummaryGeneratingListener()
        val listenerLogger = LoggingListener.forJavaUtilLogging()


        LauncherFactory.openSession().use { session ->
            val launcher = session.launcher
            // Register a listener of your choice
            launcher.registerTestExecutionListeners(listener, listenerLogger)
            // Discover tests and build a test plan
            val testPlan = launcher.discover(request)
            // Execute test plan
            launcher.execute(testPlan)
        }

        // Print test summary
        val summary = listener.summary
        val writer = PrintWriter(System.out)
        summary.printTo(writer)
        writer.flush()

        //   Fail the task if there are test failures
        if (summary.totalFailureCount > 0) {
            throw GradleException("There were test failures.")
        }

    }
}

fun dynamicloading(
    classLoader: ClassLoader,
    request: LauncherDiscoveryRequest // todo load dynamically also?
) {
    val summaryGeneratingListenerClass =
        classLoader.loadClass("org.junit.platform.launcher.listeners.SummaryGeneratingListener")
    val listener = summaryGeneratingListenerClass.getDeclaredConstructor().newInstance()

    val launcherFactoryClass = classLoader.loadClass("org.junit.platform.launcher.core.LauncherFactory")

    val staticOpenSessionMethod = launcherFactoryClass.getMethod("openSession")
    println("staticOpenSessionMethod")
    val session = staticOpenSessionMethod.invoke(null)
    println("session")
    val getLauncherMethod = session.javaClass.getMethod("getLauncher")
    getLauncherMethod.isAccessible = true
    println("getLauncherMethod")
    val launcher = getLauncherMethod.invoke(session)
    println("launcher")

    val launcherClass = classLoader.loadClass("org.junit.platform.launcher.Launcher")
    val testExecutionListenerClass = classLoader.loadClass("org.junit.platform.launcher.TestExecutionListener")

    println("launcherClass")
    val registerTestExecutionListenersMethod =
        launcherClass.getMethod("registerTestExecutionListeners", testExecutionListenerClass.arrayType())
    registerTestExecutionListenersMethod.isAccessible = true
    println("registerTestExecutionListenersMethod")

    //    val listenerArray: Array<out TestExecutionListener> = arrayOf(listener)
    //   registerTestExecutionListenersMethod.invoke(launcher, listener)
    println("registerTestExecutionListeners call")
    val launcherDiscoveryRequestClass = classLoader.loadClass("org.junit.platform.launcher.LauncherDiscoveryRequest")

    val discoverMethod = launcherClass.getMethod("discover", launcherDiscoveryRequestClass)
    println("discoverMethod")
    val testPlan = discoverMethod.invoke(launcher, request)
    println("testPlan")
    val executeMethod = launcherClass.getMethod("execute")
    println("executeMethod")
    executeMethod.invoke(launcher, testPlan)
    println("executeMethod invoke")
}
