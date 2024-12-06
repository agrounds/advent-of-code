package com.groundsfam.advent.y2015.d23

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

// register a = 0, b = 1
data class Instruction(val name: String, val r: Int?, val offset: Int?)

val end = Instruction("end", null, null)

fun parseLine(line: String): Instruction {
    val parts = line.split(""",?\s""".toRegex())
    val name = parts[0]

    if (name !in setOf("hlf", "tpl", "inc", "jmp", "jie", "jio")) {
        return end
    }

    val register = parts[1].takeUnless { name == "jmp" }?.let {
        if (it == "a") 0
        else 1
    }
    val offset = when (name) {
        "jmp" -> parts[1].toInt()
        "jie", "jio" -> parts[2].toInt()
        else -> null
    }
    return Instruction(name, register, offset)
}

fun runProgram(instructions: List<Instruction>, initA: Int): Int {
    val registers = Array(2) { if (it == 0) initA else 0 }
    var line = 0
    while (line < instructions.size && instructions[line] != end) {
        val ins = instructions[line]
        when (ins.name) {
            "hlf" -> {
                registers[ins.r!!] /= 2
                line++
            }
            "tpl" -> {
                registers[ins.r!!] *= 3
                line++
            }
            "inc" -> {
                registers[ins.r!!]++
                line++
            }
            "jmp" -> {
                line += ins.offset!!
            }
            "jie" -> {
                if (registers[ins.r!!] % 2 == 0) {
                    line += ins.offset!!
                } else {
                    line++
                }
            }
            "jio" -> {
                if (registers[ins.r!!] == 1) {
                    line += ins.offset!!
                } else {
                    line++
                }
            }
        }
    }
    return registers[1]
}

fun main() = timed {
    val instructions = (DATAPATH / "2015/day23.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parseLine)
    }
    println("Part one: ${runProgram(instructions, 0)}")
    println("Part two: ${runProgram(instructions, 1)}")
}
