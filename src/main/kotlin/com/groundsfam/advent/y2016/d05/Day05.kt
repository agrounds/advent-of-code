package com.groundsfam.advent.y2016.d05

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.security.MessageDigest
import kotlin.io.path.div
import kotlin.io.path.readText

const val NULL = 0.toChar()

fun decodePasswords(doorId: String): Pair<String, String> {
    val md = MessageDigest.getInstance("MD5")
    val pw1 = CharArray(8)
    val pw2 = CharArray(8)
    var pw1Idx = 0
    var n = 0
    while (pw1Idx < 8 || pw2.any { it == NULL }) {
        val digest = md.digest("$doorId$n".toByteArray())
            .take(4)
            .joinToString("") { byte -> "%02x".format(byte) }
        if ((0..4).all { digest[it] == '0' }) {
            if (pw1Idx < 8) {
                pw1[pw1Idx] = digest[5]
                pw1Idx++
            }
            val pw2Idx =
                if (digest[5] in '0'..'9') digest[5] - '0'
                else 10
            if (pw2Idx < 8 && pw2[pw2Idx] == NULL) {
                pw2[pw2Idx] = digest[6]
            }
        }
        n++
    }
    return String(pw1) to String(pw2)
}

fun main() = timed {
    val doorId = (DATAPATH / "2016/day05.txt").readText().trim()
    val (pw1, pw2) = decodePasswords(doorId)
    println("Part one: $pw1")
    println("Part two: $pw2")
}
