package advent.y2021.d06

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

// new fish spawn with timer of this value
const val newFishTimer = 8
// old fish timers restart at this value
const val oldFishTimer = 6


fun fishGrowth(initialFish: List<Int>, days: Int): Long {
    val fish = LongArray(newFishTimer + 1).apply {
        initialFish.forEach { this[it]++ }
    }
    repeat(days) {
        val newFish = fish[0]
        (1..newFishTimer).forEach { i ->
            fish[i-1] = fish[i]
        }
        fish[newFishTimer] = newFish
        fish[oldFishTimer] += newFish
    }

    return fish.sum()
}

fun main() {
    val initialFish = (DATAPATH / "2021/day06.txt").useLines { lines ->
        lines.first().split(",").map { it.toInt() }
    }
    println("Part one: ${fishGrowth(initialFish, 80)}")
    println("Part two: ${fishGrowth(initialFish, 256)}")
}
