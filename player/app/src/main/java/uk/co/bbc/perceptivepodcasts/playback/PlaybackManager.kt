package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.co.bbc.perceptivepodcasts.podcast.MediaItem
import uk.co.bbc.perceptivepodcasts.podcast.mediaItemLocation
import uk.co.bbc.perceptivepodcasts.util.Duration

class PlaybackManager(
    context: Context,
    private val mainScope: CoroutineScope
) : PlaybackController {

    private val context: Context
    private var preparedMediaItem: MediaItem? = null

    enum class PlayState {
        NULL, PREPARING, PREPARED, PREPARE_FAILED, PLAYING, PAUSED, COMPLETED
    }

    enum class PercentPlayedModes {
        PPM_STARTED,
        PPM_IN_PROGRESS,
        PPM_COMPLETED,
    }

    data class PlaybackProgress(
        var mode: PercentPlayedModes,
        var pc: Double,
        var phtMillis: Long
    )

    data class PlayerState(
        val playState: PlayState = PlayState.NULL,
        val mediaItem: MediaItem? = null
    )

    val mldPlayerState = MutableLiveData(PlayerState())
    val mldPlaybackProgress =
        MutableLiveData(PlaybackProgress(PercentPlayedModes.PPM_COMPLETED, 0.0, 0))

    private var perceptivePlayer: PerceptivePlayer? = null

    init {
        this.context = context.applicationContext
    }

    fun getPlayer(): PerceptivePlayer? {
        return perceptivePlayer
    }

    private fun updateData(func: PlayerState.() -> PlayerState) {
        mldPlayerState.value = mldPlayerState.value?.func()
    }

    private fun onPrepareFailed() {
        updateData { copy(playState = PlayState.PREPARE_FAILED) }
    }

    private fun onPrepared(mediaItem: MediaItem) {
        preparedMediaItem = mediaItem
        updateData { copy(playState = PlayState.PREPARED) }
    }

    private fun onCompleted() {
        val curPHT = perceptivePlayer?.playHeadTime?.getCurr()?.asMillis() ?: 0L
        Log.d("onPodcastCompleted PHT ", "dur was $curPHT")
        perceptivePlayer?.rewind()
        updateData { copy(playState = PlayState.COMPLETED) }
    }

    override fun release() {
        perceptivePlayer?.let { player ->
            mldPlayerState.value = PlayerState(PlayState.NULL, null)
            preparedMediaItem = null
            player.release()
        }
    }

    override fun pause() {
        perceptivePlayer?.let { player ->
            updateData { copy(playState = PlayState.PAUSED) }
            player.pause()
        }
    }

    override fun play() {
        val player = perceptivePlayer
        val mediaItem = preparedMediaItem

        if (player != null && mediaItem != null) {
            updateData { copy(playState = PlayState.PLAYING) }
            player.play()
            startPlayerServiceInForeground(context, mediaItem)
        }
    }

    override fun handleSkip(deltaMsPM: Duration?, onSkipCompleted: () -> Unit) {
        val player = perceptivePlayer
        val mediaItem = preparedMediaItem
        val curPlayerState = mldPlayerState.value
        if ((player != null && mediaItem != null && curPlayerState != null) &&
            (curPlayerState.playState == PlayState.PLAYING || curPlayerState.playState == PlayState.PAUSED)
        ) {
            if (deltaMsPM == null) { // null indicates reStart
                val prevState = curPlayerState.playState
                if (prevState == PlayState.PLAYING) {
                    player.pause()
                }
                player.rewind()
                if (prevState == PlayState.PLAYING) {
                    player.play()
                }
            } else {
                if (!deltaMsPM.isZero) {
                    player.skipFwd(deltaMsPM)
                }
            }
        }
        onSkipCompleted()
    }

    override fun prepare(mItem: MediaItem) {
        if (perceptivePlayer == null) {
            perceptivePlayer = PlayerBuilder.build(context, mainScope).apply {
                setOnCompletedListener { onCompleted() }
            }
        }

        perceptivePlayer?.let { player ->
            player.release()
            mldPlayerState.value = PlayerState(PlayState.PREPARING, mItem)
            val parentDir = context.mediaItemLocation(mItem)
            val uri = "smil.xml"
            player.setDataSource(parentDir, uri)

            mainScope.launch {
                if (player.prepare()) {
                    onPrepared(mItem)
                } else {
                    onPrepareFailed()
                }
            }
        }
    }

}