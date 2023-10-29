package uk.co.bbc.perceptivepodcasts.channel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.co.bbc.perceptivepodcasts.podcast.MediaItem
import org.apache.commons.io.FileUtils.deleteDirectory
import uk.co.bbc.perceptivepodcasts.podcast.PodcastDownloadManager
import uk.co.bbc.perceptivepodcasts.podcast.PodcastImporter
import uk.co.bbc.perceptivepodcasts.podcast.podcastsDirectory
import uk.co.bbc.perceptivepodcasts.podcast.smallBitmapPath
import uk.co.bbc.perceptivepodcasts.util.Result
import java.io.File
import java.io.IOException
import java.util.function.Function

enum class DownloadState {
    NULL, DOWNLOADING, DOWNLOADED, DOWNLOAD_ERROR
}

enum class ImportState {
    NULL, IMPORTING, IMPORT_SUCCESS, IMPORT_FAILURE
}

enum class ChannelRefreshState {
    NULL, REFRESHING
}

data class ChannelItem(var mediaItem: MediaItem, var state: DownloadState, var bitmap: Bitmap?)

data class ChannelState(
    val channelRefreshState: ChannelRefreshState,
    val channelItems: List<ChannelItem>,
    val importState: ImportState
)

// Copy-with-change builder method that changes a single
// item if it matches the supplied media item.
fun ChannelState.withUpdatedChannelItem(
    mediaItem: MediaItem,
    changeFunc: Function<ChannelItem, ChannelItem>
): ChannelState {
    val items = channelItems.map { item: ChannelItem ->
        if (item.mediaItem.guid == mediaItem.guid) {
            changeFunc.apply(item)
        } else {
            item
        }
    }
    return copy(channelItems = items)
}

