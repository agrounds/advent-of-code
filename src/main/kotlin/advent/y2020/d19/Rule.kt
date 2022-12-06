package advent.y2020.d19

sealed class Rule
data class LiteralRule(val literal: String) : Rule()
data class SequenceRule(val rules: List<Int>) : Rule()
data class OrRule(val leftRules: List<Int>, val rightRules: List<Int>) : Rule()

class RuleParser(private val ruleStrings: List<String>) {
    private val parsedRules = mutableMapOf<Int, Rule>()

    init {
        ruleStrings.forEach { ruleString ->
            val colonIdx = ruleString.indexOf(':')
            val n = ruleString.substring(0 until colonIdx).toInt()
            val parts = ruleString.substring(colonIdx + 1).split(" ")
            val firstPart = parts.first()
            parsedRules[n] = when {
                parts.size == 1 && firstPart[0] == '"' ->
                    LiteralRule(firstPart.substring(1 until firstPart.length - 1))
                "|" in parts ->
                    OrRule(parts.takeWhile { it != "|" }.map { it.toInt() },
                        parts.takeLastWhile { it != "|" }.map { it.toInt() })
                else ->
                    SequenceRule(parts.map { it.toInt() })
            }
        }
    }
}
