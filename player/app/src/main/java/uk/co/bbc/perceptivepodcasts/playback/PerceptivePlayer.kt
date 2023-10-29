package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParserException
import uk.co.bbc.perceptivepodcasts.merchant.DataMerchant
import uk.co.bbc.perceptivepodcasts.playback.Playable.ActiveState
import uk.co.bbc.perceptivepodcasts.podcast.SmilLiteParser
import uk.co.bbc.perceptivepodcasts.util.Duration
import uk.co.bbc.perceptivepodcasts.util.Duration.Companion.ZERO
import uk.co.bbc.perceptivepodcasts.util.Duration.Companion.uptime
import uk.co.bbc.perceptivepodcasts.getApp
import uk.co.bbc.perceptivepodcasts.playback.PlaybackManager.PlaybackProgress
import uk.co.bbc.perceptivepodcasts.playback.PlaybackManager.PercentPlayedModes
import java.io.File
import java.io.FileInputStream
import java.io.IOException

data class PlayerSource(val directory: String, val uri: String)

// Helper class for managing the progression of time
// based on the wall clock time. The complexity here is
// that the player can be paused so we need to
// accumulate time passing rather than just compare
// with an initial time
class PlayTime {

    private var playTime: Duration = ZERO
    private var lastUpTime: Duration? = null

    fun pause() {
        lastUpTime = null
    }

    fun moveOn(): Duration {
        // update the current play time
        val timeNow = uptime()
        val delta = lastUpTime?.let { timeNow - it } ?: ZERO
        lastUpTime = timeNow
        playTime += delta
        return playTime
    }

    fun reset() {
        playTime = ZERO
        lastUpTime = null
    }

    fun getCurr(): Duration {
        return playTime
    }

    fun skipFwd(posDelta: Duration): Duration {
        val timeNow = uptime()
        val delta = lastUpTime?.let { timeNow - it } ?: ZERO
        lastUpTime = timeNow
        playTime += (delta + posDelta)
        return playTime
    }
}

private const val ACTION = "uk.co.bbc.perceptivepodcasts.ttsbc"

class PerceptivePlayer(private val context: Context, private val mainScope: CoroutineScope) {

    private var playerSource: PlayerSource? = null

    private val dataMerchants = HashMap<String, DataMerchant>()
    private val tag = "Perceptive Player"

    val playHeadTime = PlayTime()

    var topLevel: Playable? = null

    private var onCompletedListener: () -> Unit = {}

    private var playJob: Job? = null
    private var isPlaying = false

    private var isTmpPaused: Boolean = false
    private var signalOK: Boolean = false
    private var totDurMs: Long = 0
    private var pppCounter: Long = 0

    // Start/Resume playback
    fun play() {
        playJob?.cancel()
        playJob = mainScope.launch {
            internalPlay()
        }
    }

    private var currentProgress: PlaybackProgress =
        PlaybackProgress(PercentPlayedModes.PPM_COMPLETED, 0.0, 0L)

    private fun postPercentPlayed(mode: PercentPlayedModes, pc: Double, phtMillis: Long) {
        currentProgress.mode = mode
        currentProgress.pc = pc
        currentProgress.phtMillis = phtMillis

        when (mode) {
            PercentPlayedModes.PPM_STARTED -> {
                if (currentProgress.pc != 0.0) {
                    currentProgress.pc = 0.0
                }
                (context.getApp().playbackController as PlaybackManager)
                    .mldPlaybackProgress.postValue(currentProgress)
            }

            PercentPlayedModes.PPM_IN_PROGRESS -> {
                pppCounter++
                if (pppCounter % 20L == 0L) {
                    (context.getApp().playbackController as PlaybackManager)
                        .mldPlaybackProgress.postValue(currentProgress)
                }
            }

            PercentPlayedModes.PPM_COMPLETED -> {
                if (currentProgress.pc != 100.0) {
                    currentProgress.pc = 100.0
                }
                (context.getApp().playbackController as PlaybackManager)
                    .mldPlaybackProgress.postValue(currentProgress)
                pppCounter = 0
            }
        }
    }

    private suspend fun internalPlay() {
        isPlaying = true
        topLevel?.let { topLevel ->
            while (true) {
                if (!isTmpPaused) {
                    val newPlayHeadTime = playHeadTime.moveOn()
                    val curPercentPlayed = if (totDurMs == 0L) {
                        0.0
                    } else {
                        val phtMillis = newPlayHeadTime.asMillis()
                        100 * phtMillis.toDouble() / (totDurMs.toDouble())
                    }
                    postPercentPlayed(
                        PercentPlayedModes.PPM_IN_PROGRESS,
                        curPercentPlayed,
                        newPlayHeadTime.asMillis()
                    )
                    val activeState =
                        topLevel.playUpdate(newPlayHeadTime, newPlayHeadTime.asMillis())
                    if (activeState == ActiveState.COMPLETE) {
                        postPercentPlayed(
                            PercentPlayedModes.PPM_COMPLETED,
                            100.0,
                            newPlayHeadTime.asMillis()
                        )
                        break
                    }
                } else {
                    assert(!signalOK)
                }
                if (!signalOK) {
                    signalOK = true
                }

                delay(16)
            }
            topLevel.rewind()
        }
        playHeadTime.reset()
        onCompletedListener()
    }

