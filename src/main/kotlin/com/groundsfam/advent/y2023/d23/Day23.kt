package com.groundsfam.advent.y2023.d23

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.contains
import com.groundsfam.advent.grids.pointOfFirst
import com.groundsfam.advent.grids.pointOfLast
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.points.go
import com.groundsfam.advent.points.s
import com.groundsfam.advent.timed
import com.groundsfam.advent.toDirection
import kotlin.io.path.div
import kotlin.math.max

fun partOne(grid: Grid<Char>): Int {
    val start = grid.pointOfFirst { it == '.' }
    val end = grid.pointOfLast { it == '.' }
    var longest = 0

    data class Walk(val position: Point, val visited: MutableSet<Point>)

    val partialWalks = ArrayDeque<Walk>()
    // to simplify things, start at the second position on the walk, which
    // is always the point south of start
    partialWalks.add(Walk(start.s, mutableSetOf(start)))

    while (partialWalks.isNotEmpty()) {
        val walk = partialWalks.removeFirst()
        var position: Point? = walk.position
        val visited = walk.visited

        while (position != null && position != end) {
            visited.add(position)
            var next: Point? = null
            val slopeDir = grid[position].toDirection()
            val adjacentPoints =
                if (slopeDir != null) listOf(position.go(slopeDir))
                else position.adjacents(diagonal = false)

            adjacentPoints
                .filter { it !in visited && grid[it] != '#' }
                .forEach {
                    if (next == null) {
                        next = it
                    } else {
                        partialWalks.add(Walk(it, visited.toMutableSet()))
                    }
                }

            position = next
        }

        if (position == end) {
            longest = max(longest, visited.size)
        }
    }

    return longest
}

class PartTwo(val grid: Grid<Char>) {
    val start = grid.pointOfFirst { it == '.' }
    val end = grid.pointOfLast { it == '.' }
    // for two fork-in-road points p, q, neighbors[p] contains q
    // iff there is a path from p to q that does not pass through
    // any other fork-in-road point
    // distances[p to q] = distances[q to p] = length of (longest) direct path from p to q
    val neighbors: Map<Point, Set<Point>>
    val distances: Map<Pair<Point, Point>, Int>

    init {
        // initialize neighbors and distances
        val neighbors = mutableMapOf<Point, MutableSet<Point>>()
        val distances = mutableMapOf<Pair<Point, Point>, Int>()

        // find all edges between nodes and record their lengths
        val queue = ArrayDeque<Pair<Point, Direction>>()
        val seen = mutableSetOf<Pair<Point, Direction>>()
        // assumption: path always goes down to start
        queue.add(start to Direction.DOWN)
        while (queue.isNotEmpty()) {
            val pair = queue.removeFirst()
            if (!seen.add(pair)) {
                continue
            }
            val from = pair.first
            var p = from
            var nextDirs = listOf(pair.second)
            var pathLen = 0
            while (nextDirs.size == 1) {
                val dir = nextDirs[0]
                p = p.go(dir)
                pathLen++
                nextDirs = Direction.entries.filter {
                    val q = p.go(it)
                    when {
                        it == -dir -> false
                        q !in grid -> false
                        grid[q] == '#' -> false
                        else -> true
                    }
                }
            }
            if (p == end || nextDirs.isNotEmpty()) {
                listOf(from to p, p to from).forEach { (a, b) ->
                    val nbrs = neighbors[a] ?: mutableSetOf<Point>().also { neighbors[a] = it }
                    nbrs.add(b)
                    distances[a to b] = max(pathLen, distances[a to b] ?: 0)
                }
                nextDirs.forEach { dir ->
                    queue.add(p to dir)
                }
            }
        }

        // Observation: Excluding the start and end, the graph consists of
        // vertices of degree 3 or 4. Vertices of degree 3 form the exterior
        // boundary of the graph, and vertices of degree 4 form the interior.
        // Any path which reaches one of the boundary vertices cannot turn
        // back towards start and still reach the end. Thus, these exterior
        // edges are effectively directed. This cuts down on the number
        // of paths to consider substantially.

        // remove edges going wrong direction on boundary
        val firstNode = neighbors[start]!!.first()
        val lastNode = neighbors[end]!!.first()
        neighbors[firstNode]!!.filterNot { it == start }.forEach {
            var node = it
            val boundary = mutableListOf(firstNode)
            while (node != lastNode) {
                val nextNode = neighbors[node]!!.first { nextNode ->
                    nextNode != boundary.last() && neighbors[nextNode]!!.size == 3
                }
                boundary.add(node)
                node = nextNode
            }
            boundary.add(lastNode)
            (0 until boundary.size - 2).forEach { i ->
                val a = boundary[i]
                val b = boundary[i + 1]
                neighbors[b]!!.remove(a)
                distances.remove(b to a)
            }
        }

        // remove edges going wrong way from/to start/end
        neighbors[firstNode]!!.remove(start)
        distances.remove(firstNode to start)
        neighbors[end]!!.remove(lastNode)
        distances.remove(end to lastNode)

        this.neighbors = neighbors
        this.distances = distances
    }

    fun longestWalk(): Int {
        fun helper(p: Point, visited: Set<Point>): Int? =
            if (p == end) 0
            else {
                neighbors[p]!!
                    .filterNot { it in visited }
                    .mapNotNull { q ->
                        helper(q, visited + q)?.let { qlen ->
                            distances[p to q]!! + qlen
                        }
                    }
                    .maxOrNull()
            }

        return helper(start, setOf(start)) ?: throw RuntimeException("No valid path from start to end")
    }
}

fun main() = timed {
    val grid = (DATAPATH / "2023/day23.txt").readGrid()
    println("Part one: ${partOne(grid)}")
    println("Part two: ${PartTwo(grid).longestWalk()}")
}
