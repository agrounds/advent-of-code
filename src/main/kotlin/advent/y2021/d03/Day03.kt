package advent.y2021.d03

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


// most common bit of position i of all nums
fun gammaRate(binaryNums: List<String>): String {
    val n = binaryNums.size
    return binaryNums[0].indices.joinToString("") { i ->
        if (binaryNums.count { it[i] == '0' } > n / 2) "0"
        else "1"
    }
}

fun powerConsumption(binaryNums: List<String>): Int {
    val gamma = gammaRate(binaryNums)
    val epsilon = gamma.indices.joinToString("") { i ->
        if (gamma[i] == '0') "1"
        else "0"
    }
    return gamma.toInt(2) * epsilon.toInt(2)
}

fun gasRating(binaryNums: List<String>, keepMostCommon: Boolean): Int {
    var i = 0
    val nums = binaryNums.toMutableList()
    while (nums.size > 1) {
        val zeroesCount = nums.count { it[i] == '0' }
        val commonBit = if (zeroesCount > nums.size / 2) 0 else 1
        val requiredBit = '0' + if (keepMostCommon) commonBit else 1 - commonBit
        nums.removeAll { it[i] != requiredBit }
        i++
    }
    return nums.first().toInt(2)
}

fun lifeSupportRating(binaryNums: List<String>): Int {
    val oxygen = gasRating(binaryNums, true)
    val co2 = gasRating(binaryNums, false)
    return oxygen * co2
}

fun main() {
    val binaryNums = (DATAPATH / "2021/day03.txt").useLines { lines ->
        lines.toList()
    }
    println("Part one: ${powerConsumption(binaryNums)}")
    println("Part two: ${lifeSupportRating(binaryNums)}")
}
