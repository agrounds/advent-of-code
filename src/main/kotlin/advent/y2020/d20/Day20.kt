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


/**
 * Order matters. Here, we optionally flip across the vertical axis, then rotate
 * some number of times clockwise.
 */
data class TileOrientation(val num: Int, val flip: Boolean, val rotate: Int)

class Solver(private val tilesMap: Map<Int, Tile>) {
    // the tiles will assemble into a square image, so there are a
    // square number of them
    private val sideLen = sqrt(tilesMap.size)
    private val tileSideLen = tilesMap.values.first().size

    // a table on which to lay down tiles to solve the puzzle. it
    // is just big enough to store the solved puzzle, so table[0][0]
    // must be a corner piece in correct orientation
    private val table = Array(sideLen) { Array<TileOrientation?>(sideLen) { null } }

    val cornerTiles: List<Int>
    private val puzzleEdges: Set<String>
    private val tilesByEdge: Map<String, Set<Int>>

    init {
        // map from an edge to all tiles having it as an edge, possibly flipped
        val tileEdges = mutableMapOf<String, MutableSet<Int>>()
        tilesMap.forEach { (num, tile) ->
            tile.edges().forEach { edge ->
                (tileEdges[edge] ?: mutableSetOf<Int>().also { tileEdges[edge] = it })
                    .add(num)
            }
        }
        tilesByEdge = tileEdges

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

    /**
     * Returns all edges of this tile, post transformation, read
     * both clockwise (cw) and counterclockwise (ccw). The order is:
     *   cw top, cw right, cw bottom, cw left,
     *   ccw top, ccw right, ccw bottom, ccw left
     */
    fun TileOrientation.edges(): List<String>  =
        tilesMap[num]!!.edges().take(4).let { unflippedEdges ->
            if (flip) {
                unflippedEdges
                    // cw edges become ccw edges
                    .map { it.reversed() }
                    // the order changes to what was originally top, left, bottom, right
                    .let { reversedEdges ->
                        (0 until 4).map { reversedEdges[(4 - it) % 4] }
                    }
            } else {
                unflippedEdges
            }
        }.let { unrotatedEdges ->
            // perform rotation
            (0 until 4).map { unrotatedEdges[(it + 4 - rotate) % 4] }
        }.let { edges ->
            edges + edges.map { it.reversed() }
        }

    fun TileOrientation.transformed(): Tile {
        val rowsFirst = rotate % 2 == 0
        val reverseRows = if (flip) {
            rotate in setOf(0, 1)
        } else {
            rotate in setOf(2, 3)
        }
        val reverseCols = rotate in setOf(1, 2)

        val original = tilesMap[num]!!

        return if (rowsFirst) {
            (0 until tileSideLen)
                .let { if (reverseCols) it.reversed() else it }
                .map { j ->
                    original[j].let {
                        if (reverseRows) it.reversed()
                        else it
                    }
                }
        } else {
            (0 until tileSideLen)
                .let { if (reverseRows) it.reversed() else it }
                .map { i ->
                    (0 until tileSideLen)
                        .let { if (reverseCols) it.reversed() else it }
                        .map { j -> original[j][i] }
                        .toCharArray()
                        .let(::String)
                }
        }
    }

    fun solvePuzzle(): List<String> {
        val firstCorner = cornerTiles[0]
        tilesMap[firstCorner]!!.edges()
            .take(4)
            .map { it in puzzleEdges }
            .let { (t, r, b, l) ->
                val firstCornerTurns = when {
                    l && t -> 0
                    t && r -> 3
                    r && b -> 2
                    b && l -> 1
                    else -> throw RuntimeException("First corner $firstCorner has unexpected puzzle edge overlap")
                }
                table[0][0] = TileOrientation(firstCorner, false, firstCornerTurns)
            }

        (0 until sideLen).forEach { i ->
            if (i != 0) {
                // place tile based on the one above this spot
                val tileAbove = table[i-1][0] ?: throw RuntimeException("Tile not placed at (${i-1}, 0)!")
                val bottomEdge = tileAbove.edges()[2]
                val nextTile = tilesByEdge[bottomEdge]!!
                    .filterNot { it == tileAbove.num }
                    .first()
                table[i][0] = tilesMap[nextTile]!!
                    .edges()
                    .indexOf(bottomEdge)
                    .let { matchingEdgeIdx ->
                        // if tileAbove's CW bottom edge matches one of our CW edges, then a flip is required
                        if (matchingEdgeIdx < 4) TileOrientation(nextTile, true, matchingEdgeIdx)
                        // if tileAbove's CW bottom edge matches one of our CCW edges, then no flip is required
                        else TileOrientation(nextTile, false, (8 - matchingEdgeIdx) % 4)
                    }
            }
            (1 until sideLen).forEach { j ->
                // place tile based on the one to the left of this spot
                val tileLeft = table[i][j-1] ?: throw RuntimeException("Tile not placed at ($i, ${j-1})!")
                val rightEdge = tileLeft.edges()[1]
                val nextTile = tilesByEdge[rightEdge]!!
                    .filterNot { it == tileLeft.num }
                    .first()
                table[i][j] = tilesMap[nextTile]!!
                    .edges()
                    .indexOf(rightEdge)
                    .let { matchingEdgeIdx ->
                        // if tileAbove's CW right edge matches one of our CW edges, then a flip is required
                        if (matchingEdgeIdx < 4) TileOrientation(nextTile, true, (matchingEdgeIdx + 3) % 4)
                        // if tileAbove's CW bottom edge matches one of our CCW edges, then no flip is required
                        else TileOrientation(nextTile, false, (7 - matchingEdgeIdx) % 4)
                    }
            }
        }

        val ret = Array(sideLen * tileSideLen - 2) { "" }
        table.forEachIndexed { i, row ->
            val strings = row.map {
                if (it == null) throw RuntimeException("Table not filled out!")
                tilesMap[it.num]!!
            }.reduce { tileA, tileB ->
                tileA.indices.map {
                    tileA[it] + tileB[it]
                }.map {
                    // strip off border characters
                    it.substring(1 until it.length - 1)
                }
            }
            val jRange = when (i) {
                0 -> {
                    // don't include first row, it's part of the border
                    1 until strings.size
                }
                table.size - 1 -> {
                    // don't include last row, it's part of the border
                    0 until strings.size - 1
                }
                else -> {
                    strings.indices
                }
            }
            jRange.forEach { j ->
                ret[tileSideLen * i + j - 1] = strings[j]
            }
        }
        return ret.toList()
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
    val solvedPuzzle = solver.solvePuzzle()
}
