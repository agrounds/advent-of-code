package com.groundsfam.advent


fun LongRange.rangeIntersect(that: LongRange): LongRange? = when {
    this.first in that -> this.first..min(this.last, that.last)
    this.last in that -> max(this.first, that.first)..this.last
    this.first < that.first && this.last > that.last -> that
    else -> null
}

fun LongRange.except(that: LongRange): List<LongRange> = when {
    this.first in that ->
        if (this.last in that) emptyList()
        else listOf((that.last + 1)..this.last)

    this.last in that ->
        listOf(this.first until that.first)

    that.first in this -> // also implies that.last in this
        listOf(this.first until that.first, (that.last + 1)..this.last)

    else -> // no intersection
        listOf(this)
}
