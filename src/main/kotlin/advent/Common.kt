package advent

import java.util.Stack
import kotlin.io.path.Path
import kotlin.math.pow


fun Int.pow(p: Int): Long = this.toDouble().pow(p).toLong()

fun <T> Stack<T>.peekOrNull(): T? =
    if (isEmpty()) null
    else peek()

val DATAPATH = Path("${System.getProperty("user.home")}/data/advent-of-code")
