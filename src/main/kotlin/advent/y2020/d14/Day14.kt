package advent.y2020.d14

import advent.y2020.DATAPATH
import advent.y2020.pow
import kotlin.io.path.div
import kotlin.io.path.useLines

private sealed class Instruction

private data class SetMask(val mask: String) : Instruction()
private data class WriteValue(val location: Int, val value: Long) : Instruction()

private val memRegex = Regex("""mem\[(.+)] = (.+)""")
private fun parseInstruction(line: String): Instruction = when {
    line.startsWith("mask = ") -> {
        SetMask(line.substringAfter("mask = "))
    }
    else -> {
        val (location, origValue) = memRegex.matchEntire(line)!!.destructured
        WriteValue(location.toInt(), origValue.toLong())
    }
}

private fun runProgram1(program: List<Instruction>): Long {
    var zeroesMask: Long = 0
    var onesMask: Long = 0

    val memory = mutableMapOf<Int, Long>()

    program.forEach { ins ->
        when (ins) {
            is SetMask -> {
                ins.mask.let {
                    zeroesMask = it.replace('X', '1').toLong(2)
                    onesMask = it.replace('X', '0').toLong(2)
                }
            }
            is WriteValue -> {
                memory[ins.location] = (ins.value and zeroesMask) or onesMask
            }
        }
    }

    return memory.values.sum()
}

private fun runProgram2(program: List<Instruction>): Long {
    var onesMask: Long = 0
    // zeroesMask + floatingBits together implement the wildcard nature of Xs
    // zeroesMask is the 36 bit number of zeroes for Xs in the mask, and 1s elsewhere
    // floatingBits is a list of all the inverse indices of Xs,
    // i.e. 00000000000000000000000000000000000X would have floatingBits = [0], not [35]
    var zeroesMask: Long = 0
    var floatingBits: List<Int> = emptyList()

    val memory = mutableMapOf<Long, Long>()

    fun setAll(location: Long, value: Long, fbIdx: Int) {
        if (fbIdx == floatingBits.size) {
            memory[location] = value
        } else {
            setAll(location, value, fbIdx + 1)
            setAll(location + 2.pow(floatingBits[fbIdx]), value, fbIdx + 1)
        }
    }

    program.forEach { ins ->
        when (ins) {
            is SetMask -> {
                ins.mask.let {
                    onesMask = it.replace('X', '0').toLong(2)
                    zeroesMask = it.replace('0', '1').replace('X', '0').toLong(2)
                    floatingBits = it.mapIndexed { i, c ->
                        (it.length - i - 1).takeIf { c == 'X' }
                    }.filterNotNull()
                }
            }
            is WriteValue -> {
                ((ins.location.toLong() and zeroesMask) or onesMask).let { loc ->
                    setAll(loc, ins.value, 0)
                }
            }
        }
    }

    return memory.values.sum()
}

fun main() {
    val program = (DATAPATH / "day14.txt")
        .useLines { it.map(::parseInstruction).toList() }

    runProgram1(program)
        .also { println("Part one: $it") }

    runProgram2(program)
        .also { println("Part two: $it") }
}