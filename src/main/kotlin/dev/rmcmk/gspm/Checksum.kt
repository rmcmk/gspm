package dev.rmcmk.gspm

import java.io.File
import java.security.MessageDigest

fun ByteArray.calculateChecksum(): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(this)
    return digest.digest()
}

fun File.checksumMatches(content: String): Boolean {
    if (!exists()) {
        return false
    }

    val other = content.toByteArray()
    val bytes = readBytes()
    if (bytes.size != other.size) {
        return false
    }

    return bytes.calculateChecksum().contentEquals(other.calculateChecksum())
}
