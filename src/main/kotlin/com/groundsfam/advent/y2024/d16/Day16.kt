package com.groundsfam.advent.y2024.d16

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div

data class Path(val p: Point, val prevDir: Direction, val pathLen: Long)

fun bestPath(grid: Grid<Char>): Long {
    val start = grid.pointOfFirst { it == 'S' }
    val end = grid.pointOfFirst { it == 'E' }

    val scores = mutableMapOf<Point, Long>()

    val queue = PriorityQueue<Path>(compareBy { it.pathLen })
    val visited = mutableSetOf<Point>()
    queue.add(Path(start, Direction.RIGHT, 0))
    while (queue.isNotEmpty()) {
        val (p, prevDir, pathLen) = queue.poll()
        scores[p] = pathLen
        if (p == end) return pathLen
        if (!visited.add(p)) continue

        listOf(prevDir, prevDir.cw, prevDir.ccw).forEach { dir ->
            val q = p.go(dir)
            if (q !in visited && q in grid && grid[q] != '#') {
                val cost = if (dir == prevDir) 1 else 1001
                queue.add(Path(q, dir, pathLen + cost))
            }
        }
    }
    throw RuntimeException("No path from start to end")
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day16.txt").readGrid()
    println("Part one: ${bestPath(grid)}")
}
