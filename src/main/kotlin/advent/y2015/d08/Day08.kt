package advent.y2015.d08

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun interpretedLength(line: String): Int {
    var count = 0
    // skip opening and closing quotes
    var i = 1
    while (i <= line.length - 2) {
        if (line[i] == '\\') {
            // escaped character, so must determine which type
            if (line[i+1] == 'x') {
                // escaped hex ascii character, takes four code characters to represent
                i += 4
            } else {
                // other escaped characters take two code characters to represent
                i += 2
            }
        } else {
            // ordinary character, move to next character
            i++
        }
        count++
    }
    return count
}

fun encodedLength(line: String): Int {
    var count = 2  // opening and closing quotation marks required
    line.forEach { c ->
        if (c in "\"\\") count += 2
        else count++
    }
    return count
}


fun main() {
    val lines = (DATAPATH / "2015/day08.txt").useLines { lines ->
        lines.toList()
    }

    lines.sumOf { it.length - interpretedLength(it) }
        .also { println("Part one: $it") }
    lines.sumOf { encodedLength(it) - it.length }
        .also { println("Part two: $it") }
}
