package com.groundsfam.advent.y2023.d15

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines


private data class Lens(val label: String, val focalLength: Int)

private fun hash(s: String): Int =
    s.fold(0) { curr, c ->
        (curr + c.code) * 17 % 256
    }

private fun executeHashmap(instructions: List<Instruction>): Long {
    val boxes = Array(256) { linkedMapOf<String, Lens>() }

    instructions.forEach { ins ->
        val box = boxes[hash(ins.label)]
        when (ins) {
            is Remove -> {
                box.remove(ins.label)
            }
            is Insert -> {
                box[ins.label] = Lens(ins.label, ins.focalLength)
            }
        }
    }

    return boxes.indices.sumOf { i ->
        boxes[i].values.foldIndexed(0L) { j, sum, lens ->
            sum + (i + 1) * (j + 1) * lens.focalLength
        }
    }
}

fun main() = timed {
    val steps = (DATAPATH / "2023/day15.txt").readLines().first()
        .split(",")

    steps
        .sumOf(::hash)
        .also { println("Part one: $it") }

    steps
        .map(String::parse)
        .let(::executeHashmap)
        .also { println("Part two: $it") }
}
