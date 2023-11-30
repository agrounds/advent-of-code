package com.groundsfam.advent.y2020.d21

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

data class Ingredients(val ingredients: List<String>, val allergens: List<String>)
fun parseLine(line: String): Ingredients =
    line.split(" (contains ").let { (a, b) ->
        Ingredients(
            a.split(" "),
            b.split(" ").map { it.substring(0, it.length - 1) }
        )
    }

// map from allergen to ingredients possibly containing that allergen
fun findAllergyCandidates(foods: List<Ingredients>): Map<String, Set<String>> {
    val maybeHasAllergy = mutableMapOf<String, Set<String>>()
    foods.forEach { (ingredients, allergens) ->
        val ingrSet = ingredients.toSet()
        allergens.forEach { allergen ->
            if (allergen !in maybeHasAllergy) {
                maybeHasAllergy[allergen] = ingrSet
            } else {
                maybeHasAllergy[allergen] = maybeHasAllergy[allergen]!! intersect ingrSet
            }
        }
    }
    return maybeHasAllergy
}

fun allergenFreeIngredients(foods: List<Ingredients>): Int {
    val allergyCandidates = findAllergyCandidates(foods).values.reduce { a, b -> a union b }
    return (foods.flatMap { it.ingredients } - allergyCandidates).size
}

fun associateAllergens(foods: List<Ingredients>): Map<String, String> {
    val maybeHasAllergy = findAllergyCandidates(foods).mapValues { (_, v) -> v.toMutableSet() }
    val ret = mutableMapOf<String, String>().apply {
        maybeHasAllergy.entries.forEach { (allergen, possibleIngredients) ->
            if (possibleIngredients.size == 1) {
                this[allergen] = possibleIngredients.first()
            }
        }
    }
    while (ret.size != maybeHasAllergy.size) {
        ret.forEach { (allergen, ingredient) ->
            maybeHasAllergy.keys.forEach { otherAllergen ->
                if (allergen != otherAllergen) {
                    maybeHasAllergy[otherAllergen]!!.remove(ingredient)
                }
            }
        }
        maybeHasAllergy.entries.forEach { (allergen, possibleIngredients) ->
            if (allergen !in ret && possibleIngredients.size == 1) {
                ret[allergen] = possibleIngredients.first()
            }
        }
    }

    return ret
}


fun main() {
    val foods = (DATAPATH / "2020/day21.txt").useLines { lines ->
        lines.map(::parseLine).toList()
    }
    allergenFreeIngredients(foods)
        .also { println("Part one: $it") }
    associateAllergens(foods)
        .entries
        .sortedBy { (allergen, _) -> allergen }
        .joinToString(",") { it.value }
        .also { println("Part two: $it") }
}
