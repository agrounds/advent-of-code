package com.groundsfam.advent.y2020.d19

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

class Solver(private val rules: Map<Int, Rule>, private val string: String, private val partTwo: Boolean = false) {
    data class Key(val from: Int, val to: Int, val rule: Int)

    private val dp = mutableMapOf<Key, Boolean>()

    fun matches(): Boolean {
        return checkMatches(0, string.length, 0)
    }

    private fun checkMatches(from: Int, to: Int, rule: Int): Boolean {
        val key = Key(from, to, rule)
        if (key in dp) return dp[key]!!

        if (partTwo) {
            if (rule == 8) {
                return (from != to && checkMatches8(from, to))
                    .also { dp[key] = it }
            }
            if (rule == 11) {
                return (from != to && checkMatches11(from, to))
                    .also { dp[key] = it }
            }
        }

        return when (val r = rules[rule]) {
            is LiteralRule ->
                string.substring(from, to) == r.literal
            is SequenceRule ->
                checkMatchesSequence(from, to, r.rules)
            is OrRule ->
                checkMatchesSequence(from, to, r.leftRules) || checkMatchesSequence(from, to, r.rightRules)
            else ->
                throw RuntimeException("Rule $rule not found")
        }.also {
            dp[key] = it
        }
    }

    private fun checkMatchesSequence(from: Int, to: Int, rules: List<Int>): Boolean {
        if (rules.isEmpty()) {
            // if we've found matches on all the rules, the sequence match was successful
            // iff we used the whole string
            return from == to
        }
        return (from+1..to).any { mid ->
            checkMatches(from, mid, rules[0]) && checkMatchesSequence(mid, to, rules.subList(1, rules.size))
        }
    }

    // special case: in part two, the rule is
    //   8: 42 | 8 42
    // which means "any number of 42s"
    private fun checkMatches8(from: Int, to: Int): Boolean {
        if (from == to) return true
        return (from+1..to).any { mid ->
            checkMatches(from, mid, 42) && checkMatches8(mid, to)
        }
    }

    // special case: in part two, the rule is
    //   11: 42 31 | 42 11 31
    // which means any number of 42s, followed by the same number of 31s
    private fun checkMatches11(from: Int, to: Int): Boolean {
        if (from == to) return true
        return (from+1 until to).any { mid1 ->
            checkMatches(from, mid1, 42) && (mid1 until to).any { mid2 ->
                checkMatches11(mid1, mid2) && checkMatches(mid2, to, 31)
            }
        }
    }
}


fun main() {
    val (rules, messages) = (DATAPATH / "2020/day19.txt")
        .useLines { it.toList() }
        .let { lines ->
            (lines
                .takeWhile { it.isNotBlank() }
                .let { RuleParser(it).parsedRules }
                to lines.takeLastWhile { it.isNotBlank() })
        }
    messages.count {
        Solver(rules, it).matches()
    }.also { println("Part one: $it") }
    messages.count {
        Solver(rules, it, partTwo = true).matches()
    }.also { println("Part two: $it") }
}
