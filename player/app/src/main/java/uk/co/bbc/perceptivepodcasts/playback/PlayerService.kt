package uk.co.bbc.perceptivepodcasts.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import uk.co.bbc.perceptivepodcasts.podcast.MediaItem
import uk.co.bbc.perceptivepodcasts.R
import uk.co.bbc.perceptivepodcasts.getApp
import uk.co.bbc.perceptivepodcasts.playback.PlaybackManager.PlayerState
import uk.co.bbc.perceptivepodcasts.podcast.MEDIA_ITEM_FRAGMENT_KEY
import uk.co.bbc.perceptivepodcasts.podcast.MediaDetailActivity
import uk.co.bbc.perceptivepodcasts.util.compatGetSerializableExtra

private const val ONGOING_NOTIFICATION_ID = 1
private const val CHANNEL_ID = "uk.co.bbc.perceptivepodcasts.playernotification"
private const val MEDIA_ITEM_KEY = "mediaItem"

fun startPlayerServiceInForeground(context: Context, preparedMediaItem: MediaItem) {
    val intent = Intent(context, PlayerService::class.java)
    intent.putExtra(MEDIA_ITEM_KEY, preparedMediaItem)
    context.startForegroundService(intent)
}

// This Service exists to show a foreground notification
// while playback is happening.
class PlayerService : LifecycleService() {

    private val tag =  PlayerService::class.simpleName

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mediaItem: MediaItem? = intent?.compatGetSerializableExtra(MEDIA_ITEM_KEY)
        if (mediaItem != null) {
            createNotificationChannel()
            val notification = createNotification(mediaItem)
            startForeground(ONGOING_NOTIFICATION_ID, notification)
            getApp().playerLiveData.observe(this, this::onPlayerState)
        } else {
            Log.w(tag, "PlayerService started without media item!")
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(mediaItem: MediaItem): Notification {

        val showDetailIntent = Intent(this, MediaDetailActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(MEDIA_ITEM_FRAGMENT_KEY, mediaItem)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            showDetailIntent,
            pendingIntentFlags
        )

        return Notification.Builder(this, CHANNEL_ID).run {
            setContentTitle(mediaItem.name)
            setContentText(getString(R.string.playback_notification_content))
            setSmallIcon(android.R.drawable.ic_media_play)
            setContentIntent(pendingIntent)
            build()
        }
    }

    private fun createNotificationChannel() {
        val name: CharSequence = getString(R.string.playback_notification_channel_name)
        val desc = getString(R.string.playback_notification_channel_desc)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = desc
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun onPlayerState(playerState: PlayerState) {
        if (playerState.playState !== PlaybackManager.PlayState.PLAYING) {
            stopSelf()
        } else {
            playerState.mediaItem?.let { mediaItem ->
                val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notification = createNotification(mediaItem)
                mNotificationManager.notify(ONGOING_NOTIFICATION_ID, notification)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}

