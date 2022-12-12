package advent.y2022.d12

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

typealias Grid = List<List<Int>>
fun Grid.height(x: Int, y: Int): Int = this[y][x]


fun main() {
    var startX = 0
    var startY = 0
    var endX = 0
    var endY = 0

    val grid: Grid = (DATAPATH / "2022/day12.txt").useLines { lines ->
        lines.toList().mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                when (c) {
                    'S' -> {
                        startX = x
                        startY = y
                        0
                    }
                    'E' -> {
                        endX = x
                        endY = y
                        25
                    }
                    else ->
                        c - 'a'
                }
            }
        }
    }
}
