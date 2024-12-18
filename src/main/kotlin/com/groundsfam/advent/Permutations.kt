package com.groundsfam.advent

fun <T> generatePermutations(elems: List<T>): Iterator<List<T>> = iterator {
    if (elems.isEmpty()) {
        yield(emptyList())
    } else {
        elems.forEachIndexed { i, x ->
            generatePermutations(elems.subList(0, i) + elems.subList(i + 1, elems.size)).forEach { xs ->
                yield(listOf(x) + xs)
            }
        }
    }
}
