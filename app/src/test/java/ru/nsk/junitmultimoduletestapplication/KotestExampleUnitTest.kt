package ru.nsk.junitmultimoduletestapplication

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class KotestExampleUnitTest: StringSpec({
     "sdfds" {
          (1 + 2) shouldBe 3
     }

     "sdfdsd" {
         // error("asfffdf")
         R.string.app_name
     }
})