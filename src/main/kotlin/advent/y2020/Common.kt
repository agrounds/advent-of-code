package advent.y2020

import kotlin.io.path.Path
import kotlin.math.pow

val DATAPATH = Path("/Users/alex/data/advent-of-code/2020")

fun Int.pow(p: Int): Long = this.toDouble().pow(p).toLong()