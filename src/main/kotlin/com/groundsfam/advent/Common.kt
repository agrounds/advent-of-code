package com.groundsfam.advent

import org.apache.commons.lang3.time.StopWatch
import kotlin.io.path.Path


val DATAPATH = Path("${System.getProperty("user.home")}/data/advent-of-code")

fun timed(block: () -> Unit) {
    val watch = StopWatch()
    watch.start()

    block()

    watch.stop()
    println("Took ${watch.duration.toMillis()}ms")
}
