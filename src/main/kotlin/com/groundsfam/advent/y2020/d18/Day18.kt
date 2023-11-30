package com.groundsfam.advent.y2020.d18

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun evaluatePartOne(token: Token): Long = when (token) {
    is NumberToken -> token.value
    is TokenList -> {
        val tokens = token.tokens
        var ret = evaluatePartOne(tokens[0])
        for (i in 1 until tokens.size step 2) {
            val opToken = tokens[i]
            if (opToken !is OpToken) throw RuntimeException("Illegal token $opToken")
            if (i+1 >= tokens.size) throw RuntimeException("Missing right operand for token $opToken")
            ret = opToken.op.eval(ret, evaluatePartOne(tokens[i+1]))
        }
        ret
    }
    is OpToken -> throw RuntimeException("Cannot evaluate an operation without operands")
}

fun evaluatePartTwo(token: Token): Long = when (token) {
    is NumberToken -> token.value
    is TokenList -> {
        // evaluate parentheses groups first, so all that remains is numbers and operations
        val tokens = token.tokens.map {
            if (it is TokenList) NumberToken(evaluatePartTwo(it))
            else it
        }
        // evaluate PLUSes
        val firstPass = mutableListOf((tokens.first() as NumberToken).value)
        for (i in 1 until tokens.size step 2) {
            val opToken = tokens[i]
            if (opToken !is OpToken) throw RuntimeException("Illegal token $opToken")
            when (opToken.op) {
                Operation.PLUS -> {
                    val prev = firstPass.removeLast()
                    if (i+1 >= tokens.size) throw RuntimeException("Missing right operand for token $opToken")
                    firstPass.add(opToken.op.eval(prev, (tokens[i+1] as NumberToken).value))
                }
                Operation.TIMES -> {
                    if (i+1 >= tokens.size) throw RuntimeException("Missing right operand for token $opToken")
                    firstPass.add((tokens[i+1] as NumberToken).value)
                }
            }
        }
        // evaluate TIMESes
        firstPass.reduce(Operation.TIMES::eval)
    }
    is OpToken -> throw RuntimeException("Cannot evaluate an operation without operands")
}


fun main() {
    val tokenizedLines: List<Token> = (DATAPATH / "2020/day18.txt").useLines { lines ->
        lines.toList()
            .map { parseLine(it) }
    }
    tokenizedLines.sumOf(::evaluatePartOne)
        .also { println("Part one: $it") }
    tokenizedLines.sumOf(::evaluatePartTwo)
        .also { println("Part two: $it") }
}