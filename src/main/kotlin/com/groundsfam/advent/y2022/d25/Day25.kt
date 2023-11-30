package com.groundsfam.advent.y2022.d25

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun snafuToDecimal(snafu: String): Long {
    var ret = 0L
    var factor = 1L
    snafu.reversed().forEach { c ->
        when (c) {
            '=' -> -2
            '-' -> -1
            '0' -> 0
            '1' -> 1
            '2' -> 2
            else -> throw RuntimeException("Invalid snafu digit $c in snafu number $snafu")
        }.also {
            ret += factor * it
        }
        factor *= 5
    }
    return ret
}
fun decimalToSnafu(n: Long): String {
    var num = n
    val ret = mutableListOf<Char>()

    while (num != 0L) {
        when(val remainder = (num % 5).toInt()) {
            3 -> {
                ret.add('=')
                num += 2
            }
            4 -> {
                ret.add('-')
                num++
            }
            else -> {
                ret.add('0' + remainder)
            }
        }
        num /= 5
    }

    return ret.reversed().joinToString("")
}


fun main() {
    (DATAPATH / "2022/day25.txt").useLines { lines ->
        lines
            .map(::snafuToDecimal)
            .sum()
            .let(::decimalToSnafu)
            .also { println("Part one: $it") }
    }
}
