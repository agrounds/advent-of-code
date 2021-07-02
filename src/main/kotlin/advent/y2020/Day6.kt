package advent.y2020

import kotlin.io.path.div
import kotlin.io.path.useLines

fun main() {
    val groupedLines: List<List<String>> = (DATAPATH / "day6.txt").useLines { lines ->
        mutableListOf<MutableList<String>>(mutableListOf()).apply {
            lines.forEach { line ->
                if (line.isBlank()) {
                    add(mutableListOf())
                } else {
                    last().add(line)
                }
            }
        }
    }

    groupedLines.map { group ->
        group.fold(mutableSetOf<Char>()) { set, line ->
            set.apply { addAll(line.toList()) }
        }
    }.sumOf { it.size }
        .also { println("Part one: $it") }

    groupedLines.map { group ->
        group.map { it.toSet() }
            .reduce { s1, s2 -> s1 intersect s2 }
    }.sumOf { it.size }
        .also { println("Part two: $it") }
}