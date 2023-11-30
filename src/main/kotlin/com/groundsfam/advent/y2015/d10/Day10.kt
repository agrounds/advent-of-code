package com.groundsfam.advent.y2015.d10

fun speakAndSay(sequence: String): String {
    // we are building a very long string! avoid repeating a ton of work by using stringbuilder rather than string += ...
    val next = StringBuilder()
    var i = 0
    while (i < sequence.length) {
        var count = 1
        while (i + count < sequence.length && sequence[i+count] == sequence[i])
            count++
        next.append("$count${sequence[i]}")
        i += count
    }
    return next.toString()
}

fun main() {
    var curr = "1321131112"
    repeat(40) {
        curr = speakAndSay(curr)
    }
    println("Part one: ${curr.length}")
    repeat(10) {
        curr = speakAndSay(curr)
    }
    println("Part two: ${curr.length}")
}
