package advent

import kotlin.io.path.Path
import kotlin.math.pow
import kotlin.math.round


fun Int.pow(p: Int): Long = this.toDouble().pow(p).toLong()
fun sqrt(n: Int): Int = round(kotlin.math.sqrt(n.toDouble())).toInt()

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

val DATAPATH = Path("${System.getProperty("user.home")}/data/advent-of-code")
