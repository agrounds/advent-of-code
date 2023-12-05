package com.groundsfam.advent.y2023.d05

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.except
import com.groundsfam.advent.rangeIntersect
import com.groundsfam.advent.timed
import org.apache.commons.lang3.time.StopWatch
import kotlin.io.path.div
import kotlin.io.path.useLines

// Models the mapping specified by a single row of the input.
//
// seed-to-soil map:
// 50 98 2
// 52 50 48
//
// corresponds to listOf(
//     FarmMapEntry(sourceNums=98..99, diff=-48),
//     FarmMapEntry(sourceNums=50..97, diff=2)
// )
data class FarmMapEntry(val sourceNums: LongRange, val diff: Long)
typealias FarmMap = List<FarmMapEntry>

fun mapSeedToLocation(seedNum: Long, maps: List<FarmMap>): Long =
    maps.fold(seedNum) { elementNum, farmMap ->
        val diff = farmMap
            .firstOrNull { elementNum in it.sourceNums }
            ?.diff
            ?: 0
        elementNum + diff
    }

fun mapElementRange(elementRange: LongRange, map: FarmMap): List<LongRange> {
    val mapped = mutableListOf<LongRange>()
    val notMapped = map.fold(listOf(elementRange)) { ranges, (sourceNums, diff) ->
        ranges.flatMap { range ->
            // find the part of this range which is mapped by this FarmMapEntry
            // apply the mapping and add it to the mapped list
            range.rangeIntersect(sourceNums)?.also {
                mapped.add((it.first + diff)..(it.last + diff))
            }
            // find the part(s) of this range which are not mapped by this FarmMapEntry
            // and pass them along to be mapped by the next FarmMapEntry
            range.except(sourceNums)
        }
    }
    mapped.addAll(notMapped)

    return mapped
}

fun main() = timed {
    val seedNums = mutableListOf<Long>()
    val maps = mutableListOf<FarmMap>()
    (DATAPATH / "2023/day05.txt").useLines { lines ->
        val iter = lines.iterator()
        iter.next()
            .split(" ")
            .drop(1)  // remove the "seeds:" prefix
            .forEach { seedNums.add(it.toLong()) }
        iter.next()  // remove blank line following seed numbers

        var map = mutableListOf<FarmMapEntry>()

        iter.forEach { line ->
            when {
                line.isBlank() -> {
                    // previous FarmMap is now complete
                    maps.add(map)
                    map = mutableListOf()
                }

                line[0].isDigit() -> {
                    val (destFrom, sourceFrom, rangeLength) = line.split(" ").map(String::toLong)
                    FarmMapEntry(
                        sourceNums = sourceFrom until (sourceFrom + rangeLength),
                        diff = destFrom - sourceFrom
                    ).also {
                        map.add(it)
                    }
                }

                else -> {
                    // otherwise this line is a "X-to-Y map:" header, ignore
                }
            }
        }
        // add the last FarmMap
        maps.add(map)
    }

    seedNums
        .minOf { mapSeedToLocation(it, maps) }
        .also { println("Part one: $it") }
    seedNums
        .chunked(2) { (start, length) -> start until (start + length) }
        .let { seedRanges ->
            maps.fold(seedRanges) { ranges, map ->
                ranges.flatMap { mapElementRange(it, map) }
            }
        }
        .minOf { it.first }
        .also { println("Part two: $it") }
}
