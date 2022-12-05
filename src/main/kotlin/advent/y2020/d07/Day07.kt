package advent.y2020.d07

import advent.DATAPATH
import java.util.ArrayDeque
import java.util.Queue
import kotlin.io.path.div
import kotlin.io.path.useLines

private fun parseRule(line: String): Rule {
    val tokens = line.split(" ")

    return rule {
        bagColor = "${tokens[0]} ${tokens[1]}"

        for (i in 4 until tokens.size step 4) {
            if (tokens[i] != "no") {  // catch the case of "no other bags."
                addCanContain(tokens[i].toInt() to "${tokens[i + 1]} ${tokens[i + 2]}")
            }
        }
    }
}

private data class Rule(val bagColor: String, val canContain: List<Pair<Int, String>>) {
    class Builder {
        var bagColor: String? = null
        val canContain = mutableListOf<Pair<Int, String>>()

        fun addCanContain(pair: Pair<Int, String>) {
            canContain.add(pair)
        }

        fun build() = Rule(bagColor!!, canContain)
    }
}

private fun rule(block: Rule.Builder.() -> Unit): Rule =
    Rule.Builder().apply(block).build()


private fun findContainingBags(relations: Map<String, List<Rule>>, target: String): Set<String> {
    val ret = mutableSetOf<String>()
    val next: Queue<String> = ArrayDeque<String>().apply { add(target) }

    while (next.isNotEmpty()) {
        val color = next.poll()
        if (color == target || ret.add(color)) {
            next.addAll(relations[color]?.map { it.bagColor } ?: emptyList())
        }
    }

    return ret
}


private fun countContainedBags(rulesMap: Map<String, Rule>, target: String): Int {
    return rulesMap[target]!!.canContain.sumOf { (count, color) ->
        count * (1 + countContainedBags(rulesMap, color))
    }
}


fun main() {
    val rules = (DATAPATH / "2020/day07.txt").useLines { it.toList().map(::parseRule) }

    mutableMapOf<String, MutableList<Rule>>().let { map ->
        rules.forEach { rule ->
            rule.canContain.forEach { (_, bagColor) ->
                if (bagColor !in map) {
                    map[bagColor] = mutableListOf()
                }
                map[bagColor]!!.add(rule)
            }
        }

        findContainingBags(map, "shiny gold")
    }.run { println("Part one: $size") }

    rules.groupBy { it.bagColor }
        .mapValues { (_, v) -> v[0] }  // bagColor key is already unique
        .let { countContainedBags(it, "shiny gold") }
        .also { println("Part two: $it") }
}