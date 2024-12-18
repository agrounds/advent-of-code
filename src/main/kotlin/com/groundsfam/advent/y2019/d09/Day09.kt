package com.groundsfam.advent.y2019.d09

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div

fun runBoost(boostProgram: List<Long>, partTwo: Boolean): Long {
    val computer = IntCodeComputer(boostProgram)
    computer.sendInput(if (partTwo) 2 else 1)
    computer.runProgram()
    val outputs = computer.getAllOutput()
    if (!partTwo) {
        outputs.subList(0, outputs.size - 1).forEach { out ->
            if (out != 0L) throw RuntimeException("Malfunctioning opcode: $out")
        }
    }
    return outputs.last()
}

fun main() = timed {
    val boostProgram = (DATAPATH / "2019/day09.txt").readProgram()
    println("Part one: ${runBoost(boostProgram, false)}")
    println("Part two: ${runBoost(boostProgram, true)}")
}
