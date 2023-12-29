package com.groundsfam.advent.y2023.d19

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


private sealed class Rule {
    /**
     * Checks if the part matches this rule's condition. If it does, this returns the name of the next workflow
     * to apply. If not, it returns null.
     *
     * This may return the special names "A" and "R" for final acceptance or rejection.
     */
    abstract fun check(partRating: PartRating): String?
}
private data class ComparisonRule(val ratingName: Char, val value: Int, val lessThan: Boolean, val nextWorkflow: String) : Rule() {
    override fun check(partRating: PartRating): String? {
        val rating = when (ratingName) {
            'x' -> partRating.x
            'm' -> partRating.m
            'a' -> partRating.a
            's' -> partRating.s
            else -> throw RuntimeException("Invalid ratingName: $ratingName")
        }

        return if (lessThan xor (rating > value)) {
            nextWorkflow
        } else {
            null
        }
    }
}

private data class DefaultRule(val nextWorkflow: String): Rule() {
    override fun check(partRating: PartRating): String = nextWorkflow
}

private data class PartRating(val x: Int, val m: Int, val a: Int, val s: Int)

private fun partIsAccepted(partRating: PartRating, workflows: Map<String, List<Rule>>): Boolean {
    var workflowName = "in"
    while (workflowName !in setOf("A", "R")) {
        workflowName = workflows[workflowName]!!
            .firstNotNullOf { it.check(partRating) }
    }
    return workflowName == "A"
}

fun main() = timed {
    val workflows = mutableMapOf<String, List<Rule>>()
    val partRatings = mutableListOf<PartRating>()
    (DATAPATH / "2023/day19.txt").useLines { lines ->
        var parsingWorkflows = true
        lines.forEach { line ->
            when {
                line.isBlank() -> {
                    parsingWorkflows = false
                }
                parsingWorkflows -> {
                    val (name, rawRules) = line.split("{")
                    workflows[name] = rawRules
                        .substring(0 until rawRules.length - 1)
                        .split(",")
                        .map { rawRule ->
                            val colonIdx = rawRule.indexOf(':').takeIf { it > -1 }
                            if (colonIdx != null) {
                                ComparisonRule(
                                    ratingName = rawRule[0],
                                    value = rawRule.substring(2 until colonIdx).toInt(),
                                    lessThan = rawRule[1] == '<',
                                    nextWorkflow = rawRule.substring(colonIdx + 1),
                                )
                            } else {
                                DefaultRule(rawRule)
                            }
                        }
                }
                else -> {
                    line.substring(1 until line.length - 1)
                        .split(',')
                        .map { it.substring(2).toInt() }
                        .let {
                            PartRating(it[0], it[1], it[2], it[3])
                        }
                        .also(partRatings::add)
                }
            }
        }
    }

    partRatings
        .filter { partIsAccepted(it, workflows) }
        .sumOf { it.x + it.m + it.a + it.s }
        .also { println("Part one: $it") }
}
