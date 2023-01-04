package advent.y2022.d19

import advent.DATAPATH
import advent.y2022.d19.Resource.*
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

fun maxGeodes(blueprint: Blueprint, timeLimit: Int): Int {
    fun helper(resources: ResourceAmount, robots: ResourceAmount, timeLeft: Int): Int {
        if (timeLeft == 0) {
            return resources.geode
        }

        // we can build one robot this minute if there are sufficient resources
        // try building each robot and recursively find max geodes after making that choice
        val buildRobotOptions = Resource.values().mapNotNull {
            val robotCost = blueprint[it]!!
            if (resources.isGeq(robotCost)) {
                helper(resources - robotCost + robots, robots.inc(it), timeLeft - 1)
            } else {
                null
            }
        }
        // we could also choose to not build a robot this minute
        val dontBuildOption = helper(resources + robots, robots, timeLeft - 1)

        return maxOf(dontBuildOption, buildRobotOptions.maxOrNull() ?: 0)
    }

    // initial state: zero resources, one ore robot
    return helper(ResourceAmount(), ResourceAmount(ore = 1), timeLimit)
}


fun main() {
    val blueprints = (DATAPATH / "2022/day19-example.txt").useLines { lines ->
        lines.map(::parseBlueprint).toList()
    }
    blueprints.mapIndexed { index, blueprint ->
        (index + 1) * maxGeodes(blueprint, 24)
    }
        .sum()
        .also { println("Part one: $it") }

}
