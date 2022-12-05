package advent.y2015.d02

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

data class Box(val w: Int, val l: Int, val h: Int) {
    val surfaceArea: Int = 2*w*l + 2*w*h + 2*l*h
    val minSideArea: Int = minOf(w*l, w*h, l*h)
    val minSidePerimiter: Int = minOf(w+l, w+h, l+h) * 2
    val volume: Int = w*l*h
}


fun main() {
    val boxes = (DATAPATH / "2015/day02.txt").useLines { lines ->
        lines.toList().map { line ->
            val lineParts = line.split('x').map { it.toInt() }
            Box(lineParts[0], lineParts[1], lineParts[2])
        }
    }
    boxes.sumOf { it.surfaceArea + it.minSideArea }
        .also { println("Total wrapping paper: $it square feet") }
    boxes.sumOf { it.minSidePerimiter + it.volume }
        .also { println("Total ribbon: $it feet") }
}