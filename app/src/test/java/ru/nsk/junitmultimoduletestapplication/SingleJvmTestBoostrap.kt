package ru.nsk.junitmultimoduletestapplication

import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.LoggingListener
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter

object SingleJvmTestBoostrap {
    @JvmStatic
    fun main(args: Array<String>) {
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
        PrintWriter(System.out).use {
            summary.printTo(it)
        }

        //   Fail the task if there are test failures
        if (summary.totalFailureCount > 0) {
            error("There were test failures.")
        }
    }
}