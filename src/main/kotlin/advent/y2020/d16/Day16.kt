package advent.y2020.d16

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

private data class Rule(val fieldName: String, val validRanges: List<IntRange>)

private fun Rule.validValue(value: Int): Boolean =
    validRanges.any { value in it }

private fun scanningErrorRate(rules: List<Rule>, ticket: List<Int>): Int =
    ticket.sumOf { value ->
        if (rules.any { it.validValue(value) }) 0
        else value
    }

// field names in ticket values order
private fun determineFields(rules: List<Rule>, validTickets: List<List<Int>>): List<String> {
    val fieldsInOrder: MutableList<String?> = rules.map { null }.toMutableList()
    val remainingRules = rules.toMutableList()

    while (fieldsInOrder.any { it == null }) {
        fieldsInOrder.forEachIndexed { i, maybeField ->
            if (maybeField == null) {
                remainingRules.filter { rule ->
                    validTickets.map { it[i] }.all { rule.validValue(it) }
                }.let { filteredRules ->
                    if (filteredRules.size == 1) {
                        fieldsInOrder[i] = filteredRules[0].fieldName
                        remainingRules.remove(filteredRules[0])
                    }
                }
            }
        }
    }

    return fieldsInOrder.map { it!! }
}

fun main() {
    var blankLines = 0
    val ruleLines = mutableListOf<String>()
    val ticketLines = mutableListOf<String>()
    (DATAPATH / "2020/day16.txt").useLines { lines ->
        lines.forEach { line ->
            when {
                line.isEmpty() -> blankLines++
                blankLines == 0 -> ruleLines.add(line)
                line[0].isDigit() -> ticketLines.add(line) // ignore non-ticket lines
            }
        }
    }

    val rules = ruleLines.map { line ->
        Rule(
            line.substringBefore(":"),
            line.substringAfter(": ").split(" or ").map {
                it.split("-").let { parts ->
                    parts[0].toInt()..parts[1].toInt()
                }
            }
        )
    }

    // NOTE: My ticket is tickets[0], nearby tickets are the rest
    val tickets = ticketLines.map { line ->
        line.split(",").map { it.toInt() }
    }
    val myTicket = tickets[0]
    val otherTickets = tickets.subList(1, tickets.size)

    otherTickets.sumOf { scanningErrorRate(rules, it) }
        .also { println("Part one: $it") }

    otherTickets.filter { ticket ->
        ticket.all { value ->
            rules.any { it.validValue(value) }
        }
    }.let { validTickets ->
        determineFields(rules, validTickets)
    }.let { fieldNamesInOrder ->
        fieldNamesInOrder.mapIndexedNotNull { index, s ->
            if (s.startsWith("departure")) index
            else null
        }
    }.map { idx ->
        myTicket[idx].toLong()
    }.reduce(Long::times)
        .also {
            println("Part two: $it")
        }
}