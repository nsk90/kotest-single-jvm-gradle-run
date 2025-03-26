package ru.nsk.myjavafeaturelib

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MyClassTest : StringSpec({

    "java test" {
        2 + 2 shouldBe 4
    }
})