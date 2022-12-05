package advent.y2020.d15

private fun memoryGame(starting: List<Int>, query: Int): Int {
    if (query < starting.size) return starting[query]

    val lastSpoken = mutableMapOf<Int, Int>().apply {
        starting.forEachIndexed { i, n ->
            if (i != starting.size - 1) {
                this[n] = i + 1
            }
        }
    }
    var prevNum = starting.last()

    for (round in (starting.size + 1)..query) {
        val thisNum = lastSpoken[prevNum]
            ?.let { round - 1 - it }
            ?: 0
        lastSpoken[prevNum] = round - 1
        prevNum = thisNum
    }

    return prevNum
}

fun main() {
    val testInput = listOf(0, 3, 6)
    val input = listOf(14, 8, 16, 0, 1, 17)

    memoryGame(testInput, 2020).also { println("2020/Test input: $it (should be 436)") }
    memoryGame(input, 2020).also { println("Part one: $it") }
    memoryGame(input, 30_000_000).also { println("Part two: $it") }
}
