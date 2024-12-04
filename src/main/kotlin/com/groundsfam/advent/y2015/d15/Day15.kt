package com.groundsfam.advent.y2015.d15

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.max

const val TOTAL_AMOUNT = 100
const val TOTAL_CALORIES = 500

data class Ingredient(val properties: List<Int>, val calories: Int)

fun parseLine(line: String): Ingredient {
    val parts = line.split(""",?\s""".toRegex())
    return Ingredient(
        listOf(
            parts[2].toInt(),
            parts[4].toInt(),
            parts[6].toInt(),
            parts[8].toInt()
        ),
        parts[10].toInt(),
    )
}

fun amountsIter(numAmounts: Int, maxAmount: Int): Iterator<List<Int>> = iterator {
    if (numAmounts == 1) {
        (0..maxAmount).forEach {
            yield(listOf(it))
        }
    } else {
        (0..maxAmount).forEach { amount ->
            amountsIter(numAmounts - 1, maxAmount - amount).forEach { amountsList ->
                yield(listOf(amount) + amountsList)
            }
        }
    }
}


fun maxScore(ingredients: List<Ingredient>, partTwo: Boolean): Long {
    var maxScore: Long = 0

    amountsIter(ingredients.size, TOTAL_AMOUNT).forEach { amounts ->
        val totalCalories = ingredients.foldIndexed(0) { i, sum, ingredient ->
            sum + ingredient.calories * amounts[i]
        }
        if (!partTwo || totalCalories == TOTAL_CALORIES) {
            // product of all property totals
            val score: Long = ingredients[0].properties.indices.fold(1) { prod, i ->
                // for one property, sum over all ingredients/amounts
                val propertyTotal = ingredients.foldIndexed(0) { j, sum, ingredient ->
                    sum + ingredient.properties[i] * amounts[j]
                }
                prod * max(propertyTotal, 0)
            }
            maxScore = max(score, maxScore)
        }
    }

    return maxScore
}

fun main() = timed {
    val ingredients = (DATAPATH / "2015/day15.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parseLine)
    }
    println("Part one: ${maxScore(ingredients, false)}")
    println("Part two: ${maxScore(ingredients, true)}")
}
