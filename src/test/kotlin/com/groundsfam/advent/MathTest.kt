package com.groundsfam.advent

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class MathTest : WordSpec({
    "divisors" should {
        "get list of divisors of a number" {
            divisors(12).toList() shouldBe listOf(12, 6, 4, 3, 2, 1)
        }
    }
})
