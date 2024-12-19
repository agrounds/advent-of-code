package com.groundsfam.advent.y2019.d14

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

const val ORE = "ORE"
const val FUEL = "FUEL"

const val ORE_LIMIT = 1_000_000_000_000

data class Reaction(val inputs: Map<String, Int>, val output: String, val outputAmount: Int)
fun parseReaction(line: String): Reaction {
    val (input, output) = line.split(" => ")
    val (outputAmount, outputName) = output.split(" ")
    val inputs = input.split(", ").associate {
        val (amount, name) = it.split(" ")
        name to amount.toInt()
    }
    return Reaction(inputs, outputName, outputAmount.toInt())
}

fun createFuel(reactions: Map<String, Reaction>, numFuel: Long): Long {
    val required = mutableMapOf(FUEL to numFuel)

    fun nextToMake(): String? =
        required.firstNotNullOfOrNull { (c, n) ->
            c.takeIf { it != ORE && n > 0 }
        }

    var toMake = nextToMake()
    while (toMake != null) {
        val requiredAmount = required[toMake]!!
        val (inputs, _, outputAmount) = reactions[toMake]!!
        val numReactions = (requiredAmount + outputAmount - 1) / outputAmount

        required[toMake] = requiredAmount - numReactions * outputAmount
        inputs.forEach { (c, n) ->
            required[c] = (required[c] ?: 0) + numReactions * n
        }
        toMake = nextToMake()
    }
    return required[ORE]!!
}

fun createMaxFuel(reactions: Map<String, Reaction>): Long {
    var a = 1L
    // heuristic: assume naive amount of fuel (num ore / ore for 1 fuel)
    // is not off by more than double
    var b = (2 * ORE_LIMIT / createFuel(reactions, 1L)).toLong()

    while (b - a > 1) {
        val mid = (a + b) / 2
        if (createFuel(reactions, mid) > ORE_LIMIT) {
            b = mid
        } else {
            a = mid
        }
    }
    return a
}

fun main() = timed {
    val reactions = (DATAPATH / "2019/day14.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parseReaction)
            .associateBy { it.output }
    }
    println("Part one: ${createFuel(reactions, 1)}")
    println("Part two: ${createMaxFuel(reactions)}")
}
