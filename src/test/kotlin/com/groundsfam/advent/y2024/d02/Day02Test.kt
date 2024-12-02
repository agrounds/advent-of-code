package com.groundsfam.advent.y2024.d02

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Day02Test : WordSpec({
    "2024 Day02" should {
        "identify safe lists as safe" {
            isSafe(listOf(1, 2, 3), true) shouldBe true
            isSafe(listOf(7, 6, 4, 2, 1), true) shouldBe true
            isSafe(listOf(1, 3, 6, 7, 9), true) shouldBe true
        }
        "tolerate one bad level change" {
            isSafe(listOf(1, 8, 2), true) shouldBe true
            isSafe(listOf(1, 8, 1), true) shouldBe false
            isSafe(listOf(1, 8, 2, 3), true) shouldBe true
            isSafe(listOf(1, 8, 7, 6), true) shouldBe true
            isSafe(listOf(1, 8, 9, 10), true) shouldBe true
            isSafe(listOf(1, 2, 8, 4, 5), true) shouldBe true
            isSafe(listOf(1, 3, 2, 1), true) shouldBe true
            isSafe(listOf(1, 3, 2, 3), true) shouldBe true
            isSafe(listOf(1, 3, 2, 4), true) shouldBe true
            isSafe(listOf(1, 2, 4, 3), true) shouldBe true
            isSafe(listOf(1, 2, 4, 2), true) shouldBe true
            isSafe(listOf(1, 2, 4, 3, 4), true) shouldBe true
            isSafe(listOf(1, 2, 5, 3, 4), true) shouldBe true
            isSafe(listOf(1, 3, 2, 4, 5), true) shouldBe true
            isSafe(listOf(8, 6, 4, 4, 1), true) shouldBe true
            isSafe(listOf(65, 64, 68, 71, 72, 75), true) shouldBe true
        }
        "not tolerate reports needing multiple removals" {
            isSafe(listOf(1, 2, 7, 8, 9), true) shouldBe false
            isSafe(listOf(9, 7, 6, 2, 1), true) shouldBe false
        }
    }
})
