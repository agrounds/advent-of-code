package advent.y2020

import kotlin.io.path.div
import kotlin.io.path.useLines

private fun joltDifferences(adapters: List<Int>): Map<Int, Int> =
    mutableMapOf<Int, Int>().apply {
        adapters.forEachIndexed { i, num ->
            if (i == 0) {
                this[num] = 1
            } else {
                val diff = num - adapters[i - 1]
                this[diff] = getOrDefault(diff, 0) + 1
            }
        }
        this[3] = getOrDefault(3, 0) + 1
    }


private fun countArrangements(adapters: List<Int>): Long {
    val arrangementCounts = mutableMapOf(0 to 1L)

    adapters.forEachIndexed { i, adapter ->
        val arrangements = arrangementCounts[adapter]!!
        repeat(3) { n ->
            if (i + n + 1 < adapters.size) {
                val nextAdapter = adapters[i + n + 1]
                if (nextAdapter - adapter <= 3) {
                    arrangementCounts[nextAdapter] = arrangementCounts.getOrDefault(nextAdapter, 0) + arrangements
                }
            }
        }
    }
    return arrangementCounts[adapters.last()]!!
}


fun main() {
    val adapters = (DATAPATH / "day10.txt").useLines { lines ->
        lines.map { it.toInt() }.toList()
    }.sorted()

    joltDifferences(adapters).let {
        it.getOrDefault(1, 0) * it.getOrDefault(3, 0)
    }.also { println("Part one: $it") }

    mutableListOf(0).apply {
        addAll(adapters)
        add(adapters.last() + 3)
    }.let(::countArrangements)
        .also { println("Part two: $it") }
}
