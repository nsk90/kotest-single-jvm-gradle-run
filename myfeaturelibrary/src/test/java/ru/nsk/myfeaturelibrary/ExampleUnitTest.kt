package ru.nsk.myfeaturelibrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.test.logging.error
import io.kotest.matchers.shouldBe
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest : StringSpec({

    "this is super test" {
        1 shouldBe 1
    }

    "this is not" {
        error( "ho ho ho")
    }
})