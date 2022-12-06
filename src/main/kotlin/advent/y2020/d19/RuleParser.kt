package advent.y2020.d19

class RuleParser(ruleStrings: List<String>) {
    private val stringsByNumber = ruleStrings.associate { ruleString ->
        val colonIdx = ruleString.indexOf(':')
        if (colonIdx < 0) throw RuntimeException("Invalid rule: $ruleString")
        ruleString.substring(0, colonIdx).toInt() to ruleString.substring(colonIdx + 2)
    }
    private val parsedRules = mutableMapOf<Int, Rule>()

    // parts must be a list of literals and/or numbers (references to other rules)
    private fun parseParts(parts: List<String>): Rule = parts.map { part ->
        val partInt = part.toIntOrNull()
        when {
            part.first() == '"' && part.last() == '"' -> LiteralRule(part.substring(1, part.length - 1))
            partInt != null -> parseRuleNum(partInt)
            else -> throw RuntimeException("Invalid rule, error on part $part")
        }
    }.let {
        if (it.size == 1) it.first()
        else SequenceRule(it)
    }

    fun parseRuleNum(n: Int): Rule {
        if (n in parsedRules) {
            return parsedRules[n]!!
        }
        val parts = stringsByNumber[n]!!.split(" ")
        return if ("|" in parts) {
            val firstParts = parts.takeWhile { it != "|" }
            val secondParts = parts.takeLastWhile { it != "|" }
            OrRule(parseParts(firstParts), parseParts(secondParts))
        } else {
            parseParts(parts)
        }.also {
            parsedRules[n] = it
        }
    }
}