package advent.y2020.d19

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

data class Key(val from: Int, val to: Int, val rule: Int)

class Solver(private val rules: Map<Int, Rule>) {
    private val dp = mutableMapOf<Key, Boolean>()
    private var string: String = ""

    fun matches(string: String): Boolean {
        this.string = string

        return checkMatches(Key(0, string.length, 0))
    }

    private fun checkMatches(key: Key): Boolean {
        if (key in dp) return dp[key]!!
        val (from, to, rule) = key

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
            checkMatches(Key(from, mid, rules[0])) && checkMatchesSequence(mid, to, rules.subList(1, rules.size))
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
        Solver(rules).matches(it)
    }.also { println("Part one: $it") }
}
