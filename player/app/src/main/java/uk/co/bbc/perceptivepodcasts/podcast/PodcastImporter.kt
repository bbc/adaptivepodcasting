package uk.co.bbc.perceptivepodcasts.podcast

import android.content.Context
import android.net.Uri
import uk.co.bbc.perceptivepodcasts.util.Result
import uk.co.bbc.perceptivepodcasts.util.unzipContent

class PodcastImporter(private val context: Context) {

    fun importPodcast(uri: Uri): Result<MediaItem, Any?> {
        val guid = "newpodcast_" + System.currentTimeMillis()
        val targetLocation = context.mediaItemLocation(guid)
        return if (unzipContent(context, uri, targetLocation)) {
            importUnzippedPodcast(targetLocation, guid)
        } else {
            Result.Error
        }
    }

    private fun importUnzippedPodcast(targetLocation: String, guid: String): Result<MediaItem, Any?> {
        try {
            // Read Manifest
            val pi = parseItemManifest(targetLocation)!!
            // Create MediaITem with for new podcast.
            val mediaItem = MediaItem(
                name = pi.title,
                guid = guid,
                link = "",
                type = MediaItemType.ZIP,
                summary = "",
                description = "",
                md5 = ""
            )
            return Result.Success(mediaItem)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.Error(e)
        }
    }
}
