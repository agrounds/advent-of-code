package com.groundsfam.advent

import java.util.PriorityQueue
import kotlin.math.pow
import kotlin.math.round

fun <T : Comparable<T>> min(a: T, b: T) = if (a <= b) a else b
fun <T : Comparable<T>> max(a: T, b: T) = if (a >= b) a else b

fun Int.pow(p: Int): Long = this.toDouble().pow(p).toLong()
fun sqrt(n: Int): Int = round(kotlin.math.sqrt(n.toDouble())).toInt()
fun numDigits(n: Int, base: Int = 10): Int =
    numDigits(n.toLong(), base)
fun numDigits(n: Long, base: Int = 10): Int =
    n.toString(base).length

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

// Sieve of Eratosthenes implementation
// https://stackoverflow.com/q/64794683
fun genPrimes(): Sequence<Int> =
    generateSequence(2 to generateSequence(3) { it + 2 }) { (_, currSeq) ->
        val nextPrime = currSeq.iterator().next()
        nextPrime to currSeq.filter { it % nextPrime != 0 }
    }.map { it.first }

// Ordered sequence of divisors
fun divisors(n: Int, desc: Boolean = true): Sequence<Int> = sequence {
    val primeDivisors = genPrimes()
        .takeWhile { it <= n }
        .filter { n % it == 0 }
        .toList()
    val seen = mutableSetOf<Int>()
    val queue = PriorityQueue<Int>()
    seen.add(1)
    queue.add(1)
    while (queue.isNotEmpty()) {
        val d = queue.poll()
        yield(if (desc) n / d else d)
        primeDivisors.forEach { p ->
            val d2 = d * p
            if (seen.add(d2) && n % d2 == 0) {
                queue.add(d2)
            }
        }
    }
}
