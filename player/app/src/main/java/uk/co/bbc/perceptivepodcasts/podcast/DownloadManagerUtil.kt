package uk.co.bbc.perceptivepodcasts.podcast

import android.app.DownloadManager
import android.content.Context
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import uk.co.bbc.perceptivepodcasts.util.closeQuietly
import java.io.*

fun DownloadManager.moveDownload(downloadId: Long, destination: File): Boolean {
    var fileStream: InputStream? = null
    var copyStream: OutputStream? = null
    destination.parentFile?.mkdirs() ?: return false
    return try {
        val pfd = openDownloadedFile(downloadId)
        fileStream = FileInputStream(pfd.fileDescriptor)
        copyStream = FileOutputStream(destination)
        fileStream.copy(copyStream)
        remove(downloadId)
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    } finally {
        fileStream?.closeQuietly()
        copyStream?.closeQuietly()
    }
}

private fun InputStream.copy(copyStream: OutputStream, bufferSize: Int = 1024) {
    val buffer = ByteArray(bufferSize)
    while(true) {
        val numBytes = read(buffer)
        if(numBytes==-1) break
        copyStream.write(buffer, 0, numBytes)
    }
    copyStream.flush()
}

fun Context.copyAssetTo(assetPath: String, destDir: String): Boolean {
    var inputStream: InputStream? = null
    var out: OutputStream? = null
    return try {
        inputStream = assets.open(assetPath)
        out = FileOutputStream(destDir + assetPath)
        inputStream.copyTo(out)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    } finally {
        inputStream?.closeQuietly()
        out?.closeQuietly()
    }
}

fun String.writeToFile(directory: String, fileName: String): Boolean {
    return this.writeToFile(File(directory, fileName))
}

fun String.writeToFile(fileName: String): Boolean {
    return this.writeToFile(File(fileName))
}

fun String.writeToFile(file: File): Boolean {
    var out: FileWriter? = null
    return try {
        out = FileWriter(file)
        out.write(this)
        out.close()
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    } finally {
        out?.closeQuietly()
    }
}

// Unzips passed zip file and extracts to app internal storage into a directory of the same name
@Throws(ZipException::class)
fun unZipAndDelete(file: File, destination: String): Boolean {
    return try {
        val zipPath = file.absolutePath
        val zipFile = ZipFile(zipPath)
        zipFile.extractAll(destination)
        file.delete()
        true
    } catch (e: ZipException) {
        e.printStackTrace()
        false
    }
}
