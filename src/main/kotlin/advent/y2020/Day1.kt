package advent.y2020

import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.useLines

fun findSum(nums: Sequence<Int>, target: Int): Pair<Int, Int>? {
    nums
        .groupBy { it }
        .mapValues { (_, v) -> v.size }
        .let { countsMap ->
            countsMap.forEach { (num, count) ->
                when {
                    num * 2 == target && count > 1 -> return num to num
                    target - num in countsMap -> return num to target - num
                }
            }
        }
    return null
}

fun main() {
    (Path(DATAPATH) / "day1.txt").useLines { lines ->
        findSum(lines.map { it.toInt() }, 2020)
            ?.let { (n1, n2) ->
                println(n1 * n2)
            }
    }
}
