package com.groundsfam.advent.y2023.d15

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.util.LinkedList
import kotlin.io.path.div
import kotlin.io.path.readLines


private data class Lens(val label: String, val focalLength: Int)

private fun hash(s: String): Int =
    s.fold(0) { curr, c ->
        (curr + c.code) * 17 % 256
    }

private fun executeHashmap(instructions: List<Instruction>): Long {
    val boxes = Array(256) { LinkedList<Lens>() }

    instructions.forEach { ins ->
        val box = boxes[hash(ins.label)]
        when (ins) {
            is Remove -> {
                // hidden assumption: there is at most one lens with a given label in the box
                // but this will always be true: a second insert instruction for the same label
                // results in the first lens being replaced, rather than a second lens with the
                // same label being added
                box.removeIf { it.label == ins.label }
            }
            is Insert -> {
                val newLens = Lens(ins.label, ins.focalLength)
                val i = box.indexOfFirst { it.label == ins.label }

                if (i != -1) {
                    box[i] = newLens
                } else {
                    box.add(newLens)
                }
            }
        }
    }

    return boxes.indices.sumOf { i ->
        boxes[i].foldIndexed(0L) { j, sum, lens ->
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
