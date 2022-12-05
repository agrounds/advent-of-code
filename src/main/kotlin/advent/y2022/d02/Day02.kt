package advent.y2022.d02

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


fun scorePartOne(opponentChoice: String, playerChoice: String): Int {
    val opponentN = when (opponentChoice) {
        "A" -> 1  // rock
        "B" -> 2  // paper
        "C" -> 3  // scissors
        else -> throw RuntimeException("Invalid opponent choice: $opponentChoice")
    }
    val playerN = when (playerChoice) {
        "X" -> 1  // rock
        "Y" -> 2  // paper
        "Z" -> 3  // scissors
        else -> throw RuntimeException("Invalid player choice: $playerChoice")
    }
    val winLoseScore = when ((playerN - opponentN + 3) % 3) {
        0 -> 3  // draw
        1 -> 6  // win
        2 -> 0  // lose
        else -> throw RuntimeException("Impossible error reached!")
    }

    return playerN + winLoseScore
}


fun scorePartTwo(opponentChoice: String, result: String): Int {
    val opponentN = when (opponentChoice) {
        "A" -> 1  // rock
        "B" -> 2  // paper
        "C" -> 3  // scissors
        else -> throw RuntimeException("Invalid opponent choice: $opponentChoice")
    }
    val (resultN, playerN) = when (result) {
        "X" -> 0 to (opponentN + 1) % 3 + 1  // need to lose, choose opponentN + 2 mod 3
        "Y" -> 3 to opponentN                // need to tie, choose opponentN
        "Z" -> 6 to opponentN % 3 + 1        // need to win, choose opponentN + 1 mod 3
        else -> throw RuntimeException("Invalid result: $result")
    }
    return resultN + playerN
}


fun main() {
    val rounds: List<Pair<String, String>> = (DATAPATH / "2022/day02.txt").useLines { lines ->
        lines.toList().map { line ->
            val (a, b) = line.split(" ")
            a to b
        }
    }
    rounds.sumOf { (opponent, player) -> scorePartOne(opponent, player) }
        .also { println("Total part one = $it") }
    rounds.sumOf { (opponent, result) -> scorePartTwo(opponent, result) }
        .also { println("Total part two = $it")}
}
