package advent.y2020.d20

import advent.DATAPATH
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

fun findCorners(tilesMap: Map<Int, Tile>): List<Int> {
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

    val uniqueEdges = edges.filterValues { it.size == 1 }
    return emptyList()
}


fun main() {
    val tilesMap: Map<Int, List<String>> = (DATAPATH / "2020/day20.txt").useLines { lines ->
        val map = mutableMapOf<Int, List<String>>()
        lines.chunked(12).forEach { chunk ->
            map[chunk.first().substring(5, 9).toInt()] = chunk.subList(1, 11)
        }
        map
    }
    findCorners(tilesMap)
}
