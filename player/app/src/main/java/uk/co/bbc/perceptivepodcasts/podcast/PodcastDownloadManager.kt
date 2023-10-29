package uk.co.bbc.perceptivepodcasts.podcast

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import org.xmlpull.v1.XmlPullParserException
import uk.co.bbc.perceptivepodcasts.channel.PodcastChannel
import uk.co.bbc.perceptivepodcasts.channel.RSSParser
import uk.co.bbc.perceptivepodcasts.util.Result
import uk.co.bbc.perceptivepodcasts.util.getMD5EncryptedString
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat

typealias FeedUrlProvider = (() -> String)

class PodcastDownloadManager(
    private val context: Context,
    private val mainScope: CoroutineScope,
    private val appIOScope: CoroutineScope,
    private val feedUrlProvider: FeedUrlProvider
) {

    private val downloadReceiver = DownloadReceiver().also {
        it.onDownloadCompleteListener = this::onDownloadComplete
    }

    private val podcastChannelCacheFilename: String by lazy {
        context.filesDir.toString() + "/" + xmlFilename
    }

    private val tag = "PodcastDownloadManager"
    private val downloadManager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val xmlFilename = "latestPodcasts.xml"
    private val podcastChannelDateFormat = "EEE, dd MMM yyyy HH:mm:ss Z"
    private val downloadItemHashMap = HashMap<Long, MediaItem>()

    private var podcastChannel: PodcastChannel? = null

    var onPodcastChannelResultListener: ((Result<PodcastChannel, Nothing?>) -> Unit) = {}
    var onPodcastDownloadedListener: ((MediaItem, Boolean) -> Unit) = { _, _ -> }
    var onPodcastDownloadStartedListener: ((mediaItem: MediaItem) -> Unit) = {}

    fun downloadPodcast(mItem: MediaItem) {
        val request = DownloadManager.Request(Uri.parse(mItem.link))
        request.setDescription(mItem.summary)
        request.setTitle(mItem.name)
        val extension = mItem.type!!.fileExtension()
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            mItem.guid + extension
        )
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)
        downloadItemHashMap[downloadId] = mItem
        onPodcastDownloadStartedListener(mItem)
    }

    fun refreshPodcastChannel() {
        // Here we're first checking to see if
        // we have a cached podcast channel, if we don't
        // then we get it from the file-cache.
        // If we do then the assumption is that the user
        // has requested a refresh from the network.
        // In that case, or if there's nothing in the
        // file-cache, we fetch from the network.
        if(podcastChannel==null) {
            val channelFromCache = readChannelFromCache()
            if (channelFromCache != null) {
                podcastChannel = channelFromCache
                onPodcastChannelResultListener(Result.Success(channelFromCache))
            } else {
                fetchPodcastChannel()
            }
        } else {
            fetchPodcastChannel()
        }
    }

    fun registerReceivers() {
        val downloadComplete = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(downloadReceiver, downloadComplete)
    }

    private fun onDownloadComplete(downloadId: Long) {
        val mediaItem = downloadItemHashMap[downloadId]
        if (mediaItem != null) {
            mainScope.launch {
                val success = withContext(appIOScope.coroutineContext) {
                    unpackDownload(mediaItem, downloadId)
                }
                onPodcastDownloadedListener(mediaItem, success)
            }
        } else {
            Log.e(tag, "MediaItem download completed, but doesn't appear in item hashmap!")
        }
    }

    private fun unpackDownload(mediaItem: MediaItem, downloadId: Long): Boolean {
        val downloadLocation = context.downloadCopyLocation(mediaItem)
        val downloadCopyFile = File(downloadLocation)
        if (!downloadManager.moveDownload(downloadId, downloadCopyFile)) {
            return false
        }

        // Podcast items can either be a ZIP file containing all media assets (ZIP),
        // or a single media asset (MP3).
        if (!checkMD5(mediaItem, downloadCopyFile)) {
            return false
        }

        if (mediaItem.type?.isZip() == true) {
            return unzipToFile(mediaItem, downloadCopyFile)
        }

        if (mediaItem.type?.isMediaAsset() == true) {
            createMediaAssets(downloadCopyFile, mediaItem)
            return true
        }

        return false
    }

    private fun createMediaAssets(downloadCopyFile: File, mediaItem: MediaItem) {
        // IF not a ZIP, assume a media asset and copy file from download to podcasts folder.
        Log.d(tag, "Not a ZIP, so assume media asset: $downloadCopyFile")
        // Check directory exists, if not create.
        val mediaItemLocation = File(context.mediaItemLocation(mediaItem))
        if (!mediaItemLocation.isDirectory) {
            mediaItemLocation.mkdirs()
        }
        // Create destination location
        val audioDirStr = context.mediaItemLocation(mediaItem, Asset.AUDIO)
        val extension = mediaItem.type!!.fileExtension()
        val dest = File(audioDirStr, mediaItem.guid + extension)
        // Copy media asset from downloads to podcast directory.
        try {
            FileUtils.copyFile(downloadCopyFile, dest)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        downloadCopyFile.delete()
        writeBasicSmilFile(mediaItem)
        writeBasicManifestFile(mediaItem)
        val imagesDirStr = context.mediaItemLocation(mediaItem, Asset.IMAGES)
        val imageDir = File(imagesDirStr)
        if (!imageDir.isDirectory) {
            imageDir.mkdirs()
        }
        context.copyAssetTo("small.jpg", imagesDirStr)
        context.copyAssetTo("medium.jpg", imagesDirStr)
        context.copyAssetTo("large.jpg", imagesDirStr)
    }

    private fun unzipToFile(mediaItem: MediaItem, downloadCopyFile: File): Boolean {
        val destination = context.mediaItemLocation(mediaItem)
        return unZipAndDelete(downloadCopyFile, destination)
    }

    private fun checkMD5(mediaItem: MediaItem, file: File): Boolean {
        val expectedMd5 = mediaItem.md5
        var md5Check = true
        if (expectedMd5 != null) {
            val calculatedMd5 = getMD5EncryptedString(file)
            md5Check = calculatedMd5 == expectedMd5
        }
        return md5Check
    }

    private fun writeBasicSmilFile(mediaItem: MediaItem) {
        val smilXml = mediaItem.buildBasicSmilXml()
        val location = context.mediaItemLocation(mediaItem)
        Log.d(tag, "Write SMIL file: $smilXml")
        smilXml.writeToFile(location, "smil.xml")
    }

    private fun writeBasicManifestFile(mediaItem: MediaItem) {
        val manifest = mediaItem.buildBasicManifest()
        val location = context.mediaItemLocation(mediaItem)
        Log.d(tag, "Write Manifest file: $manifest")
        manifest.writeToFile(location, "manifest.json")
    }

    private fun fetchPodcastChannel() {
        val stringRequest = StringRequest(
            Request.Method.GET,
            feedUrlProvider(),
            this::onRssFeedResponse,
            this::onRssFeedError
        )

        Volley.newRequestQueue(context).add(stringRequest)
    }

    private fun readChannelFromCache(): PodcastChannel? {
        return try {
            val file = File(podcastChannelCacheFilename)
            val fileInputStream = FileInputStream(file)
            RSSParser.parse(fileInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            null
        }
    }

    private fun onRssFeedError(error: VolleyError?) {
        val message = if (error is NetworkError) {
            "Cannot connect to Internet.\nPlease check your Internet connection."
        } else {
            "Error retrieving RSS feed.\nPlease provide a valid RSS feed via Preferences."
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        onPodcastChannelResultListener(Result.Error)
    }

    private fun onRssFeedResponse(response: String) {
        // here we're first updating the file-cache
        // then immediately reading from it. This
        // checks both that the write succeeded and that
        // the cached file has valid data. We then check
        // to see if the file-cache version is newer than
        // the existing version. All branches call the
        // result listener.
        val oldChannel = podcastChannel

        response.writeToFile(podcastChannelCacheFilename)
        val channelFromCache = readChannelFromCache()

        if(channelFromCache!=null) {
            val newChannel = when {
                oldChannel == null  -> channelFromCache
                compareChannels(channelFromCache, oldChannel) -> channelFromCache
                else -> oldChannel
            }
            podcastChannel = newChannel
            onPodcastChannelResultListener(Result.Success(newChannel))
        } else {
            onPodcastChannelResultListener(Result.Error)
        }
    }

    // returns true if the new channel is newer or different compared to the old one
    private fun compareChannels(newChannel: PodcastChannel, oldChannel: PodcastChannel): Boolean {
        return try {
            val format = SimpleDateFormat(podcastChannelDateFormat)
            val newData = format.parse(newChannel.lastBuildDate)!!
            val oldDate = format.parse(oldChannel.lastBuildDate)!!
            val newTitle = newChannel.title
            val oldTitle = oldChannel.title

            (newTitle != oldTitle) || newData.after(oldDate)
        } catch (e: ParseException) {
            false
        }
    }

    // Determines whether podcast has already been downloaded and is available on the device
    fun isPodcastDownloaded(mediaItem: MediaItem): Boolean {
        return File(context.mediaItemLocation(mediaItem)).isDirectory
    }

}

private fun MediaItem.buildBasicSmilXml(): String {
    val extension = type!!.fileExtension()
    return """
                |<?xml version="1.0" encoding="UTF-8"?>
                |<smil><head></head><body><seq>
                |   <audio src="AUDIO/$guid$extension" type="$type"/>
                |</seq></body></smil>
                |""".trimMargin()
}

private fun MediaItem.buildBasicManifest(): String {
    return """"
                |{
                |   "title":"$guid",
                |   "series":1,
                |   "episode":1,
                |   "imageryURIs":[
                |       {"large":"IMAGES/large.jpg"},
                |       {"medium":"IMAGES/medium.jpg"},
                |       {"small":"IMAGES/small.jpg"}
                |   ],
                |   "creditGroups":[
                |       {"name":"Audio credits","credits":[]},
                |       {"name":"Producer(s)","credits":[]}
                |   ]
                |}
                |"""".trimMargin()
}
