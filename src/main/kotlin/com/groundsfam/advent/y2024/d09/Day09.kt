package com.groundsfam.advent.y2024.d09

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText

fun defragmentDisk(diskMap: List<Int>): Long {
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

fun main() = timed {
    val diskMap = (DATAPATH / "2024/day09.txt").readText().trim().map {
        it.digitToInt()
    }
    println("Part one: ${defragmentDisk(diskMap)}")
}
