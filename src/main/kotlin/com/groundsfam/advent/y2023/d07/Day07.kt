package com.groundsfam.advent.y2023.d07

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines

val cardOrderPartOne = listOf(
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    'T',
    'J',
    'Q',
    'K',
    'A',
)
val cardOrderPartTwo = listOf(
    'J',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9',
    'T',
    'Q',
    'K',
    'A',
)

data class Hand(val cards: String, val bid: Long)

fun List<Int>.toType() = when {
    // five of a kind
    5 in this -> 7
    // four of a kind
    4 in this -> 6
    // full house
    3 in this && 2 in this -> 5
    // three of a kind
    3 in this -> 4
    // two pair
    this.filter { it == 2 }.size == 2 -> 3
    // one pair
    2 in this -> 2
    // high card
    else -> 1
}

fun Hand.typePartOne(): Int =
    cards.groupBy { it }
        .map { (_, l) -> l.size }
        .toType()

fun Hand.comparePartOne(that: Hand): Int =
    if (this.typePartOne() != that.typePartOne()) this.typePartOne() - that.typePartOne()
    else this.cards
        .indices
        .first { this.cards[it] != that.cards[it] }
        .let { cardOrderPartOne.indexOf(this.cards[it]) - cardOrderPartOne.indexOf(that.cards[it]) }

fun Hand.typePartTwo(): Int {
    if (this.cards == "JJJJJ") {
        // all jokers - five of a kind
        return 7
    }
    val jokers = this.cards.filter { it == 'J' }.length
    val counts = this.cards.filterNot { it == 'J' }.groupBy { it }.mapTo(mutableListOf()) { (_, l) -> l.size }
    val maxIdx = counts.indexOf(counts.max())
    counts[maxIdx] += jokers
    return counts.toType()
}

fun Hand.comparePartTwo(that: Hand): Int =
    if (this.typePartTwo() != that.typePartTwo()) this.typePartTwo() - that.typePartTwo()
    else this.cards
        .indices
        .first { this.cards[it] != that.cards[it] }
        .let { cardOrderPartTwo.indexOf(this.cards[it]) - cardOrderPartTwo.indexOf(that.cards[it]) }

fun main() = timed {
    val hands = (DATAPATH / "2023/day07.txt").readLines()
        .map { line ->
            line.split(" ")
                .let { (cards, bid) ->
                    Hand(cards, bid.toLong())
                }
        }
    hands.sortedWith { a, b -> a.comparePartOne(b) }
        .mapIndexed { i, hand ->
            hand.bid * (i + 1)
        }
        .sum()
        .also { println("Part one $it") }
    hands.sortedWith { a, b -> a.comparePartTwo(b) }
        .mapIndexed { i, hand ->
            hand.bid * (i + 1)
        }
        .sum()
        .also { println("Part two $it") }
}
