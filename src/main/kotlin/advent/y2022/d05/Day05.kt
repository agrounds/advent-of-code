package advent.y2022.d05
import advent.DATAPATH
import java.util.Stack
import kotlin.io.path.div
import kotlin.io.path.useLines


val moves = mutableListOf<Move>()
val stacksPartOne = Array<Stack<Char>>(9) { Stack() }
val stacksPartTwo = Array<Stack<Char>>(9) { Stack() }

data class Move(val amount: Int, val from: Int, val to: Int)

fun performMovePartOne(move: Move) {
    val from = move.from - 1
    val to = move.to - 1

    if (move.amount > stacksPartOne[from].size) {
        throw RuntimeException("Invalid move $move. Only ${stacksPartOne[from].size} boxes in stack ${move.from}.")
    }
    repeat(move.amount) {
        stacksPartOne[to].push(stacksPartOne[from].pop())
    }
}

fun performMovePartTwo(move: Move) {
    val from = move.from - 1
    val to = move.to - 1

    if (move.amount > stacksPartTwo[from].size) {
        throw RuntimeException("Invalid move $move. Only ${stacksPartTwo[from].size} boxes in stack ${move.from}.")
    }
    val buffer = mutableListOf<Char>()
    repeat(move.amount) {
        buffer.add(stacksPartTwo[from].pop())
    }
    buffer.reversed().onEach(stacksPartTwo[to]::push)
}

fun main() {
    // There are exactly 9 stacks, labeled 1 through 9. I have edited the original input file to remove
    // brackets and the stack labels. Now each character is the name of that box, if present. The ith
    // stack's box is at character i-1. There is a blank line separating the original stack configuration from
    // the list of moves.
    val tmpStacks = Array<MutableList<Char>>(9) { mutableListOf() }

    (DATAPATH / "2022/day05.txt").useLines { lines ->
        var initStacks = true
        lines.forEach { line ->
            if (initStacks) {
                if (line.isBlank()) {
                    initStacks = false
                } else {
                    line.forEachIndexed { i, c ->
                        if (c != ' ') {
                            tmpStacks[i].add(c)
                        }
                    }
                }
            } else {
                // Moves are given using the format "move 3 from 8 to 9" for instance.
                val lineParts = line.split(" ")
                moves.add(Move(lineParts[1].toInt(), lineParts[3].toInt(), lineParts[5].toInt()))
            }
        }
    }

    tmpStacks.forEachIndexed { i, s ->
        s.reversed().forEach {
            stacksPartOne[i].push(it)
            stacksPartTwo[i].push(it)
        }
    }
    moves.forEach(::performMovePartOne)
    stacksPartOne.map { it.peek() }.joinToString("").also { println("Part one: $it") }
    moves.forEach(::performMovePartTwo)
    stacksPartTwo.map { it.peek() }.joinToString("").also { println("Part two: $it") }
}