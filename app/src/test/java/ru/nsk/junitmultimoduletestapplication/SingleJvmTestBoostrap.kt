package ru.nsk.junitmultimoduletestapplication

import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import java.io.PrintWriter


import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan

class TeamCityTestListener : TestExecutionListener {

    override fun executionStarted(testIdentifier: TestIdentifier) {
        if (testIdentifier.isTest) {
            println("##teamcity[testStarted name='${escape(testIdentifier.displayName)}' captureStandardOutput='true']")
        } else {
            println("##teamcity[testSuiteStarted name='${escape(testIdentifier.displayName)}']")
        }
    }

    override fun executionFinished(testIdentifier: TestIdentifier, testExecutionResult: TestExecutionResult) {
        if (testIdentifier.isTest) {
            when (testExecutionResult.status) {
                TestExecutionResult.Status.SUCCESSFUL -> {
                    println("##teamcity[testFinished name='${escape(testIdentifier.displayName)}']")
                }
                TestExecutionResult.Status.FAILED -> {
                    val throwable = testExecutionResult.throwable.orElse(null)
                    val details = throwable?.let {
                        val writer = java.io.StringWriter()
                        throwable.printStackTrace(java.io.PrintWriter(writer))
                        escape(writer.toString())
                    } ?: ""
                    val message = throwable?.message ?: "No message"
                    println("##teamcity[testFailed name='${escape(testIdentifier.displayName)}' message='${escape(message)}' details='$details']")
                }
                TestExecutionResult.Status.ABORTED -> {
                    //For aborted tests, report them as ignored in TeamCity
                    println("##teamcity[testIgnored name='${escape(testIdentifier.displayName)}' message='Aborted']")
                }
            }
        } else { //for containers
            println("##teamcity[testSuiteFinished name='${escape(testIdentifier.displayName)}']")
        }
    }

    override fun executionSkipped(testIdentifier: TestIdentifier, reason: String) {
        println("##teamcity[testIgnored name='${escape(testIdentifier.displayName)}' message='${escape(reason)}']")
    }
    private fun escape(value: String): String {
        return value.replace("|", "||")
            .replace("'", "|'")
            .replace("\n", "|n")
            .replace("\r", "|r")
            .replace("[", "|[")
            .replace("]", "|]")
    }

    // Implement empty methods for other TestExecutionListener events
    override fun testPlanExecutionStarted(testPlan: TestPlan?) {}
    override fun testPlanExecutionFinished(testPlan: TestPlan?) {}
    override fun reportingEntryPublished(testIdentifier: TestIdentifier?, entry: ReportEntry?) {}
}

object SingleJvmTestBoostrap {
    @JvmStatic
    fun main(args: Array<String>) {
        val request: LauncherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                // *selectClasspathRoots(testClasses.map { Paths.get(it) }.toSet()).toTypedArray(),
                // selectClass("ru.nsk.junitmultimoduletestapplication.KotestExampleUnitTest"),
                // selectPackage("ru.nsk.junitmultimoduletestapplication"),
            )
            .build()

        val listener = SummaryGeneratingListener()
        val teamCityTestListener = TeamCityTestListener()


        LauncherFactory.openSession().use { session ->
            val launcher = session.launcher
            // Register a listener of your choice
            launcher.registerTestExecutionListeners(listener, teamCityTestListener)
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