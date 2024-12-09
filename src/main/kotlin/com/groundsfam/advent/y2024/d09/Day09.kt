package com.groundsfam.advent.y2024.d09

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.math.min

fun defragmentDiskOne(diskMap: List<Int>): Long {
    // for every i, diskMap[2 * i] = number of blocks of file i

    // index of diskMap entry
    var i = 0
    // index of the next block on disk to fill
    var blockIdx = 0
    // start from last fileId
    var fileId = (diskMap.size - 1) / 2
    // number of blocks of file #fileId already relocated
    var blocksMoved = 0

    var sum = 0L
    while (i < 2 * fileId) {
        repeat(diskMap[i]) {
            if (i % 2 == 0) {
                // this is already a file
                sum += blockIdx * (i / 2)
            } else {
                // this is empty space to fill
                sum += blockIdx * fileId
                blocksMoved++
                if (blocksMoved == diskMap[2 * fileId]) {
                    fileId--
                    blocksMoved = 0
                }
            }
            blockIdx++
        }
        i++
    }
    // count remaining unmoved blocks of partially moved file
    repeat(diskMap[2 * fileId] - blocksMoved) {
        sum += blockIdx * fileId
        blockIdx++
    }
    return sum
}

fun defragmentDiskTwo(diskMap: List<Int>): Long {
    // mapping from fileId to position of first block on disk
    val filePositions = IntArray((diskMap.size + 1) / 2)
    // mapping from length of space to positions of spaces of that length
    val spaces = (1..9).associateWithTo(mutableMapOf<Int, PriorityQueue<Int>>()) { PriorityQueue() }

    var pos = 0
    diskMap.forEachIndexed { i, len ->
        if (i % 2 == 0) {
            filePositions[i / 2] = pos
        } else {
            // NOTE: This only works because there are no zero-length files in the input,
            //       hence we do not need to consider concatenating adjacent spaces together
            if (len > 0) {
                spaces[len]!!.add(pos)
            }
        }
        pos += len
    }

    // for each fileId, in descending order, attempt to move it
    // as far to the left as possible
    ((diskMap.size - 1) / 2 downTo 0).forEach { fileId ->
        val fileLen = diskMap[2 * fileId]
        var minSpaceLen: Int? = null
        var minSpacePos: Int? = null
        (fileLen..9).forEach { len ->
            val spacePos = spaces[len]?.takeIf { it.isNotEmpty() }?.peek()
            if (spacePos != null && spacePos < filePositions[fileId] && minSpacePos?.let { it < spacePos } != true) {
                minSpaceLen = len
                minSpacePos = min(spacePos, minSpacePos ?: spacePos)
            }
        }
        if (minSpaceLen != null) {
            filePositions[fileId] = minSpacePos!!
            spaces[minSpaceLen]!!.poll()
            // length of this space now that we've moved the file here
            val newSpaceLen = minSpaceLen!! - fileLen
            if (newSpaceLen > 0) {
                spaces[newSpaceLen]!!.add(minSpacePos!! + fileLen)
            }
        }
    }
    return filePositions.indices.sumOf { fileId ->
        val fileLen = diskMap[2 * fileId]
        // closed formula for truncated triangle number
        // a + (a + 1) + (a + 2) + ... + (a + l - 1) = l * (2*a + l - 1) / 2
        fileId.toLong() * fileLen * (2 * filePositions[fileId] + fileLen - 1) / 2
    }
}

fun main() = timed {
    val diskMap = (DATAPATH / "2024/day09.txt").readText().trim().map {
        it.digitToInt()
    }
    println("Part one: ${defragmentDiskOne(diskMap)}")
    println("Part two: ${defragmentDiskTwo(diskMap)}")
}
