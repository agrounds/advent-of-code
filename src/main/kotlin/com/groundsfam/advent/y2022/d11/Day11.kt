package com.groundsfam.advent.y2022.d11

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

class Monkey(startingItems: List<Long>, val operation: (Long) -> Long, val test: (Long) -> Int) {
    private val items = ArrayDeque(startingItems)
    var inspections = 0L
        private set

    fun handleItem(): Pair<Long, Int> =
        operation(items.removeFirst())
            .let { it to test(it) }
            .also { inspections++ }

    fun receiveItem(item: Long) {
        items.addLast(item)
    }

    fun isEmpty() = items.isEmpty()
}

fun doRound(monkeys: List<Monkey>) {
    monkeys.forEach { monkey ->
        while (!monkey.isEmpty()) {
            monkey.handleItem()
                .also { (item, nextMonkey) ->
                    monkeys[nextMonkey].receiveItem(item)
                }
        }
    }
}


fun main() {
    val (monkeysOne, monkeysTwo) = (DATAPATH / "2022/day11.txt").useLines {
        it.toList()
    }.let { lines ->
        val mod = (lines.indices step 7).map { i ->
            lines[i + 3].split(" ").last().toLong()
        }.reduce { a, b -> a * b }

        (lines.indices step 7).map { i ->
            val items = lines[i + 1].trim().split(" ")
                .let { it.subList(2, it.size) }
                .map { it.filterNot { c -> c == ',' }.toLong() }
            val operation = lines[i + 2].trim().split(" ").let {
                val (op, arg) = it.subList(4, it.size)
                val partial = when (op) {
                    "+" -> { a: Long, b: Long -> a + b }
                    "*" -> { a: Long, b: Long -> a * b }
                    else -> throw RuntimeException("Illegal operation: ${lines[i + 2]}")
                }
                when (arg) {
                    "old" -> { old: Long -> partial(old, old) }
                    else -> { old: Long -> partial(old, arg.toLong()) }
                }
            }
            val divBy = lines[i + 3].split(" ").last().toLong()
            val ifTrue = lines[i + 4].split(" ").last().toInt()
            val ifFalse = lines[i + 5].split(" ").last().toInt()
            val test = { x: Long -> if (x % divBy == 0L) ifTrue else ifFalse }
            Monkey(items, { old: Long -> operation(old) / 3 }, test) to
                Monkey(items, { old: Long -> operation(old) % mod }, test)
        }
    }.let { list ->
        list.map { it.first } to list.map { it.second }
    }

    repeat(20) {
        doRound(monkeysOne)
    }
    monkeysOne.map{ it.inspections }
        .sorted()
        .takeLast(2)
        .let { (a, b) -> a * b }
        .also { println("Part one: $it") }

    repeat(10_000) {
        doRound(monkeysTwo)
    }
    monkeysTwo.map { it.inspections }
        .sorted()
        .takeLast(2)
        .let { (a, b) -> a * b }
        .also { println("Part two: $it") }
}
