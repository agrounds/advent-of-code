package com.groundsfam.advent.y2016.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

val chars = ('a'..'z').toList()

data class Room(val name: String, val sector: Int, val checksum: String)

fun parse(line: String): Room {
    val parts = line.split("""[\[\]-]""".toRegex())
    val name = parts.subList(0, parts.size - 3).joinToString("-")
    val sector = parts[parts.size - 3].toInt()
    val checksum = parts[parts.size - 2]
    return Room(name, sector, checksum)
}

fun isReal(room: Room): Boolean {
    val counts = mutableMapOf<Char, Int>()
    room.name.forEach { c ->
        if (c != '-') {
            counts[c] = (counts[c] ?: 0) + 1
        }
    }
    val computedCheckSum = counts.entries
        .sortedWith(compareBy({ -it.value }, { it.key }))
        .take(5)
        .map { it.key }
        .joinToString("")
    return computedCheckSum == room.checksum
}

fun decodeName(room: Room): String =
    room.name
        .map { c ->
            if (c == '-') ' '
            else chars[((c - 'a') + room.sector) % 26]
        }
        .joinToString("")

fun main() = timed {
    val rooms = (DATAPATH / "2016/day04.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parse)
    }
    val realRooms = rooms.filter(::isReal)
    println("Part one: ${realRooms.sumOf { it.sector }}")
    realRooms
        .first { decodeName(it) == "northpole object storage" }
        .also { println("Part two: ${it.sector}") }
}
