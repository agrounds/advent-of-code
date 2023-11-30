package com.groundsfam.advent.y2015.d05

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

val vowels = setOf('a', 'e', 'i', 'o', 'u')
val naughtySubstrings = setOf("ab", "cd", "pq", "xy")

fun isNice1(string: String): Boolean {
    var numVowels = 0
    var doubleLetter = false
    var naughtySubstring = false

    string.forEachIndexed { i, c ->
        if (c in vowels)
            numVowels++
        if (i+1 < string.length) {
            if (c == string[i+1])
                doubleLetter = true
            if (string.substring(i, i+2) in naughtySubstrings)
                naughtySubstring = true
        }
    }
    return numVowels >= 3 && doubleLetter && !naughtySubstring
}

fun isNice2(string: String): Boolean {
    var repeatTwoLetter = false
    var repeatWithSkip = false

    string.forEachIndexed { i, c ->
        if (string.length >= i+4 && string.substring(i+2, string.length).contains(string.substring(i, i+2))) {
            repeatTwoLetter = true
        }
        if (string.length >= i+3 && c == string[i+2]) {
            repeatWithSkip = true
        }
    }
    return repeatTwoLetter && repeatWithSkip
}


fun main() {
    val strings = (DATAPATH / "2015/day05.txt").useLines { lines ->
        lines.toList()
    }
    strings
        .count(::isNice1)
        .also { println("Part one: $it") }
    strings
        .count(::isNice2)
        .also { println("Part two: $it") }
}