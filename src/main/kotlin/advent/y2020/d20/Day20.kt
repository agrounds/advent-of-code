package advent.y2020.d20

import advent.DATAPATH
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

fun findCornerTiles(tilesMap: Map<Int, Tile>): List<Int> {
    // map from an edge to all tiles having it as an edge, possibly flipped
    val tileEdges: MutableMap<String, MutableSet<Int>> = mutableMapOf()
    tilesMap.forEach { (num, tile) ->
        tile.edges().let { edges ->
            edges + edges.map { it.reversed() }
        }.forEach { edge ->
            (tileEdges[edge] ?: mutableSetOf<Int>().also { tileEdges[edge] = it })
                .add(num)
        }
    }

    return tileEdges.values
        .filter { it.size == 1 }
        .map { it.first() }
        .groupBy { it }
        .mapValues { (_, v) -> v.size }.entries
        // edge tiles appear twice here, and corners appear four times
        .filter { (_, count) -> count == 4 }
        .let { cornerTiles ->
            cornerTiles.map { it.key }
        }
}


fun main() {
    val tilesMap: Map<Int, Tile> = (DATAPATH / "2020/day20.txt").useLines { lines ->
        val map = mutableMapOf<Int, Tile>()
        lines.chunked(12).forEach { chunk ->
            map[chunk.first().substring(5, 9).toInt()] = chunk.subList(1, 11)
        }
        map
    }
    val cornerTiles = findCornerTiles(tilesMap)
    cornerTiles.map { it.toLong() }
        .reduce { a, b -> a * b }
        .also{ println("Part one: $it") }
}
