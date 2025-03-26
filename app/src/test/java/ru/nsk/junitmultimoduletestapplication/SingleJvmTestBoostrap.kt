package ru.nsk.junitmultimoduletestapplication

import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter

object SingleJvmTestBoostrap {
    @JvmStatic
    fun main(args: Array<String>) {
        val request: LauncherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors( // empty selectors mean "select everything"
                // *selectClasspathRoots(testClasses.map { Paths.get(it) }.toSet()).toTypedArray(),
                // selectClass("ru.nsk.junitmultimoduletestapplication.KotestExampleUnitTest"),
                // selectPackage("ru.nsk.junitmultimoduletestapplication"),
            )
            .build()

        val listener = SummaryGeneratingListener()


        LauncherFactory.openSession().use { session ->
            val launcher = session.launcher
            // Register a listener of your choice
            launcher.registerTestExecutionListeners(listener)
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
            println("There were test failures.")
        } else {
            println("Tests passed successfully.")
        }
    }
}