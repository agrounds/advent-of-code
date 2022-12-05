package advent.y2015.d11

fun CharArray.next() {
    var carry = true
    var i = this.size - 1
    while (carry && i >= 0) {
        if (this[i] in 'a'..'y') {
            this[i]++
            carry = false
        } else {
            this[i] = 'a'
        }
        i--
    }
}

fun goodPassword(candidate: CharArray): Boolean {
    var hasStraight = false
    var containsBadLetter = false
    val foundPairs = mutableSetOf<Char>()

    candidate.forEachIndexed { i, c ->
        if (c in "iol") {
            containsBadLetter = true
        }
        if (i+2 < candidate.size && candidate[i+1] == c+1 && candidate[i+2] == c+2) {
            hasStraight = true
        }
        if (i+1 < candidate.size && candidate[i+1] == c) {
            foundPairs.add(c)
        }
    }

    return hasStraight && !containsBadLetter && foundPairs.size >= 2
}


fun main() {
    val password = "hxbxwxba".toCharArray()
    while (!goodPassword(password)) {
        password.next()
    }
    println("Part one: ${password.joinToString("")}")
    password.next()
    while (!goodPassword(password)) {
        password.next()
    }
    println("Part two: ${password.joinToString("")}")
}
