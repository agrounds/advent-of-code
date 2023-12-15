package com.groundsfam.advent.y2021.d14

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


// Note about this problem: I verified that there are
// exactly 10 letters that occur in the (non-example) input,
// and there are 100 pair insertion rules, meaning that
// every pair of letters produces a new letter at every step.

private class Solution(private val insertionRules: Map<String, Char>, template: String) {
    private var pairCounts: Map<String, Long>
    private val lastChar = template.last()
    init {
        val initCounts = mutableMapOf<String, Long>()
        (1 until template.length).forEach { i ->
            val pair = template.substring(i - 1..i)
            initCounts[pair] = (initCounts[pair] ?: 0) + 1
        }
        pairCounts = initCounts
    }

    fun step() {
        val nextCounts = mutableMapOf<String, Long>()
        pairCounts.forEach { (pair, count) ->
            val newChar = insertionRules[pair]!!
            listOf("${pair[0]}$newChar", "$newChar${pair[1]}").forEach { newPair ->
                nextCounts[newPair] = (nextCounts[newPair] ?: 0) + count
            }
        }
        pairCounts = nextCounts
    }

    fun mostMinusLeastCommon(): Long {
        val charCounts = mutableMapOf<Char, Long>()
        pairCounts.forEach { (pair, count) ->
            val c = pair[0]
            charCounts[c] = (charCounts[c] ?: 0) + count
        }
        charCounts[lastChar] = (charCounts[lastChar] ?: 0) + 1

        return charCounts.values.let { counts ->
            counts.maxOrNull()!! - counts.minOrNull()!!
        }
    }
}

fun main() = timed {
    val solution =
        (DATAPATH / "2021/day14.txt").useLines { lines ->
            val rules = mutableMapOf<String, Char>()
            var tmpl: String? = null

            lines.forEachIndexed { i, line ->
                if (i == 0) {
                    tmpl = line
                }
                if (i > 1) {
                    rules[line.substring(0..1)] = line[6]
                }
            }
            Solution(rules, tmpl!!)
        }

    repeat(10) {
        solution.step()
    }
    println("Part one: ${solution.mostMinusLeastCommon()}")
    repeat(30) {
        solution.step()
    }
    println("Part two: ${solution.mostMinusLeastCommon()}")
}
