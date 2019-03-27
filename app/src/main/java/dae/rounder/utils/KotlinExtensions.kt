package dae.rounder.utils

import java.math.BigInteger
import java.security.MessageDigest

fun String?.md5(): String {
    val m = MessageDigest.getInstance("MD5")
    m.reset()
    m.update((this ?: "").toByteArray())
    val digest = m.digest()
    val bigInt = BigInteger(1, digest)
    var hash = bigInt.toString(16)
    while (hash.length < 32) {
        hash = "0$hash"
    }
    return hash
}