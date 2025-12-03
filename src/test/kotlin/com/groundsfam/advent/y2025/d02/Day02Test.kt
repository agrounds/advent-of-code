package com.groundsfam.advent.y2025.d02

import com.groundsfam.advent.pow
import io.kotest.assertions.assertionCounterContextElement
import io.kotest.core.spec.style.WordSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import org.junit.jupiter.api.Assertions.assertEquals


fun bruteforceSumInvalidIDs(range: LongRange, numDigits: Int, repeatLen: Int): Long =
    range.sumOf { n ->
        val numStr = n.toString()
        when {
            numStr.length != numDigits -> 0
            numStr == numStr.substring(0..<repeatLen).repeat(numDigits / repeatLen) -> n
            else -> 0
        }
    }

class Day02Test : WordSpec({
    "sumInvalidIDs" should {
        "match brute-force values" {
            val rangeMax = 1_000_000L
            val rangeGen = Arb.long(1L..<rangeMax).flatMap { a ->
                Arb.long(a..<rangeMax).map { b -> a..b }
            }
            val paramGen = Arb.int(2..6).flatMap { numDigits ->
                Arb.int(1..<numDigits).map { repeatLen ->
                    numDigits to repeatLen
                }
            }.filter { (nd, rl) ->
                nd % rl == 0
            }
            checkAll(rangeGen, paramGen) { range, (numDigits, repeatLen) ->
                val expected = bruteforceSumInvalidIDs(range, numDigits, repeatLen)
                val actual = sumInvalidIDs(range, numDigits, repeatLen)
                actual == expected
            }
        }
    }
})
