package com.groundsfam.advent.y2015.d19

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

typealias Molecule = List<String>

fun parseReplacement(line: String): Pair<String, Molecule>? {
    val parts = line.split(" ")
    if (parts.size < 3) return null
    return parts[0] to parseMolecule(parts[2])
}

fun parseMolecule(s: String): Molecule {
    val molecule = mutableListOf<String>()
    var i = 0
    while (i < s.length) {
        if (i + 1 == s.length || s[i + 1] !in 'a'..'z') {
            molecule.add(s.substring(i..i))
            i++
        } else {
            molecule.add(s.substring(i..i + 1))
            i += 2
        }
    }
    return molecule
}

fun createMolecules(molecule: Molecule, replacements: Map<String, Set<Molecule>>): Set<Molecule> {
    val createdMolecules = mutableSetOf<Molecule>()
    molecule.forEachIndexed { i, atom ->
        replacements[atom]?.forEach { replacement ->
            val newMolecule = molecule.subList(0, i) +
                replacement +
                molecule.subList(i + 1, molecule.size)
            createdMolecules.add(newMolecule)
        }
    }
    return createdMolecules
}

fun stepsToTarget(target: Molecule): Int {
    // observations from input:
    // all replacements are of one of these forms, where * is
    // an atom other than Rn, Ar, or Y
    //   * -> * *
    //   * -> * Rn * Ar
    //   * -> * Rn * Y * Ar
    //   * -> * Rn * Y * Y * Ar
    // thus, the net effect of any of these transformations
    // is to add one more * than Y
    // if we count the *s and subtract the Ys in the final molecule,
    // we will end up with one more than the number of transformations
    // performed to reach it
    //
    // these observations may not be true on other inputs, YMMV!

    return target.sumOf {
        when (it) {
            "Rn", "Ar" -> 0
            "Y" -> -1
            else -> 1
        }.toInt()
    } - 1
}

fun main() = timed {
    val replacements = mutableMapOf<String, MutableSet<Molecule>>()
    val medicineMolecule = (DATAPATH / "2015/day19.txt").useLines { lines ->
        lines.map { line ->
            parseReplacement(line)?.also { (k, v) ->
                val replacementSet = replacements[k]
                if (replacementSet == null) {
                    replacements[k] = mutableSetOf(v)
                } else {
                    replacementSet.add(v)
                }
            }
            line
        }
            // the last line is the medicine molecule
            .last()
            .let(::parseMolecule)
    }
    println("Part one: ${createMolecules(medicineMolecule, replacements).size}")
    println("Part two: ${stepsToTarget(medicineMolecule)}")
}
