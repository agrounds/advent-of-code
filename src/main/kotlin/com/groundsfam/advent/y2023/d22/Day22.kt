package com.groundsfam.advent.y2023.d22

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.rangeIntersect
import com.groundsfam.advent.timed
import java.util.TreeSet
import kotlin.io.path.div
import kotlin.io.path.useLines


private data class Brick(val x: IntRange, val y: IntRange, val z: IntRange)

private fun Brick.intersect(that: Brick): Brick? {
    val x = this.x.rangeIntersect(that.x) ?: return null
    val y = this.y.rangeIntersect(that.y) ?: return null
    val z = this.z.rangeIntersect(that.z) ?: return null
    return Brick(x, y, z)
}

private fun Brick.fall(): Brick = copy(z = z.first - 1..<z.last)

private fun safeBricks(bricks: List<Brick>): Int {
    // sorted list of settled bricks
    // bricks are sorted by their highest z value first, then by other values to serve as tiebreakers
    // we only use the fact that they are sorted by highest z value below
    val settledBricks = TreeSet<Brick> { a, b ->
        fun Brick.attrList() = listOf(z.last, z.first, y.last, y.first, x.last, x.first)

        a.attrList()
            .zip(b.attrList())
            .map { (a1, b1) -> a1 - b1 }
            .firstOrNull { it != 0 }
            ?: 0
    }
    // bricks that cannot safely be removed
    val unsafeBricks = mutableSetOf<Brick>()
    bricks
        .sortedBy { it.z.first }
        .forEach { brick ->
            var prevStep: Brick? = null
            var fallingBrick = brick
            val hitBricks = mutableSetOf<Brick>()
            while (fallingBrick.z.first > 0 && hitBricks.isEmpty()) {
                prevStep = fallingBrick
                fallingBrick = fallingBrick.fall()
                // iterate through settled bricks in sorted order, highest to lowest
                val settledBricksIter = settledBricks.descendingIterator()
                var nextBrick = if (settledBricksIter.hasNext()) settledBricksIter.next() else null
                // stop iteration when the bricks are below the falling brick
                while (nextBrick != null && nextBrick.z.last >= fallingBrick.z.first) {
                    if (nextBrick.intersect(fallingBrick) != null) {
                        hitBricks.add(nextBrick)
                    }
                    nextBrick = if (settledBricksIter.hasNext()) settledBricksIter.next() else null
                }
            }

            // count how many bricks this one rests on
            // if only one, that one is unsafe to remove
            hitBricks
                .takeIf { it.size == 1 }
                ?.also { unsafeBricks.add(it.first()) }
            if (prevStep == null) {
                throw RuntimeException("Brick $brick is in impossible position and cannot fall")
            }
            settledBricks.add(prevStep)
        }

    return bricks.size - unsafeBricks.size
}

fun main() = timed {
    val bricks = (DATAPATH / "2023/day22.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            val (from, to) = line.split("~")
            val (fromX, fromY, fromZ) = from.split(",").map(String::toInt)
            val (toX, toY, toZ) = to.split(",").map(String::toInt)
            Brick(fromX..toX, fromY..toY, fromZ..toZ)
        }
    }
    println("Part one: ${safeBricks(bricks)}")
}
