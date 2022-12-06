package advent.y2020.d18

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun evaluate(token: Token): Long = when (token) {
    is LitToken -> token.value
    is TokenList -> {
        val tokens = token.tokens
        var ret = evaluate(tokens[0])
        for (i in 1 until tokens.size step 2) {
            val opToken = tokens[i]
            if (opToken !is OpToken) throw RuntimeException("Illegal token $opToken")
            if (i+1 >= tokens.size) throw RuntimeException("Missing right operand for token $opToken")
            ret = opToken.op.eval(ret, evaluate(tokens[i+1]))
        }
        ret
    }
    is OpToken -> throw RuntimeException("Cannot evaluate an operation without operands")
}


fun main() {
    val tokenizedLines: List<Token> = (DATAPATH / "2020/day18.txt").useLines { lines ->
        lines.toList()
            .map { parseLine(it) }
    }
    tokenizedLines.sumOf(::evaluate)
        .also { println("Part one: $it") }
}