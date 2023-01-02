package advent.y2022.d18

import advent.DATAPATH
import advent.adjacents
import advent.Point3 as Point
import kotlin.io.path.div
import kotlin.io.path.useLines

fun surfaceArea(points: List<Point>): Int {
    val prevPoints = mutableSetOf<Point>()
    return points.fold(0) { area, point ->
        if (point in prevPoints) area
        else {
            prevPoints.add(point)
            area + 6 - 2 * (prevPoints intersect point.adjacents().toSet()).size
        }
    }
}


fun main() {
    val points = (DATAPATH / "2022/day18.txt").useLines { lines ->
        lines.toList().map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            Point(x, y, z)
        }
    }
    println("Part one: ${surfaceArea(points)}")
}
