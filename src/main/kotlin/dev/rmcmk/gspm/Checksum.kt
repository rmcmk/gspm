package dev.rmcmk.gspm

import java.io.File
import java.io.InputStream
import java.security.MessageDigest

fun InputStream.calculateChecksum(): ByteArray {
    val digest = MessageDigest.getInstance("SHA-256")
    digest.update(readAllBytes())
    return digest.digest()
}

fun InputStream.checksumMatches(other: InputStream): Boolean {
    if (other.available() != available()) {
        return false
    }
    return calculateChecksum().contentEquals(other.calculateChecksum())
}

fun InputStream.checksumMatches(other: File) = other.exists() && other.inputStream().checksumMatches(this)
