package com.groundsfam.advent.y2022.d19

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2022.d19.Resource.CLAY
import com.groundsfam.advent.y2022.d19.Resource.GEODE
import com.groundsfam.advent.y2022.d19.Resource.OBSIDIAN
import com.groundsfam.advent.y2022.d19.Resource.ORE
import kotlin.io.path.div
import kotlin.io.path.useLines

enum class Resource {
    ORE,
    CLAY,
    OBSIDIAN,
    GEODE,
}

data class ResourceAmount(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
    operator fun plus(other: ResourceAmount) = ResourceAmount(
        ore + other.ore,
        clay + other.clay,
        obsidian + other.obsidian,
        geode + other.geode,
    )
    operator fun minus(other: ResourceAmount) = ResourceAmount(
        ore - other.ore,
        clay - other.clay,
        obsidian - other.obsidian,
        geode - other.geode,
    )
    operator fun get(resourceType: Resource) = when (resourceType) {
        ORE -> ore
        CLAY -> clay
        OBSIDIAN -> obsidian
        GEODE -> geode
    }

    fun isGeq(other: ResourceAmount): Boolean =
        ore >= other.ore &&
            clay >= other.clay &&
            obsidian >= other.obsidian &&
            geode >= other.geode

    fun inc(resourceType: Resource): ResourceAmount = when (resourceType) {
        ORE -> copy(ore = ore + 1)
        CLAY -> copy(clay = clay + 1)
        OBSIDIAN -> copy(obsidian = obsidian + 1)
        GEODE -> copy(geode = geode + 1)
    }
}
typealias Blueprint = Map<Resource, ResourceAmount>
fun parseBlueprint(line: String): Blueprint {
    val parts = line.split(" ")
    return mapOf(
        ORE to ResourceAmount(ore = parts[6].toInt()),
        CLAY to ResourceAmount(ore = parts[12].toInt()),
        OBSIDIAN to ResourceAmount(ore = parts[18].toInt(), clay = parts[21].toInt()),
        GEODE to ResourceAmount(ore = parts[27].toInt(), obsidian = parts[30].toInt()),
    )
}

data class State(val resources: ResourceAmount, val robots: ResourceAmount, val timeLeft: Int)

fun maxGeodes(blueprint: Blueprint, timeLimit: Int): Int {
    var maxSpend = ResourceAmount()
    blueprint.values.forEach {
        maxSpend = ResourceAmount(
            maxOf(maxSpend.ore, it.ore),
            maxOf(maxSpend.clay, it.clay),
            maxOf(maxSpend.obsidian, it.obsidian),
        )
    }

    // map from a starting state to the max number of geodes that can be harvested from that state
    // this number includes previously mined geodes, not just new ones
    val dp = mutableMapOf<State, Int>()

    fun helper(resources: ResourceAmount, robots: ResourceAmount, timeLeft: Int): Int {
        if (timeLeft == 0) {
            return resources.geode
        }

        // throw away resources we can't possibly use with the time left
        fun canSpendMax(resourceType: Resource): Int =
            maxSpend[resourceType] + (maxSpend[resourceType] - robots[resourceType]) * (timeLeft - 1)
        val usefulResources = ResourceAmount(
            ore = minOf(resources.ore, canSpendMax(ORE)),
            clay = minOf(resources.clay, canSpendMax(CLAY)),
            obsidian = minOf(resources.obsidian, canSpendMax(OBSIDIAN)),
            resources.geode
        )
        val state = State(usefulResources, robots, timeLeft)
        if (state in dp) return dp[state]!!

        // we can build one robot this minute if there are sufficient resources and we do not already have
        // the max number of useful robots (equal to maxSpend.resourceType)
        // try building each robot and recursively find max geodes after making that choice
        val buildRobotOptions = Resource.entries.mapNotNull {
            val robotCost = blueprint[it]!!
            if (usefulResources.isGeq(robotCost) && (it == GEODE || robots[it] < maxSpend[it])) {
                helper(usefulResources - robotCost + robots, robots.inc(it), timeLeft - 1)
            } else {
                null
            }
        }
        // we could also choose to not build a robot this minute
        val dontBuildOption = helper(usefulResources + robots, robots, timeLeft - 1)

        return maxOf(dontBuildOption, buildRobotOptions.maxOrNull() ?: 0)
            .also {
                dp[state] = it
            }
    }

    // initial state: zero resources, one ore robot
    return helper(ResourceAmount(), ResourceAmount(ore = 1), timeLimit)
}


fun main() = timed {
    val blueprints = (DATAPATH / "2022/day19.txt").useLines { lines ->
        lines.map(::parseBlueprint).toList()
    }
    blueprints.mapIndexed { index, blueprint ->
        (index + 1) * maxGeodes(blueprint, 24)
    }
        .sum()
        .also { println("Part one: $it") }
    blueprints.take(3)
        .fold(1) { product, blueprint ->
            product * maxGeodes(blueprint, 32)
        }
        .also { println("Part two: $it") }
}
