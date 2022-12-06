package advent.y2020.d18

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun evaluatePartOne(token: Token): Long = when (token) {
    is LitToken -> token.value
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
    is LitToken -> token.value
    is TokenList -> {
        val tokens = token.tokens
        val firstPass = mutableListOf<Token>()
        var i = 0
        while (i < tokens.size) {
            // seek all immediately following PLUSes
            var j = i + 1
            while (j < tokens.size && (tokens[j] as? OpToken)?.op == Operation.PLUS) {
                j += 2
            }
            // if we found PLUSes, evaluate them
            // otherwise leave tokens as-is
            if (j > i + 1) {
                var total: Long = 0
                for (k in i..j step 2) {
                    total += evaluatePartTwo(tokens[k])
                }
                firstPass.add(LitToken(total))
            } else {
                firstPass.add(tokens[i])
            }
            i = j
        }
        // now that all PLUSes are evaluated, the original method works because there is no precedence to consider
        evaluatePartOne(TokenList(firstPass))
    }
    is OpToken -> throw RuntimeException("Cannot evaluate an operation without operands")
}


fun main() {
    val tokenizedLines: List<Token> = (DATAPATH / "2020/day18-example4.txt").useLines { lines ->
        lines.toList()
            .map { parseLine(it) }
    }
    tokenizedLines.sumOf(::evaluatePartOne)
        .also { println("Part one: $it") }
    tokenizedLines.sumOf(::evaluatePartTwo)
        .also { println("Part two: $it") }
}