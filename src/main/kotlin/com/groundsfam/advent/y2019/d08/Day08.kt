package com.groundsfam.advent.y2019.d08

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText

const val WIDTH = 25
const val HEIGHT = 6

fun fewestZeros(image: List<Int>): Int =
    image.chunked(WIDTH * HEIGHT)
        .minBy { layer ->
            layer.count { it == 0 }
        }
        .let { layer ->
            layer.count { it == 1 } * layer.count { it == 2 }
        }

fun render(image: List<Int>): String {
    val rendered = IntArray(WIDTH * HEIGHT) { 2 }
    image.chunked(WIDTH * HEIGHT).forEach { layer ->
        layer.forEachIndexed { i, color ->
            if (rendered[i] == 2) {
                rendered[i] = color
            }
        }
    }
    return (0 until HEIGHT).joinToString("\n") { r ->
        (0 until WIDTH).joinToString("") { c ->
            when (rendered[r * WIDTH + c]) {
                0 -> "."
                1 -> "#"
                else -> "?" }
        }
    }
}

fun main() = timed {
    val image = (DATAPATH / "2019/day08.txt").readText()
        .trim()
        .map(Char::digitToInt)
    println("Part one: ${fewestZeros(image)}")
    println("Part two:")
    println(render(image))
}
