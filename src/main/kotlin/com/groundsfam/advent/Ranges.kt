package com.groundsfam.advent



fun IntRange.rangeIntersect(that: IntRange): IntRange? = when {
    this.first in that -> this.first..min(this.last, that.last)
    this.last in that -> max(this.first, that.first)..this.last
    this.first < that.first && this.last > that.last -> that
    else -> null
}

fun LongRange.rangeIntersect(that: LongRange): LongRange? = when {
    this.first in that -> this.first..min(this.last, that.last)
    this.last in that -> max(this.first, that.first)..this.last
    this.first < that.first && this.last > that.last -> that
    else -> null
}

// returns null if there's no overlap,
// i.e. union is not a single range
fun LongRange.rangeUnion(that: LongRange): LongRange? = when {
    this.first in that.first..(that.last + 1) -> that.first..max(this.last, that.last)
    this.last in (that.first - 1)..that.last -> min(this.first, that.first)..that.last
    this.first < that.first && this.last > that.last -> this
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

val LongRange.size: Long
    get() = last - first + 1
