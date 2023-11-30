package com.groundsfam.advent.y2021.d04

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


typealias Board = MutableList<List<Int>>

// if winningBoard = false, we want to find the board that wins last
fun scoreBoard(nums: List<Int>, boards: List<Board>, winningBoard: Boolean): Int {
    val markers = boards.map { board ->
        board.map { row ->
            row.map { false }.toMutableList()
        }
    }

    fun isWinner(boardMarkers: List<List<Boolean>>) =
        boardMarkers.any { row -> row.all { it } } ||  // won along a row
            boardMarkers.indices.any { x -> boardMarkers.all { it[x] } }  // won along a column

    var i = 0
    var lastWinner: Int? = null
    val winners = mutableSetOf<Int>()
    while ((winningBoard && winners.isEmpty()) || (!winningBoard && winners.size < boards.size)) {
        val num = nums[i]
        boards.forEachIndexed { boardNum, board ->
            board.forEachIndexed { y, row ->
                row.forEachIndexed { x, entry ->
                    if (entry == num) {
                        markers[boardNum][y][x] = true
                    }
                }
            }
            if (boardNum !in winners && isWinner(markers[boardNum])) {
                winners.add(boardNum)
                lastWinner = boardNum
            }
        }
        i++
    }
    val unmarkedSum = boards[lastWinner!!].flatMapIndexed { y, row ->
        row.filterIndexed { x, _ ->
            !markers[lastWinner!!][y][x]
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
    println("Part one: ${scoreBoard(nums, boards, true)}")
    println("Part two: ${scoreBoard(nums, boards, false)}")
}
