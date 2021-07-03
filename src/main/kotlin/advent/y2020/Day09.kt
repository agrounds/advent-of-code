package advent.y2020

import kotlin.io.path.div
import kotlin.io.path.useLines

private fun findInvalidNumber(nums: List<Long>, preamble: Int = 25): Long? {
    val counts = mutableMapOf<Long, Int>().apply {
        repeat(preamble) { i ->
            this[nums[i]] = this.getOrDefault(nums[i], 0) + 1
        }
    }

    fun isTwoSum(num: Long): Boolean {
        counts.forEach { (n, _) ->
            if (num - n in counts) return true
        }
        return false
    }

    for (i in preamble until nums.size) {
        val num = nums[i]
        if (!isTwoSum(num)) return num
        // update counts map to reflect new window of `preamble` elements
        counts[nums[i]] = counts.getOrDefault(nums[i], 0) + 1
        counts[nums[i - preamble]] = counts[nums[i - preamble]]!! - 1
        if (counts[nums[i - preamble]] == 0) counts.remove(nums[i - preamble])
    }
    return null
}

private fun findRange(nums: List<Long>, targetSum: Long): Pair<Int, Int>? {
    var from = 0
    var to = 0
    var sum = nums[0]

    while (from < nums.size && to < nums.size) {
        when {
            sum < targetSum -> {
                to++
                if (to < nums.size) {
                    sum += nums[to]
                }
            }
            sum > targetSum -> {
                sum -= nums[from]
                from++
            }
            else -> return from to to
        }
    }

    return null
}

fun main() {
    val nums = (DATAPATH / "day09.txt").useLines { lines ->
        lines.map { it.toLong() }.toList()
    }

    val invalidNumber = findInvalidNumber(nums)
        ?.also { println("Part one: $it") }!!

    findRange(nums, invalidNumber)?.let { (from, to) ->
        nums.subList(from, to).minOrNull()!! + nums.subList(from, to).maxOrNull()!!
    }?.also { println("Part two: $it") }
}
