package advent.y2020.d24

import advent.DATAPATH
import advent.Point
import kotlin.io.path.div
import kotlin.io.path.useLines

enum class Direction {
    W,
    NW,
    NE,
    E,
    SE,
    SW
}

// convention for this problem:
//   going NE changes only Y coord, while going NW changes both X and Y
//   going SE changes X and Y coords, while going SW only changes X
// these conventions are consistent with movement on a hexagonal grid
fun Direction.asPoint(): Point = when (this) {
    Direction.W -> Point(-1, 0)
    Direction.NW -> Point(-1, -1)
    Direction.NE -> Point(0, -1)
    Direction.E -> Point(1, 0)
    Direction.SE -> Point(1, 1)
    Direction.SW -> Point(0, 1)
}

fun Point.adjacents(): List<Point> = Direction.values().map { this + it.asPoint() }


fun initialBlackTiles(tileIdentifiers: List<List<Direction>>): Set<Point> {
    val flippedSet = mutableSetOf<Point>()
    tileIdentifiers.forEach { directions ->
        val tile = directions.fold(Point(0, 0)) { point, direction ->
            point + direction.asPoint()
        }
        if (tile in flippedSet) {
            flippedSet.remove(tile)
        }
        else {
            flippedSet.add(tile)
        }
    }

    return flippedSet
}

// given the current black tiles, return the set of black tiles after tiles are
// flipped according to rules in part two
fun flipTiles(blackTiles: Set<Point>): Set<Point> {
    val tilesToFlip = mutableSetOf<Point>()

    // find matching black tiles
    blackTiles.forEach { tile ->
        tile
            .adjacents()
            .count { it in blackTiles }
            .takeIf { it !in setOf(1, 2) }
            ?.also {
                tilesToFlip.add(tile)
            }
    }

    // find matching white tiles
    blackTiles.forEach { tile ->
        tile.adjacents()
            .filter { it !in blackTiles }
            .forEach { whiteTile ->
                whiteTile
                    .adjacents()
                    .count { it in blackTiles }
                    .takeIf { it == 2 }
                    ?.also {
                        tilesToFlip.add(whiteTile)
                    }
            }
    }

    // return symmetric difference
    return (blackTiles + tilesToFlip) - (blackTiles.intersect(tilesToFlip))
}


fun main() {
    val tileIdentifiers = (DATAPATH / "2020/day24.txt").useLines { lines ->
        lines.toList().map { line ->
            val list = mutableListOf<Direction>()
            var i = 0
            while (i < line.length) {
                when (line[i]) {
                    'e' ->
                        Direction.E
                            .also { i++ }
                    'w' ->
                        Direction.W
                            .also { i++ }
                    'n' ->
                        (if (line[i+1] == 'e') Direction.NE
                        else Direction.NW)
                            .also { i += 2 }
                    's' ->
                        (if (line[i+1] == 'e') Direction.SE
                        else Direction.SW)
                            .also { i += 2 }
                    else -> throw RuntimeException("Illegal character ${list[i]} at position $i of $line")
                }.also {
                    list.add(it)
                }
            }
            list.toList()  // make it immutable
        }
    }
    var blackTiles = initialBlackTiles(tileIdentifiers)
        .also { println("Part one: ${it.size}") }
    repeat(100) {
        blackTiles = flipTiles(blackTiles)
    }
    println("Part two: ${blackTiles.size}")
}