// The store of the channel data - the list of all the podcasts from an
// RSS feed (from the download manager) and any imported podcasts from
// zip files. This class observes the download manager and the importer
// and updates the live data accordingly. Some complexity comes from the
// expectation that the object provided by live data is a different
// object with each change - so we can't mutate a member in place, we
// need to create a new instance whenever the data changes.
class ChannelRepo(
    private val downloadManager: PodcastDownloadManager,
    private val context: Context,
    private val appIOScope: CoroutineScope,
    private val mainScope: CoroutineScope
) {
    val liveData = MutableLiveData(ChannelState(ChannelRefreshState.NULL, emptyList(), ImportState.NULL))

    private var imported = emptyList<MediaItem>()
    private val podcastImporter = PodcastImporter(context)

    init {
        downloadManager.onPodcastDownloadedListener = this::onPodcastDownloaded
        downloadManager.onPodcastChannelResultListener = this::onChannelRefreshResult
        downloadManager.onPodcastDownloadStartedListener = this::onPodcastDownloadStarted
    }

    // this function runs the passed-in lambda on the main thread
    // allowing for mutation of the main-thread-latest version of
    // the state. Don't do any IO in the lambda!
    private fun updateState(function: (oldState: ChannelState) -> ChannelState) {
        mainScope.launch {
            liveData.value = function(liveData.value!!)
        }
    }

    private fun onChannelRefreshResult(result: Result<PodcastChannel, Any?> ) {
        removeDeletedImports()
        updateState { oldState ->
            val channelItems = when(result) {
                // when we get an error back from the channel refresh
                // just use the existing channel items, i.e. don't clear
                // them.
                is Result.Error -> oldState.channelItems
                // Otherwise merge them with the imported podcasts
                is Result.Success -> mergeChannel(result.item)
            }
            oldState.copy(
                channelRefreshState = ChannelRefreshState.NULL,
                channelItems = channelItems
            )
        }
    }

    private fun mergeChannel(podcastChannel: PodcastChannel): List<ChannelItem> {
        val itemList: List<MediaItem> = imported + podcastChannel.mediaItemList
        return itemList.map { mediaItem: MediaItem -> mediaItem.toChannelItem() }
    }

    private fun removeDeletedImports() {
        // so that we only show imported podcasts
        // that are actually downloaded (which can
        // get out of sync if we delete all downloads)
        // let's check them here.
        imported = imported.filter { downloadManager.isPodcastDownloaded(it) }
    }

    private fun MediaItem.toChannelItem(): ChannelItem {
        return if (downloadManager.isPodcastDownloaded(this)) {
            val bitmap: Bitmap? = BitmapFactory.decodeFile(context.smallBitmapPath(this))
            ChannelItem(this, DownloadState.DOWNLOADED, bitmap)
        } else if (isDownloading(this)) {
            ChannelItem(this, DownloadState.DOWNLOADING, null)
        } else {
            ChannelItem(this, DownloadState.NULL, null)
        }
    }

    private fun onPodcastDownloadStarted(mediaItem: MediaItem) {
        updateState { oldState ->
            oldState.withUpdatedChannelItem(mediaItem) { item ->
                ChannelItem(item.mediaItem, DownloadState.DOWNLOADING, item.bitmap)
            }
        }
    }

    private fun onPodcastDownloaded(mediaItem: MediaItem, success: Boolean) {
        val downloadState = if (success) DownloadState.DOWNLOADED else DownloadState.DOWNLOAD_ERROR

        val bitmap = if (success) {
            BitmapFactory.decodeFile(context.smallBitmapPath(mediaItem))
        } else {
            null
        }

        updateState { oldState ->
            oldState.withUpdatedChannelItem(mediaItem) {
                ChannelItem(mediaItem, downloadState, bitmap)
            }
        }

        if (!success) {
            updateState { oldState ->
                oldState.withUpdatedChannelItem(mediaItem) { item ->
                    ChannelItem(mediaItem, DownloadState.NULL, item.bitmap)
                }
            }
        }
    }

    private fun isDownloading(mediaItem: MediaItem): Boolean {
        return liveData.value!!.channelItems.any { item: ChannelItem ->
            val downloading = item.state == DownloadState.DOWNLOADING
            val same = item.mediaItem.guid == mediaItem.guid
            same && downloading
        }
    }

    private fun onImportStarted() {
        updateState { oldState ->
            oldState.copy(importState = ImportState.IMPORTING)
        }
    }

    private fun onImportCompleted(result: Result<MediaItem, Any?>) {
        when (result) {
            is Result.Success -> onImportSuccess(result.item)
            is Result.Error -> onImportFailed()
        }
    }

    private fun onImportFailed() {
        updateState { it.copy(importState = ImportState.IMPORT_FAILURE) }
    }

    private fun onImportSuccess(mediaItem: MediaItem) {
        // On success save the imported one to the
        // list of imported podcasts
        imported = listOf(mediaItem) + imported

        val bitmap = BitmapFactory.decodeFile(context.smallBitmapPath(mediaItem))
        val channelItem = ChannelItem(mediaItem, DownloadState.DOWNLOADED, bitmap)

        updateState { oldState ->
            val channelItemList = listOf(channelItem) + oldState.channelItems
            oldState.copy(
                channelItems = channelItemList,
                importState = ImportState.IMPORT_SUCCESS
            )
        }
    }

    fun clearImportState() {
        updateState { it.copy(importState = ImportState.NULL) }
    }

    fun refreshPodcastList() {
        updateState { it.copy(channelRefreshState = ChannelRefreshState.REFRESHING) }
        downloadManager.refreshPodcastChannel()
    }

    fun downloadPodcast(mediaItem: MediaItem) {
        downloadManager.downloadPodcast(mediaItem)
    }

    fun addPodcastFromFile(uri: Uri) {
        appIOScope.launch {
            onImportStarted()
            val result = podcastImporter.importPodcast(uri)
            onImportCompleted(result)
        }
    }

    fun deleteAllDownloads() {
        markAllDownloadsNull()

        val podcastsLocation = context.podcastsDirectory()
        val podcastFolder = File(podcastsLocation) //delete podcasts folder
        try {
            deleteDirectory(podcastFolder)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun markAllDownloadsNull() {
        updateState { state ->
            val clearedDownloads = state.channelItems.map {
                it.copy(state = DownloadState.NULL)
            }
            state.copy(channelItems = clearedDownloads)
        }
    }

}
