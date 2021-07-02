package advent.y2020

import java.lang.RuntimeException
import kotlin.io.path.div
import kotlin.io.path.useLines

private val pattern = Regex("""(\d+)-(\d+) (\w): (\w+)""")

private data class PasswordWithPolicy(val password: String, val requiredChar: Char, val num1: Int, val num2: Int) {
    val valid1: Boolean by lazy {
        password.filter { it == requiredChar }.length in num1..num2
    }
    val valid2: Boolean by lazy {
        (password[num1 - 1] == requiredChar) xor (password[num2 - 1] == requiredChar)
    }
}

fun main() {
    val passwords = (DATAPATH / "day2.txt").useLines { lines ->
        lines.map { line ->
            pattern.matchEntire(line).let { matchResult ->
                if (matchResult == null) throw RuntimeException("Could not match input $line against pattern $pattern")
                val (min, max, char, password) = matchResult.destructured
                PasswordWithPolicy(password, char[0], min.toInt(), max.toInt())
            }
        }.toList()
    }

    passwords
        .filter { it.valid1 }
        .run { println("Part one: $size") }
    passwords
        .filter { it.valid2 }
        .run { println("Part two: $size") }
}