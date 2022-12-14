package advent.y2020.d22

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun simulateCombat(deck1: List<Int>, deck2: List<Int>): Int {
    val mDeck1 = ArrayDeque(deck1)
    val mDeck2 = ArrayDeque(deck2)

    while (mDeck1.isNotEmpty() && mDeck2.isNotEmpty()) {
        val card1 = mDeck1.removeFirst()
        val card2 = mDeck2.removeFirst()
        if (card1 > card2) {
            mDeck1.addLast(card1)
            mDeck1.addLast(card2)
        } else {
            mDeck2.addLast(card2)
            mDeck2.addLast(card1)
        }
    }

    return (if (mDeck1.isEmpty()) mDeck2 else mDeck1).reversed()
        .mapIndexed { i, card ->
            card * (i+1)
        }.sum()
}


fun main() {
    val decks = Array(2) { mutableListOf<Int>() }
    (DATAPATH / "2020/day22.txt").useLines { lines ->
        var i = 0
        lines.forEach { line ->
            if (line.isBlank()) i++
            line.toIntOrNull()?.let {
                decks[i].add(it)
            }
        }
    }
    simulateCombat(decks[0], decks[1])
        .also { println("Part one: $it") }
}
