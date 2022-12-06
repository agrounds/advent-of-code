package advent.y2020.d19

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


fun main() {
    val (parser, messages) = (DATAPATH / "2020/day19.txt")
        .useLines { it.toList() }
        .let { lines ->
            (lines
                .takeWhile { it.isNotBlank() }
                .let { RuleParser(it) }
                to lines.takeLastWhile { it.isNotBlank() })
        }
    val rootRule = parser.parseRuleNum(0)
    rootRule.matches(messages[1])
    messages.count(rootRule::matches)
        .also { println("Part one: $it") }
}
