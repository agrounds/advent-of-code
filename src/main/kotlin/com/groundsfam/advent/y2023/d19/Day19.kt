package com.groundsfam.advent.y2023.d19

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.max
import kotlin.math.min


sealed class Rule {
    /**
     * Checks if the part matches this rule's condition. If it does, this returns the name of the next workflow
     * to apply. If not, it returns null.
     *
     * This may return the special names "A" and "R" for final acceptance or rejection.
     */
    abstract fun check(partRating: PartRating): String?
}
data class ComparisonRule(val ratingName: Char, val value: Int, val lessThan: Boolean, val nextWorkflow: String) : Rule() {
    override fun check(partRating: PartRating): String? =
        if (lessThan xor (partRating[ratingName]!! > value)) {
            nextWorkflow
        } else {
            null
        }
}

data class DefaultRule(val nextWorkflow: String): Rule() {
    override fun check(partRating: PartRating): String = nextWorkflow
}

@JvmInline
value class PartRating(val ratingMap: Map<Char, Int>) : Map<Char, Int> by ratingMap

@JvmInline
value class PartRatingRange(val ratingRangeMap: Map<Char, IntRange>) : Map<Char, IntRange> by ratingRangeMap {
    fun copy(k: Char, v: IntRange): PartRatingRange =
        mutableMapOf<Char, IntRange>()
            .apply {
                ratingRangeMap.forEach { (k1, v1) ->
                    if (k1 == k) {
                        this[k] = v
                    } else {
                        this[k1] = v1
                    }
                }
            }
            .let(::PartRatingRange)
}

fun partIsAccepted(partRating: PartRating, workflows: Map<String, List<Rule>>): Boolean {
    var workflowName = "in"
    while (workflowName !in setOf("A", "R")) {
        workflowName = workflows[workflowName]!!
            .firstNotNullOf { it.check(partRating) }
    }
    return workflowName == "A"
}

fun acceptedRanges(workflows: Map<String, List<Rule>>): List<PartRatingRange> {
    val paths = mutableListOf<PartRatingRange>()

    data class QueueItem(val nextWorkflow: String, val partRatingRange: PartRatingRange)
    val queue = ArrayDeque<QueueItem>()
    val startingRange = (1..4000).let {
        PartRatingRange(mapOf('x' to it, 'm' to it, 'a' to it, 's' to it))
    }
    queue.add(QueueItem("in", startingRange))

    while (queue.isNotEmpty()) {
        val (nextWorkflow, prevRange) = queue.removeFirst()
        val workflow = workflows[nextWorkflow]!!
        workflow.fold(prevRange as PartRatingRange?) { range, rule ->
            if (range == null) null
            else when (rule) {
                is ComparisonRule -> {
                    val ratingName = rule.ratingName
                    val value = rule.value
                    val ratingRange = range[ratingName]!!
                    val appliedRange =
                        if (rule.lessThan) {
                            ratingRange.first..min(value - 1, ratingRange.last)
                        } else {
                            max(value + 1, ratingRange.first)..ratingRange.last
                        }
                            .takeIf { it.first <= it.last }
                    val unappliedRange =
                        if (rule.lessThan) {
                            max(value, ratingRange.first)..ratingRange.last
                        } else {
                            ratingRange.first..min(value, ratingRange.last)
                        }
                            .takeIf { it.first <= it.last }

                    if (appliedRange != null) {
                        when (rule.nextWorkflow) {
                            "R" -> {} // ignore it
                            "A" -> paths.add(range.copy(ratingName, appliedRange))
                            else -> queue.add(
                                QueueItem(rule.nextWorkflow, range.copy(ratingName, appliedRange))
                            )
                        }
                    }
                    unappliedRange?.let { range.copy(ratingName, it) }
                }

                is DefaultRule -> {
                    when (rule.nextWorkflow) {
                        "R" -> {} // ignore it
                        "A" -> paths.add(range)
                        else -> {
                            queue.add(QueueItem(rule.nextWorkflow, range))
                        }
                    }
                    range
                }
            }
        }
    }

    return paths
}

fun parseInput(path: Path): Pair<Map<String, List<Rule>>, List<PartRating>> {
    val workflows = mutableMapOf<String, List<Rule>>()
    val partRatings = mutableListOf<PartRating>()
    path.useLines { lines ->
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
                        .associate { it[0] to it.substring(2).toInt() }
                        .let(::PartRating)
                        .also(partRatings::add)
                }
            }
        }
    }

    return Pair(workflows, partRatings)
}

fun main() = timed {
    val (workflows, partRatings) = parseInput(DATAPATH / "2023/day19.txt")

    partRatings
        .filter { partIsAccepted(it, workflows) }
        .sumOf { it.values.sum() }
        .also { println("Part one: $it") }

    acceptedRanges(workflows)
        .sumOf { range ->
            range.values
                .fold(1L) { a, r ->
                    a * (r.last - r.first + 1)
                }
        }
        .also { println("Part two: $it") }
}
