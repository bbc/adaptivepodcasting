package uk.co.bbc.perceptivepodcasts.util

import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun Closeable.closeQuietly() {
    try {
        close()
    } catch (ignored: IOException) {
    }
}

fun getMD5EncryptedString(encTarget: File): String {
    var inputStream: FileInputStream? = null
    return try {
        inputStream = FileInputStream(encTarget)
        val md = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(8192)
        while (true) {
            val numBytes = inputStream.read(buffer)
            if (numBytes == -1) break
            md.update(buffer, 0, numBytes)
        }
        md.digest().fold("") { string, byte ->
            string + String.format("%02x", byte)
        }
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        ""
    } catch (e: IOException) {
        e.printStackTrace()
        ""
    } finally {
        inputStream?.closeQuietly()
    }
}

fun ttsSanitize(input: CharSequence): CharSequence {
    var cleaned = ""
    var appending = true
    for (c: Char in input) {
        if (c == '<') {
            appending = false
        } else if (c == '>') {
            appending = true
        } else if (appending) {
            cleaned += c
        }
    }
    return cleaned
}
