package com.groundsfam.advent.y2023.d22

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.mapWithPutDefault
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

private class Solution(bricks: List<Brick>) {
    private val settledBricks = TreeSet<Brick> { a, b ->
        fun Brick.attrList() = listOf(z.last, z.first, y.last, y.first, x.last, x.first)

        a.attrList()
            .zip(b.attrList())
            .map { (a1, b1) -> a1 - b1 }
            .firstOrNull { it != 0 }
            ?: 0
    }
    private val restsOn: Map<Brick, Set<Brick>>
    private val supports: Map<Brick, Set<Brick>>


    init {
        val _restsOn = mutableMapOf<Brick, MutableSet<Brick>>()
        val _supports: MutableMap<Brick, MutableSet<Brick>> by mapWithPutDefault { mutableSetOf() }

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
                    var settledBrick = if (settledBricksIter.hasNext()) settledBricksIter.next() else null
                    // stop iteration when the bricks are below the falling brick
                    while (settledBrick != null && settledBrick.z.last >= fallingBrick.z.first) {
                        if (settledBrick.intersect(fallingBrick) != null) {
                            hitBricks.add(settledBrick)
                        }
                        settledBrick = if (settledBricksIter.hasNext()) settledBricksIter.next() else null
                    }
                }

                if (prevStep == null) {
                    throw RuntimeException("Brick $brick is in impossible position and cannot fall")
                }
                settledBricks.add(prevStep)
                _restsOn[prevStep] = hitBricks
                hitBricks.forEach { hitBrick ->
                    _supports.getValue(hitBrick).add(prevStep)
                }
            }

        restsOn = _restsOn
        supports = _supports
    }

    fun safeBricks(): Int {
        val unsafeBricks = restsOn
            .values
            .mapNotNullTo(mutableSetOf()) {
                if (it.size == 1) it.first()
                else null
            }

        return settledBricks.size - unsafeBricks.size
    }
}

fun main() = timed {
    val solution = (DATAPATH / "2023/day22.txt").useLines { lines ->
        lines
            .mapTo(mutableListOf()) { line ->
                val (from, to) = line.split("~")
                val (fromX, fromY, fromZ) = from.split(",").map(String::toInt)
                val (toX, toY, toZ) = to.split(",").map(String::toInt)
                Brick(fromX..toX, fromY..toY, fromZ..toZ)
            }
            .let(::Solution)
    }
    println("Part one: ${solution.safeBricks()}")
}
