package advent.y2020.d04

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


private fun parsePassports(input: Sequence<String>): List<Map<String, String>> {
    val ret: MutableList<Map<String, String>> = mutableListOf()
    var curr: MutableMap<String, String> = mutableMapOf()

    input.forEach { line ->
        if (line.isBlank()) {
            ret.add(curr)
            curr = mutableMapOf()
        } else {
            line.split(" ")
                .forEach { part ->
                    curr[part.substringBefore(':')] = part.substringAfter(':')
                }
        }
    }

    if (curr.isNotEmpty()) ret.add(curr)

    return ret
}

private fun validate(passport: Map<String, String>): Boolean {
    if (!passport.keys.containsAll(requiredFields)) return false

    try {
        passport["byr"]?.let { byr ->
            if (byr.toInt() !in 1920..2002) return false
        }
        passport["iyr"]?.let { iyr ->
            if (iyr.toInt() !in 2010..2020) return false
        }
        passport["eyr"]?.let { eyr ->
            if (eyr.toInt() !in 2020..2030) return false
        }
        passport["hgt"]?.let { hgt ->
            val unitIdx = hgt.length - 2
            val num = hgt.substring(0 until unitIdx).toInt()

            when (hgt.substring(unitIdx)) {
                "cm" -> if (num !in 150..193) return false
                "in" -> if (num !in 59..76) return false
                else -> return false
            }
        }
        passport["hcl"]?.let { hcl ->
            if (hcl.length != 7) return false
            hcl.forEachIndexed { i, c ->
                when (i) {
                    0 -> if (c != '#') return false
                    else -> if (c !in '0'..'9' && c !in 'a'..'f') return false
                }
            }
        }
        passport["ecl"]?.let { ecl ->
            if (ecl !in validEyeColors) return false
        }
        passport["pid"]?.let { pid ->
            if (pid.length != 9 || pid.any { c -> c !in '0'..'9' }) return false
        }
    } catch (e: Exception) {
        return false
    }

    return true
}

private val requiredFields = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
private val validEyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

fun main() {
    val passports = (DATAPATH / "2020/day04.txt").useLines { parsePassports(it) }

    passports
        .count { it.keys.containsAll(requiredFields) }
        .also { println("Part one: $it") }

    passports
        .count(::validate)
        .also { println("Part two: $it") }
}