    // Pause playback
    fun pause() {
        playHeadTime.pause()
        playJob?.cancel()
        topLevel?.pause()
        isPlaying = false
    }

    private fun tmpPause() {
        synchronized(this) {
            isTmpPaused = true
            signalOK = false
        }
    }

    private fun tmpUnPause() {
        isTmpPaused = false
    }

    fun setDataSource(parentDir: String, uri: String) {
        playerSource = PlayerSource(parentDir, uri)
    }

    fun setOnCompletedListener(onCompletedListener: () -> Unit) {
        this.onCompletedListener = onCompletedListener
    }

    fun addDataMerchant(merchantId: String, dataMerchant: DataMerchant) {
        dataMerchants[merchantId] = dataMerchant
        Log.d(tag, "Data Merchant, $merchantId, added")
    }

    // Initialise player before playback can begin
    suspend fun prepare(): Boolean {
        playHeadTime.reset()

        val source = playerSource ?: return false

        topLevel = createPlayable(source) // SMIL parsing
        val prepResult = topLevel?.prepare() ?: false

        if (!prepResult) return false

        // insert for user speed override
        val userSpeedOverrideMultiplier = UserSpeedOverrideHelper(context).getMult()
        val ppParams = PPParams.UserSpeedOverride(userSpeedOverrideMultiplier)
        val ppResult = topLevel?.postPrepare(ppParams)
        totDurMs = ppResult?.durationMs ?: 0L
        postPercentPlayed(PercentPlayedModes.PPM_STARTED, 0.0, playHeadTime.getCurr().asMillis())

        return true
    }

    private fun createPlayable(source: PlayerSource): Playable? {
        return try {
            val path = "${source.directory}/${source.uri}"
            val parser = SmilLiteParser(context, dataMerchants, source.directory)
            val smilXmlUri = FileInputStream(File(path))
            parser.parse(smilXmlUri)
        } catch (ex: IOException) {
            val intent = Intent()
            intent.action = ACTION
            intent.putExtra("bisStart", true)
            intent.putExtra("TTSText", "IOException")
            context.sendBroadcast(intent)
            null
        } catch (ex: XmlPullParserException) {
            val message = ex.message
            val line = ex.lineNumber
            val column = ex.columnNumber
            val intent = Intent()
            intent.action = ACTION
            intent.putExtra("bisStart", true)
            intent.putExtra("TTSText", "Line:$line, Column:$column: $message")
            context.sendBroadcast(intent)
            null
        }
    }


    // Release any resources which are being consumed
    fun release() {
        playJob?.cancel()
        topLevel?.release()
        topLevel = null
        isPlaying = false
    }

    // Resets all media in player (puts everything back to the start)
    fun rewind() {
        pause()
        playHeadTime.reset()
        topLevel?.rewind()
    }

    fun skipFwd(deltasMsPM: Duration) {
        if (topLevel == null || deltasMsPM.isZero) {
            return
        }
        val playHeadTimeMs = playHeadTime.getCurr().asMillis()
        var safeDelta: Duration = deltasMsPM
        if (!deltasMsPM.isNegative()) {
            val durationMs: Long = topLevel!!.durationMs
            if ((playHeadTimeMs + safeDelta.asMillis()) > durationMs) {
                val maxPosSkip = (durationMs - playHeadTimeMs).coerceAtLeast(0L)
                if (maxPosSkip == 0L) {
                    return
                }
                safeDelta = Duration.fromMillis(maxPosSkip)
            }
        }

        val curPlayStateWasPlaying = isPlaying
        if (isPlaying) {
            tmpPause()
            playHeadTime.pause()
            topLevel?.pause()
            var cnt = 0
            while (!signalOK) {
                if (++cnt > 20) {
                    signalOK = false
                    break
                }
            }
        }
        val maxPossSeek: Long? = topLevel?.deltaSeekRelevantPIs(
            safeDelta.asMillis(),
            playHeadTimeMs,
            Playable.SeekMode.QUERY
        )
        val actSeeked: Long?
        if (maxPossSeek != null && maxPossSeek != 0L) {
            actSeeked = topLevel?.deltaSeekRelevantPIs(
                maxPossSeek,
                playHeadTimeMs,
                Playable.SeekMode.PERFORM
            )
            if (actSeeked != null) {
                playHeadTime.skipFwd(Duration(actSeeked))
            }
        }
        if (curPlayStateWasPlaying) {
            tmpUnPause()
        }
    }
}