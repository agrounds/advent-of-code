package com.groundsfam.advent.y2015.d09

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

data class TourPlan(val start: Int, val destinations: Set<Int>)

fun findExtremeTour(distances: Array<IntArray>, minimize: Boolean = true): Int {
    // store the minimum achievable tour of a subset of our locations
    val extremeTours = mutableMapOf<TourPlan, Int>()

    fun findExtremeSubtour(tourPlan: TourPlan): Int {
        val (start, destinations) = tourPlan
        if (tourPlan in extremeTours) return extremeTours[tourPlan]!!
        // only one possible tour
        if (destinations.size == 1) {
            return distances[start][destinations.first()]
                .also { extremeTours[tourPlan] = it }
        }
        // try each possible subtour recursively
        return if (minimize) {
            destinations.minOf { next ->
                distances[start][next] + findExtremeSubtour(TourPlan(next, destinations - next))
            }
        } else {
            destinations.maxOf { next ->
                distances[start][next] + findExtremeSubtour(TourPlan(next, destinations - next))
            }
        }.also {
            extremeTours[tourPlan] = it
        }
    }

    return if (minimize) {
        distances.indices.minOf { start ->
            findExtremeSubtour(TourPlan(start, distances.indices.toSet() - start))
        }
    } else {
        distances.indices.maxOf { start ->
            findExtremeSubtour(TourPlan(start, distances.indices.toSet() - start))
        }
    }.also {
        println()
    }
}


fun main() {
    // forget the actual location names. just store as indices 0-7.
    // the distance from i to j is in distances[i][j]
    val distances = (DATAPATH / "2015/day09.txt").useLines { lines ->
        val namesList = mutableListOf<String>()
        val distances = Array(8) { IntArray(8) }  // there are exactly eight locations in input file
        lines.forEach { line ->
            val parts = line.split(" ")
            if (parts[0] !in namesList) namesList.add(parts[0])
            val a = namesList.indexOf(parts[0])
            if (parts[2] !in namesList) namesList.add(parts[2])
            val b = namesList.indexOf(parts[2])
            distances[a][b] = parts[4].toInt()
            distances[b][a] = parts[4].toInt()
        }
        distances
    }

    findExtremeTour(distances, minimize = true)
        .also { println("Part one: $it") }
    findExtremeTour(distances, minimize = false)
        .also { println("Part two: $it") }
}
