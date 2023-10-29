package uk.co.bbc.perceptivepodcasts.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

fun unzipContent(
    context: Context,
    contentUri: Uri,
    targetDir: String
): Boolean {

    if(!createDirectory(targetDir)) {
        return false
    }

    var zipStream: ZipInputStream? = null
    var inputStream: InputStream? = null

    return try {
        inputStream = context.contentResolver.openInputStream(contentUri)
        zipStream = ZipInputStream(inputStream)
        while (true) {
            val entry = zipStream.nextEntry ?: break
            val pathname = "$targetDir/${entry.name}"
            if (entry.isDirectory) {
                createDirectory(pathname)
            } else {
                zipStream.writeTo(pathname)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } finally {
        inputStream?.closeQuietly()
        zipStream?.closeQuietly()
    }
}

private fun ZipInputStream.writeTo(destination: String) {
    var output: FileOutputStream? = null
    try {
        output = FileOutputStream(destination)
        val buffer = ByteArray(1024)
        while(true) {
            val bytesRead = read(buffer)
            if(bytesRead == -1) break
            output.write(buffer, 0, bytesRead)
        }
    }
    finally {
        closeEntry()
        output?.closeQuietly()
    }
}

private fun createDirectory(pathname: String): Boolean {
    val f = File(pathname)
    return if(f.exists()) {
        return f.isDirectory
    } else {
        // attempt to make the dir
        f.mkdirs()
    }
}
