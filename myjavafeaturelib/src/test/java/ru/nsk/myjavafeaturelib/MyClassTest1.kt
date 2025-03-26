package ru.nsk.myjavafeaturelib

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MyClassTest1 : StringSpec({

    "java test" {
        2 + 2 shouldBe 4
    }

    "java test1" {
        2 + 2 shouldBe 4
    }
})