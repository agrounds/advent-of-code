package advent.y2022.d12

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

typealias Point = Pair<Int, Int>
val Point.x: Int
    get() = this.first
val Point.y: Int
    get() = this.second

fun Point.left(): Point = this.copy(first = first-1)
fun Point.right(): Point = this.copy(first = first+1)
fun Point.up(): Point = this.copy(second = second-1)
fun Point.down(): Point = this.copy(second = second+1)

typealias Grid = List<List<Int>>
fun Grid.height(point: Point): Int? = point.let { (x, y) ->
    when {
        y !in this.indices -> null
        x !in this[y].indices -> null
        else -> this[y][x]
    }
}

class Solver(private val grid: Grid, start: Point) {
    private val toVisit = mutableSetOf(start)
    private val visited = mutableSetOf<Point>()
    private val distances = Array(grid.size) { IntArray(grid.first().size) { Integer.MAX_VALUE } }
        .apply { this[start.y][start.x] = 0 }

    private fun neighbors(point: Point): List<Point> =
        listOf(point.left(), point.right(), point.up(), point.down())
            .filter { neighbor ->
                grid.height(neighbor).let {
                    it != null && it <= grid.height(point)!! + 1
                }
            }

    private fun currDistance(point: Point): Int = distances[point.y][point.x]

    fun minDistance(to: Point): Int {
        while (to !in visited) {
            val next = toVisit.minByOrNull(::currDistance)
                ?: throw RuntimeException("Set of points to visit is empty even though graph is not fully explored!")
            toVisit.remove(next)
            neighbors(next)
                .filterNot { it in visited }
                .forEach { neighbor ->
                    distances[neighbor.y][neighbor.x] = minOf(
                        currDistance(neighbor),
                        currDistance(next) + 1
                    )
                    toVisit.add(neighbor)
                }

            visited.add(next)

        }
        return currDistance(to)
    }
}


fun main() {
    var start = 0 to 0
    var end = 0 to 0

    val grid: Grid = (DATAPATH / "2022/day12.txt").useLines { lines ->
        lines.toList().mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                when (c) {
                    'S' -> {
                        start = x to y
                        0
                    }
                    'E' -> {
                        end = x to y
                        25
                    }
                    else ->
                        c - 'a'
                }
            }
        }
    }
    val solver = Solver(grid, start)
    solver.minDistance(end)
        .also { println("Part one: $it") }
}
