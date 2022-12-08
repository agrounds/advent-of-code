package advent.y2020.d20

import advent.DATAPATH
import advent.sqrt
import kotlin.io.path.div
import kotlin.io.path.useLines

typealias Tile = List<String>

/**
 * Returns all edges of this tile, read both clockwise (cw)
 * and counterclockwise (ccw). The order is:
 *   cw top, cw right, cw bottom, cw left,
 *   ccw top, ccw right, ccw bottom, ccw left
 */
fun Tile.edges(): List<String> = listOf(
    this.first(),
    String(this.map { it.last() }.toCharArray()),
    this.last().reversed(),
    String(this.reversed().map { it.first() }.toCharArray()),
).let { edges ->
    edges + edges.map { it.reversed() }
}

fun Tile.flip(): Tile = TODO()
fun Tile.rotate(times: Int): Tile = TODO()

/**
 * Order matters. Here, we optionally flip across the vertical axis, then rotate
 * some number of times clockwise.
 */
data class TileOrientation(val num: Int, val flip: Boolean, val rotate: Int)

class Solver(private val tilesMap: Map<Int, Tile>) {
    // the tiles will assemble into a square image, so there are a
    // square number of them
    private val sideLen = sqrt(tilesMap.size)

    // a table on which to lay down tiles to solve the puzzle. it
    // is just big enough to store the solved puzzle, so table[0][0]
    // must be a corner piece in correct orientation
    private val table = Array(sideLen) { Array<Tile?>(sideLen) { null } }

    val cornerTiles: List<Int>
    private val puzzleEdges: Set<String>

    init {
        // map from an edge to all tiles having it as an edge, possibly flipped
        val tileEdges = mutableMapOf<String, MutableSet<Int>>()
        tilesMap.forEach { (num, tile) ->
            tile.edges().let { edges ->
                edges + edges.map { it.reversed() }
            }.forEach { edge ->
                (tileEdges[edge] ?: mutableSetOf<Int>().also { tileEdges[edge] = it })
                    .add(num)
            }
        }

        cornerTiles = tileEdges
            .filterValues { it.size == 1 }
            .also {
                puzzleEdges = it.keys
            }.values
            .map { it.first() }
            .groupBy { it }
            .mapValues { (_, v) -> v.size }.entries
            // edge tiles appear twice here, and corners appear four times
            .filter { (_, count) -> count == 4 }
            .let { cornerTiles ->
                cornerTiles.map { it.key }
            }
    }

    fun solvePuzzle() {

    }
}


fun main() {
    val solver = (DATAPATH / "2020/day20.txt").useLines { lines ->
        val map = mutableMapOf<Int, Tile>()
        lines.chunked(12).forEach { chunk ->
            map[chunk.first().substring(5, 9).toInt()] = chunk.subList(1, 11)
        }
        Solver(map)
    }
    solver.cornerTiles
        .map { it.toLong() }
        .reduce { a, b -> a * b }
        .also { println("Part one: $it") }
}
