package com.groundsfam.advent.y2022.d13

import com.groundsfam.advent.DATAPATH
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlin.io.path.div
import kotlin.io.path.useLines

sealed class Packet
data class PacketInt(val content: Int) : Packet() {
    override fun toString() = content.toString()
}

fun PacketInt.toList() = PacketList(listOf(this))
data class PacketList(val content: List<Packet>) : Packet() {
    override fun toString() = content.toString()
}

fun parsePacket(json: JsonElement): Packet = when (json) {
    is JsonPrimitive -> PacketInt(json.int)
    is JsonArray -> PacketList(json.map(::parsePacket))
    else -> throw RuntimeException("Invalid packet: $json")
}

object PacketComparator : Comparator<Packet> {
    override fun compare(packetA: Packet?, packetB: Packet?): Int = when (packetA) {
        null -> if (packetB == null) 0 else 1

        is PacketInt -> when (packetB) {
            null -> -1
            is PacketInt -> packetA.content - packetB.content
            is PacketList -> compare(packetA.toList(), packetB)
        }

        is PacketList -> when (packetB) {
            null -> -1
            is PacketInt -> compare(packetA, packetB.toList())
            is PacketList -> packetA.content.indices.firstOrNull { i ->
                i < packetB.content.size && compare(packetA.content[i], packetB.content[i]) != 0
            }.let { diffIdx ->
                if (diffIdx == null) packetA.content.size - packetB.content.size
                else compare(packetA.content[diffIdx], packetB.content[diffIdx])
            }
        }
    }
}

val dividerPackets = listOf(2, 6).map {
    PacketList(listOf(PacketList(listOf(PacketInt(it)))))
}


fun main() {
    val pairs = (DATAPATH / "2022/day13.txt").useLines { lines ->
        val linesList = lines.toList()
        (linesList.indices step 3).map { i ->
            listOf(i, i + 1)
                .map { parsePacket(Json.parseToJsonElement(linesList[it])) }
                .let { (a, b) -> a to b }
        }
    }
    pairs.mapIndexed { i, (a, b) ->
        if (PacketComparator.compare(a, b) <= 0) i + 1
        else 0
    }
        .sum()
        .also { println("Part one: $it") }
    pairs
        .flatMap { it.toList() }
        .let { it + dividerPackets }
        .sortedWith(PacketComparator)
        .let { sortedPackets ->
            dividerPackets.map { sortedPackets.indexOf(it) + 1 }
        }
        .let { (a, b) -> a * b }
        .also { println("Part two: $it") }
}
