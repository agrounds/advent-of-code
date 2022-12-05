package advent.y2022.d03

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


fun priority(item: Char): Int =
    if (item in 'a'..'z') {
        item - 'a' + 1
    } else {  // it's between 'A' and 'Z'
        item - 'A' + 27
    }


fun commonItem(rucksack: String): Char {
    val first = rucksack.substring(0, rucksack.length / 2)
    val second = rucksack.substring(rucksack.length / 2, rucksack.length)
    val common = first.toSet() intersect second.toSet()
    assert(common.size == 1) { "Found more/less than one common item: $common" }
    return common.toList()[0]
}


fun badgeItem(group: List<String>): Char =
    group
        .map { it.toSet() }
        .reduce { a, b -> a intersect b }
        .also { assert(it.size == 1) { "Expected only one common character, but got $it" } }
        .let { it.toList()[0] }


fun main() {
    val rucksacks = (DATAPATH / "2022/day03.txt").useLines { lines ->
        lines.toList()
    }
    rucksacks
        .sumOf { priority(commonItem(it)) }
        .also { println("Priority sum = $it") }
    rucksacks
        .chunked(3)
        .onEach { assert(it.size == 3) { "Groups should all be size 3, but got $it" } }
        .sumOf { priority(badgeItem(it)) }
        .also { println("Badge sum = $it") }
}