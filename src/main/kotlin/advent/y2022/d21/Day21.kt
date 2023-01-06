package advent.y2022.d21

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.exp

enum class Op {
    PLUS,
    MINUS,
    TIMES,
    DIVIDE;

    fun doOp(a: Long, b: Long): Long = when (this) {
        PLUS -> a + b
        MINUS -> a - b
        TIMES -> a * b
        DIVIDE -> a / b
    }

    companion object {
        fun fromChar(char: Char) = when (char) {
            '+' -> PLUS
            '-' -> MINUS
            '*' -> TIMES
            '/' -> DIVIDE
            else -> throw RuntimeException("Invalid operator $char")
        }
    }
}

sealed class Expression(open val name: String)
data class Literal(override val name: String, val value: Int) : Expression(name)
data class Operation(override val name: String, val operation: Op, val leftArg: String, val rightArg: String) : Expression(name)

fun parseExpression(line: String): Expression {
    val parts = line.split(" ")
    val name = parts[0].takeWhile { it != ':' }
    return if (parts.size == 2) Literal(name, parts[1].toInt())
    else Operation(name, Op.fromChar(parts[2][0]), parts[1], parts[3])
}

fun resolve(expressions: List<Expression>, name: String): Long {
    val expressionsByName = expressions.associateBy { it.name }
    val resolved = mutableMapOf<String, Long>()

    fun helper(name: String): Long {
        if (name in resolved) {
            return resolved[name]!!
        }

        return when (val expr = expressionsByName[name]!!) {
            is Literal -> expr.value.toLong()
            is Operation -> expr.operation.doOp(helper(expr.leftArg), helper(expr.rightArg))
        }.also {
            resolved[name] = it
        }
    }

    return helper(name)
}


fun main() {
    val expressions = (DATAPATH / "2022/day21.txt").useLines { lines ->
        lines.toList().map(::parseExpression)
    }
    println("Part one: ${resolve(expressions, "root")}")
}
