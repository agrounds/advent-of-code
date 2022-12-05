package advent.y2015.d09

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

data class TourPlan(val start: Int, val destinations: Set<Int>)

fun findMinTour(distances: Array<IntArray>): Int {
    // store the minimum achievable tour of a subset of our locations
    val minTours = mutableMapOf<TourPlan, Int>()

    fun findMinSubtour(tourPlan: TourPlan): Int {
        val (start, destinations) = tourPlan
        if (tourPlan in minTours) return minTours[tourPlan]!!
        // only one possible tour
        if (destinations.size == 1) {
            return distances[start][destinations.first()]
                .also { minTours[tourPlan] = it }
        }
        // try each possible subtour recursively
        return destinations.minOf { next ->
            distances[start][next] + findMinSubtour(TourPlan(next, destinations - next))
        }.also {
            minTours[tourPlan] = it
        }
    }

    return distances.indices.minOf { start ->
        findMinSubtour(TourPlan(start, distances.indices.toSet() - start))
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

    findMinTour(distances).also { println("Part one: $it") }
}
