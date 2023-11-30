package com.groundsfam.advent.y2022.d18

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.adjacents
import com.groundsfam.advent.Point3 as Point
import kotlin.io.path.div
import kotlin.io.path.useLines

fun surfaceArea1(points: List<Point>): Int {
    val prevPoints = mutableSetOf<Point>()
    return points.fold(0) { area, point ->
        if (point in prevPoints) area
        else {
            prevPoints.add(point)
            area + 6 - 2 * (prevPoints intersect point.adjacents().toSet()).size
        }
    }
}

fun surfaceArea2(points: List<Point>): Int {
    // bounds for the 3D rectangle to look within for air blocks touching the surface
    // of the droplet
    var (minX, minY, minZ) = points.first()
    var (maxX, maxY, maxZ) = points.first()
    val droplet = mutableSetOf<Point>()
    points.forEach { point ->
        droplet.add(point)
        val (x, y, z) = point
        if (x < minX) minX = x
        if (y < minY) minY = y
        if (z < minZ) minZ = z
        if (x > maxX) maxX = x
        if (y > maxY) maxY = y
        if (z > maxZ) maxZ = z
    }
    minX--
    minY--
    minZ--
    maxX++
    maxY++
    maxZ++

    fun inBounds(point: Point): Boolean =
        point.x in minX..maxX &&
            point.y in minY..maxY &&
            point.z in minZ..maxZ

    val start = Point(minX, minY, minZ)
    val queue = ArrayDeque(listOf(start))
    val prevQueued = mutableSetOf(start)
    var area = 0
    while (queue.isNotEmpty()) {
        val point = queue.removeFirst()
        point.adjacents().forEach { adjPoint ->
            if (adjPoint in droplet) {
                area++
            } else if (inBounds(adjPoint) && adjPoint !in prevQueued) {
                queue.add(adjPoint)
                prevQueued.add(adjPoint)
            }
        }
    }
    return area
}


fun main() {
    val points = (DATAPATH / "2022/day18.txt").useLines { lines ->
        lines.toList().map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            Point(x, y, z)
        }
    }
    println("Part one: ${surfaceArea1(points)}")
    println("Part two: ${surfaceArea2(points)}")
}
