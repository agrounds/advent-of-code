package advent.y2020.d03

import advent.y2020.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

private const val TREE = '#'

private fun countTrees(grid: List<String>, right: Int, down: Int): Int {
    val width = grid[0].length
    var x = 0

    return grid
        .filterIndexed { i, _ ->
            i % down == 0  // same as incrementing a `y` var by `down` for each step
        }.count { row ->
            (row[x] == TREE).also {
                x = (x + right) % width
            }
        }

}


fun main() {
    val grid = (DATAPATH / "day03.txt")
        .useLines { it.toList() }
        .also {
            println("Part one: ${countTrees(it, 3, 1)}")
        }

    // slopes to check
    listOf(
        1 to 1,
        3 to 1,
        5 to 1,
        7 to 1,
        1 to 2,
    ).map { (right, down) -> countTrees(grid, right, down) }
        .fold(1L) { acc, i -> acc * i }
        .also { println("Part two: $it") }
}