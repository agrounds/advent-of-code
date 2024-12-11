package com.groundsfam.advent.y2016.d07

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

data class IP(val outsideParts: List<String>, val insideParts: List<String>)

fun parse(line: String): IP {
    val outsideParts = mutableListOf<String>()
    val insideParts = mutableListOf<String>()
    line.split("""[\[\]]""".toRegex()).forEachIndexed { i, part ->
        if (i % 2 == 0) outsideParts.add(part)
        else insideParts.add(part)
    }
    return IP(outsideParts, insideParts)
}

fun hasABBA(part: String): Boolean =
    (0 until part.length - 3).any { i ->
        part[i] == part[i + 3] && part[i + 1] == part[i + 2] && part[i] != part[i + 1]
    }

fun supportsABBA(ip: IP): Boolean =
    ip.outsideParts.any(::hasABBA) && !ip.insideParts.any(::hasABBA)

fun supportsSSL(ip: IP): Boolean {
    ip.outsideParts.forEach { part ->
        (0 until part.length - 2).forEach { i ->
            if (part[i] == part[i + 2] && part[i] != part[i + 1]) {
                if (ip.insideParts.any { "${part[i+1]}${part[i]}${part[i+1]}" in it }) {
                    return true
                }
            }
        }
    }
    return false
}

fun main() = timed {
    val ips = (DATAPATH / "2016/day07.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parse)
    }
    println("Part one: ${ips.count(::supportsABBA)}")
    println("Part two: ${ips.count(::supportsSSL)}")
}
