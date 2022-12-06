package advent.y2020.d19

sealed class Rule {
    abstract fun matches(string: String): Boolean
}
data class LiteralRule(val literal: String) : Rule() {
    override fun toString() = literal
    override fun matches(string: String): Boolean =
        string == literal
}
data class SequenceRule(val sequence: List<Rule>) : Rule() {
    override fun toString() = "(${sequence.joinToString("")})"
    override fun matches(string: String): Boolean {
        fun subMatches(stringStart: Int, seqStart: Int): Boolean {
            val seq = sequence
            if (stringStart == string.length && seqStart == sequence.size) {
                return true
            }
            // out of rules, but characters in the string remain
            if (seqStart >= sequence.size) {
                return false
            }
            for (i in stringStart+1..string.length) {
                if (sequence[seqStart].matches(string.substring(stringStart, i)))
                    if (subMatches(i, seqStart+1))
                        return true
            }
            return false
        }
        return subMatches(0, 0)
    }
}
data class OrRule(val a: Rule, val b: Rule) : Rule() {
    override fun toString() = "$a|$b"
    override fun matches(string: String): Boolean =
        a.matches(string) || b.matches(string)
}
