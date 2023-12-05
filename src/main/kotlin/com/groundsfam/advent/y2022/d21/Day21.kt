package com.groundsfam.advent.y2022.d21

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.exp

const val ROOT = "root"
const val HUMN = "humn"

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
data class Operation(override val name: String, val op: Op, val leftArg: String, val rightArg: String) : Expression(name)

fun parseExpression(line: String): Expression {
    val parts = line.split(" ")
    val name = parts[0].takeWhile { it != ':' }
    return if (parts.size == 2) Literal(name, parts[1].toInt())
    else Operation(name, Op.fromChar(parts[2][0]), parts[1], parts[3])
}

fun partOne(expressions: List<Expression>): Long {
    val expressionsByName = expressions.associateBy { it.name }
    val resolved = mutableMapOf<String, Long>()

    fun resolve(name: String): Long {
        if (name in resolved) {
            return resolved[name]!!
        }

        return when (val expr = expressionsByName[name]!!) {
            is Literal -> expr.value.toLong()
            is Operation -> expr.op.doOp(resolve(expr.leftArg), resolve(expr.rightArg))
        }.also {
            resolved[name] = it
        }
    }

    return resolve(ROOT)
}

fun partTwo(expressions: List<Expression>): Long {
    val expressionsByName = expressions.associateBy { it.name }
    // value will be null if it involves "humn"
    // and we don't know the value that satisfies it yet
    val resolved = mutableMapOf<String, Long?>(HUMN to null)

    fun resolve(name: String): Long? {
        if (name in resolved) {
            return resolved[name]
        }

        return when (val expr = expressionsByName[name]!!) {
            is Literal -> expr.value.toLong()
            is Operation -> {
                val left = resolve(expr.leftArg)
                val right = resolve(expr.rightArg)
                if (left == null || right == null) null
                else expr.op.doOp(left, right)
            }
        }.also {
            resolved[name] = it
        }
    }

    fun solveFor(name: String, value: Long): Long {
        if (name == HUMN) {
            return value
        }
        val expression = expressionsByName[name]
        if (expression !is Operation) throw RuntimeException("Unexpected solveFor literal! solveFor($name, $value)")

        val left = resolve(expression.leftArg)
        val right = resolve(expression.rightArg)

        if (left == null) {
            if (right == null) throw RuntimeException("Both sides depend on HUMN! solveFor($name, $value)")
            val nextValue = when (expression.op) {
                Op.PLUS -> value - right
                Op.MINUS -> value + right
                Op.TIMES -> value / right
                Op.DIVIDE -> value * right
            }
            return solveFor(expression.leftArg, nextValue)
        }

        if (right != null) throw RuntimeException("Neither side depends on HUMN! solveFor($name, $value)")
        val nextValue = when (expression.op) {
            Op.PLUS -> value - left
            Op.MINUS -> left - value
            Op.TIMES -> value / left
            Op.DIVIDE -> left / value
        }
        return solveFor(expression.rightArg, nextValue)
    }

    val rootExpression = expressionsByName[ROOT]
    if (rootExpression !is Operation) throw RuntimeException("Root monkey is not an operation!")
    val left = resolve(rootExpression.leftArg)
    val right = resolve(rootExpression.rightArg)
    if (left == null) {
        if (right == null) throw RuntimeException("Both sides of root expression depend on HUMN!")
        return solveFor(rootExpression.leftArg, right)
    }
    if (right != null) throw RuntimeException("Neither side of root expression depends on HUMN!")
    return solveFor(rootExpression.rightArg, left)
}


fun main() = timed {
    val expressions = (DATAPATH / "2022/day21.txt").useLines { lines ->
        lines.toList().map(::parseExpression)
    }
    println("Part one: ${partOne(expressions)}")
    println("Part two: ${partTwo(expressions)}")
}
