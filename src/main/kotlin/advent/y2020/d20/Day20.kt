package advent.y2020.d20

import advent.DATAPATH
import advent.sqrt
import kotlin.io.path.div
import kotlin.io.path.useLines

typealias Tile = List<String>
fun Tile.edges(): List<String> = listOf(
    // all edges read in clockwise direction
    this.first(),  // top
    String(this.map { it.last() }.toCharArray()),  // right
    this.last().reversed(),  // bottom
    String(this.reversed().map { it.first() }.toCharArray()),  // left
)

fun solvePuzzle(tilesMap: Map<Int, Tile>) {
    // the tiles will assemble into a square image, so there are a
    // square number of them
    val sideLen = sqrt(tilesMap.size)
    // a table on which to lay down tiles to solve the puzzle. it
    // is made big enough that any piece may be placed in the middle,
    // and the table will have enough space to solve it from there.
    val table = Array(sideLen * 2 - 1) { Array<Tile?>(sideLen * 2 - 1) { null } }

    // map from an edge to all tiles having it as an edge, possibly flipped
    val edges: MutableMap<String, MutableSet<Int>> = mutableMapOf()
    tilesMap.forEach { (num, tile) ->
        tile.edges().let { edges ->
            edges + edges.map { it.reversed() }
        }.forEach { edge ->
            (edges[edge] ?: mutableSetOf<Int>().also { edges[edge] = it })
                .add(num)
        }
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
    solvePuzzle(tilesMap)
}
