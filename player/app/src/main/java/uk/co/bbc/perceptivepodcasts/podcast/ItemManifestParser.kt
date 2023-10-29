package uk.co.bbc.perceptivepodcasts.podcast

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.FileReader
import java.io.IOException

private const val MANIFEST_NAME = "manifest.json"

@Throws(IOException::class)
fun parseItemManifest(manifestPath: String): PodcastInfo? {
    return try {
        val reader = JsonReader(FileReader("$manifestPath/$MANIFEST_NAME"))
        val type = object : TypeToken<PodcastInfo?>() {}.type
        Gson().fromJson(reader, type)
    } catch (e: JsonParseException) {
        null
    }
}
