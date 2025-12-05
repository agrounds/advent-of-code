package com.groundsfam.advent


fun IntRange.rangeIntersect(that: IntRange): IntRange? =
    if (this.first <= that.last && that.first <= this.last) {
        max(this.first, that.first)..min(this.last, that.last)
    } else {
        null
    }

fun LongRange.rangeIntersect(that: LongRange): LongRange? =
    if (this.first <= that.last && that.first <= this.last) {
        max(this.first, that.first)..min(this.last, that.last)
    } else {
        null
    }

// returns null if there's no overlap and ranges are not adjacent,
// i.e. union is not a single range
fun LongRange.rangeUnion(that: LongRange): LongRange? =
    if (this.first <= that.last + 1 && that.first <= this.last + 1) {
        min(this.first, that.first)..max(this.last, that.last)
    } else {
        null
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
