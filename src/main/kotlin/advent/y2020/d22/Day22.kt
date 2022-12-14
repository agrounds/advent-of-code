package advent.y2020.d22

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun scoreDeck(deck: List<Int>): Int = deck.reversed()
    .mapIndexed { i, card ->
        card * (i + 1)
    }.sum()

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

    return scoreDeck(if (mDeck1.isEmpty()) mDeck2 else mDeck1)
}


// returns pair of (winnerIsPlayerOne, score)
fun simulateRecursiveCombat(deck1: ArrayDeque<Int>, deck2: ArrayDeque<Int>): Pair<Boolean, Int> {
    val prevGames = mutableSetOf<Pair<List<Int>, List<Int>>>()

    while (deck1.isNotEmpty() && deck2.isNotEmpty()) {
        if (!prevGames.add(deck1.toList() to deck2.toList())) {
            return true to scoreDeck(deck1)
        }

        val card1 = deck1.removeFirst()
        val card2 = deck2.removeFirst()
        val nextWinnerPlayerOne = if (card1 <= deck1.size && card2 <= deck2.size) {
            val nextDeck1 = ArrayDeque(deck1.take(card1))
            val nextDeck2 = ArrayDeque(deck2.take(card2))
            simulateRecursiveCombat(nextDeck1, nextDeck2).first
        } else {
            card1 > card2
        }

        if (nextWinnerPlayerOne) {
            deck1.addLast(card1)
            deck1.addLast(card2)
        } else {
            deck2.addLast(card2)
            deck2.addLast(card1)
        }
    }

    return if (deck1.isNotEmpty()) {
        true to scoreDeck(deck1)
    } else {
        false to scoreDeck(deck2)
    }
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
    simulateRecursiveCombat(ArrayDeque(decks[0]), ArrayDeque(decks[1]))
        .also { println("Part two: ${it.second}") }
}
