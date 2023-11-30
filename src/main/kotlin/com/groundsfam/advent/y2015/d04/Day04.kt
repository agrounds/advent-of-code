package com.groundsfam.advent.y2015.d04

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

const val secret = "iwrupvqb"  // copied from https://adventofcode.com/2015/day/4

fun digest(num: Int): ByteArray =
    MessageDigest.getInstance("MD5")
        .digest("$secret$num".toByteArray(StandardCharsets.UTF_8))


fun lenZeroPrefix(ba: ByteArray): Int =
    ba.takeWhile { it == 0.toByte() }.size.let {
        if (ba.size > it && (ba[it].toUByte() and 0xF0.toUByte()) == 0.toUByte()) {
            2*it + 1
        } else {
            2*it
        }
    }


fun main() {
    var foundPartOne = false
    var foundPartTwo = false
    var n = 1
    while (!foundPartTwo) {
        when (lenZeroPrefix(digest(n))) {
            5 -> {
                if (!foundPartOne) {
                    println("Part one: $n")
                }
                foundPartOne = true
                n++
            }
            6 -> {
                println("Part two: $n")
                foundPartTwo = true
            }
            else -> {
                n++
            }
        }
    }
}