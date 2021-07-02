package advent.y2020

import kotlin.io.path.div
import kotlin.io.path.useLines

fun findTwoSum(nums: List<Int>, target: Int): Pair<Int, Int>? {
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

fun findSumSlow(nums: List<Int>, target: Int, n: Int): List<Int>? {
    val list = MutableList(n) { 0 }

    fun helper(from: Int, subTarget: Int, subN: Int): Boolean {
        if (subN == 0) return subTarget == 0
        if (from >= nums.size) return false

        (from until nums.size).forEach { i ->
            if (helper(from = i + 1, subTarget = subTarget - nums[i], subN = subN - 1)) {
                list[n - subN] = nums[i]
                return true
            }
        }

        return false
    }

    if (helper(0, target, n)) return list
    return null
}

fun main() {
    val input = (DATAPATH / "day1.txt").useLines { lines ->
        lines.map { it.toInt() }.toList()
    }

    findTwoSum(input, 2020)
        ?.let { (n1, n2) ->
            println("first part: ${n1 * n2}")
        } ?: println("first part: not found :(")

    findSumSlow(input, 2020, 3)
        ?.fold(1) { x, y -> x*y }
        ?. also { println("second part: $it") }
        ?: println("second part: not found :(")
}
