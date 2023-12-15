package com.groundsfam.advent.y2023.d15

sealed class Instruction {
    abstract val label: String
}

data class Remove(override val label: String) : Instruction()
data class Insert(override val label: String, val focalLength: Int) : Instruction()

fun String.parse(): Instruction =
    if (this.endsWith('-')) {
        Remove(this.substring(0 until length - 1))
    } else {
        Insert(this.substring(0 until length - 2), this.last().digitToInt())
    }
