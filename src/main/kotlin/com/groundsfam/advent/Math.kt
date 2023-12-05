package com.groundsfam.advent

import kotlin.math.pow
import kotlin.math.round

fun <T : Comparable<T>> min(a: T, b: T) = if (a <= b) a else b
fun <T : Comparable<T>> max(a: T, b: T) = if (a >= b) a else b

fun Int.pow(p: Int): Long = this.toDouble().pow(p).toLong()
fun sqrt(n: Int): Int = round(kotlin.math.sqrt(n.toDouble())).toInt()
