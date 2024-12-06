package com.groundsfam.advent.y2015.d24

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

// DISCLAIMER
// This won't work on a more general list of weights!
// We DO NOT check that the remaining packages can actually be
// split up into the remaining groups so that all groups have
// the same weight. This just seems to always be possible for
// the naively-sought solution in actual inputs from AoC.
fun bestGrouping(weights: List<Int>, numGroups: Int): Long {
    val groupWeight = weights.sum() / numGroups
    // dpSize[i][total] = minimum size of any subset of the weights w0, w1, ..., wi which adds to exactly total
    // set to -1 to indicate no group adds to that weight
    val dpSize = Array(weights.size) { IntArray(groupWeight + 1) { if (it == 0) 0 else -1 } }
    // dpQE[i][total] = minimum QE for any subset of the weights w0, w1, ..., wi which adds to exactly total
    // set to -1 to indicate no group adds to that weight
    val dpQE = Array(weights.size) { LongArray(groupWeight + 1) { if (it == 0) 1 else -1 } }


    weights.forEachIndexed { i, w ->
        (1..groupWeight).forEach { total ->
            if (w > total && i > 0)  {
                dpSize[i][total] = dpSize[i - 1][total]
                dpQE[i][total] = dpQE[i - 1][total]
            }
            if (w <= total) {
                if (i == 0) {
                    if (w == total) {
                        dpSize[i][w] = 1
                        dpQE[i][w] = w.toLong()
                    }
                } else {
                    // best size and QE if w is included in the group
                    val size1 = dpSize[i - 1][total - w].takeIf { it != -1 }?.let { it + 1 }
                    val qe1 = dpQE[i - 1][total - w].takeIf { it != -1L }?.let { it * w }
                    // best size QE if is not included in the group
                    val size2 = dpSize[i - 1][total].takeIf { it != -1 }
                    val qe2 = dpQE[i - 1][total].takeIf { it != -1L }
                    // both size/qe1 and size/qe2 may be null if no such group exists, handle appropriately
                    if (size1 != null) {
                        if (size2 != null) {
                            // first use size to determine best solution, then use QE as tie-breaker
                            if (size1 < size2 || (size1 == size2 && qe1!! < qe2!!)) {
                                dpSize[i][total] = size1
                                dpQE[i][total] = qe1!!
                            } else {
                                dpSize[i][total] = size2
                                dpQE[i][total] = qe2!!
                            }
                        } else {
                            dpSize[i][total] = size1
                            dpQE[i][total] = qe1!!
                        }
                    } else if (size2 != null) {
                        dpSize[i][total] = size2
                        dpQE[i][total] = qe2!!
                    }
                }
            }
        }
    }

    return dpQE.last().last()
}

fun main() = timed {
    val weights = (DATAPATH / "2015/day24.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), String::toInt)
    }
    println("Part one: ${bestGrouping(weights, 3)}")
    println("Part two: ${bestGrouping(weights, 4)}")
}
