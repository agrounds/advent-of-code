package advent.y2020.d18

enum class Operation {
    PLUS,
    TIMES;

    fun eval(a: Int, b: Int): Int = when (this) {
        PLUS -> a + b
        TIMES -> a * b
    }
}

sealed class Token

data class TokenList(val tokens: List<Token>) : Token()
data class LitToken(val value: Int) : Token()
data class OpToken(val op: Operation) : Token()

// from inclusive, to exclusive
fun seekClosingParen(line: String, from: Int, to: Int): Int? {
    var parenCount = 1
    var i = from
    while (i < to) {
        when (line[i]) {
            '(' -> parenCount++
            ')' -> {
                parenCount--
                if (parenCount == 0) return i
            }
        }
        i++
    }
    return null
}

// from inclusive, to exclusive
fun parseLine(line: String, from: Int? = null, to: Int? = null): Token {
    var i = from ?: 0
    val stop = to ?: line.length
    val tokens = mutableListOf<Token>()

    while (i < stop) {
        when {
            line[i] == ' ' ->
                i++
            line[i] in "0123456789" -> {
                var j = i + 1
                while (j < stop && line[j] in "0123456789") j++
                tokens.add(LitToken(line.substring(i, j).toInt()))
                i = j
            }
            line[i] == '+' -> {
                tokens.add(OpToken(Operation.PLUS))
                i++
            }
            line[i] == '*' -> {
                tokens.add(OpToken(Operation.TIMES))
                i++
            }
            line[i] == '(' -> {
                val j = seekClosingParen(line, i+1, stop) ?: throw RuntimeException("Unbalanced parentheses")
                tokens.add(parseLine(line, i+1, j))
                // skip close paren character
                i = j+1
            }
            else ->
                throw RuntimeException("Unexpected token ${line[i]} at position $i")
        }
    }

    return when (tokens.size) {
        0 -> throw RuntimeException("Missing tokens in substring ${line.substring(i, stop)}, chars $i to $stop")
        1 -> tokens.first()
        else -> TokenList(tokens)
    }
}
