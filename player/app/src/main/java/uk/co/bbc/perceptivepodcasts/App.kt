package uk.co.bbc.perceptivepodcasts

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import uk.co.bbc.perceptivepodcasts.channel.ChannelRepo
import uk.co.bbc.perceptivepodcasts.playback.PlaybackController
import uk.co.bbc.perceptivepodcasts.playback.PlaybackManager
import uk.co.bbc.perceptivepodcasts.podcast.PodcastDownloadManager
import uk.co.bbc.perceptivepodcasts.settings.AppSettingsManager
import uk.co.bbc.perceptivepodcasts.useractivity.UserActivityManager

class App : Application() {

    private val appIOScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainScope = MainScope()

    val playerLiveData: MutableLiveData<PlaybackManager.PlayerState>
        get() = playbackManager.mldPlayerState

    val playbackController: PlaybackController
        get() = playbackManager

    lateinit var appSettingsManager: AppSettingsManager

    val channelManager: ChannelRepo by lazy {
        ChannelRepo(initDownloadManager(), this, appIOScope, mainScope)
    }

    private val playbackManager: PlaybackManager by lazy { PlaybackManager(this, mainScope) }

    val userActivityManager by lazy {
        val storage = getSharedPreferences("USER_ACTIVITY", Context.MODE_PRIVATE)
        UserActivityManager(this, storage)
    }

    override fun onCreate() {
        super.onCreate()
        appSettingsManager = AppSettingsManager(this)
    }

    private fun initDownloadManager(): PodcastDownloadManager {
        val podcastDownloadManager = PodcastDownloadManager(this, mainScope, appIOScope) {
            appSettingsManager.settings.rssFeedUrl
        }
        podcastDownloadManager.registerReceivers()
        return podcastDownloadManager
    }
}

fun Context.getApp(): App {
    return applicationContext as App
}