package advent.y2021.d04

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


typealias Board = MutableList<List<Int>>

fun findWinningBoard(nums: List<Int>, boards: List<Board>): Int {
    val markers = boards.map { board ->
        board.map { row ->
            row.map { false }.toMutableList()
        }
    }

    fun isWinner(boardMarkers: List<List<Boolean>>) =
        boardMarkers.any { row -> row.all { it } } ||  // won along a row
            boardMarkers.indices.any { x -> boardMarkers.all { it[x] } }  // won along a column

    var i = 0
    while (markers.none(::isWinner)) {
        val num = nums[i]
        boards.forEachIndexed { boardNum, board ->
            board.forEachIndexed { y, row ->
                row.forEachIndexed { x, entry ->
                    if (entry == num) {
                        markers[boardNum][y][x] = true
                    }
                }
            }
        }
        i++
    }
    val winner = markers.indexOfFirst(::isWinner)
    val unmarkedSum = boards[winner].flatMapIndexed { y, row ->
        row.filterIndexed { x, _ ->
            !markers[winner][y][x]
        }
    }.sum()
    return unmarkedSum * nums[i-1]
}

fun main() {
    val (nums, boards) = (DATAPATH / "2021/day04.txt")
        .useLines { it.toList() }
        .let { lines ->
            val nums = lines.first()
                .split(",")
                .map { it.toInt() }
            val boards = mutableListOf<Board>()
            lines.drop(1).forEach { line ->
                if (line.isBlank()) {
                    boards.add(mutableListOf())
                } else {
                    boards.last().add(
                        line.split(Regex("\\s+"))
                            .mapNotNull { it.toIntOrNull() }
                    )
                }
            }
            nums to boards
        }
    println("Part one: ${findWinningBoard(nums, boards)}")
}
