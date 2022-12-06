package advent.y2020.d18

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


fun main() {
    val tokenizedLines: List<Token> = (DATAPATH / "2020/day18.txt").useLines { lines ->
        lines.toList()
            .map { parseLine(it) }
    }
}