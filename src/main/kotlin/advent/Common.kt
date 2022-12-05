package advent

import kotlin.io.path.Path
import kotlin.math.pow


fun Int.pow(p: Int): Long = this.toDouble().pow(p).toLong()

val DATAPATH = Path("${System.getProperty("user.home")}/data/advent-of-code")
