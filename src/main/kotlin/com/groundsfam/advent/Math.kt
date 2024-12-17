package com.groundsfam.advent

import kotlin.math.log
import kotlin.math.pow
import kotlin.math.round

fun <T : Comparable<T>> min(a: T, b: T) = if (a <= b) a else b
fun <T : Comparable<T>> max(a: T, b: T) = if (a >= b) a else b

fun Int.pow(p: Int): Long = this.toDouble().pow(p).toLong()
fun sqrt(n: Int): Int = round(kotlin.math.sqrt(n.toDouble())).toInt()
fun numDigits(n: Int, base: Int = 10): Int =
    numDigits(n.toLong(), base)
fun numDigits(n: Long, base: Int = 10): Int =
    log(n.toDouble(), base.toDouble()).toInt() + 1

fun Collection<Long>.gcd(): Long =
    this.reduce(::gcd)

fun Collection<Long>.lcm(): Long =
    this.reduce(::lcm)

fun gcd(a: Int, b: Int): Int {
    var c = if (a >= 0) a else -a
    var d = if (b >= 0) b else -b
    while (d != 0) {
        val tmp = d
        d = c % d
        c = tmp
    }
    return c
}

fun gcd(a: Long, b: Long): Long {
    var c = if (a >= 0) a else -a
    var d = if (b >= 0) b else -b
    while (d != 0L) {
        val tmp = d
        d = c % d
        c = tmp
    }
    return c
}

fun lcm(a: Long, b: Long): Long =
    (a * b) / gcd(a, b)
