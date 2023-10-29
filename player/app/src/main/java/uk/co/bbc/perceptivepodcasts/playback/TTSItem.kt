package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import android.content.Intent
import org.xmlpull.v1.XmlPullParserException
import uk.co.bbc.perceptivepodcasts.merchant.DataMerchant
import uk.co.bbc.perceptivepodcasts.util.Duration
import java.io.IOException

// Text to Speech (TTS) playable item
class TTSItem(
    private val context: Context,
    private val inTime: Duration,
    private val clipBegin: Duration,
    private val clipEnd: Duration,
    private val duration: Duration,
    private val volLeft: Float,
    private val volRight: Float,
    private val ttsText: String,
    private val uId: String,
    private val dataMerchants: HashMap<String, DataMerchant>,
    private val ni03: Int = NarrativeImportance.defaultItemNIIndex03
) : Playable() {

    private var audioItem: AudioItem? = null
    private var interpolatedTtsText: String = ""
    private var state: AudioItem.State = AudioItem.State.NULL
    private var previousState: AudioItem.State = AudioItem.State.NULL

    override fun getInTime(): Duration {
        return inTime
    }

    override fun getActiveState(): ActiveState {
        return audioItem?.getActiveState() ?: ActiveState.COMPLETE
    }

    override suspend fun postPrepare(params: PPParams): PPResult {
        val result = audioItem?.postPrepare(params) ?: PPResult()
        if (params is PPParams.UserSpeedOverride) {
            durationMs = result.durationMs
        }
        return result
    }

    override fun playUpdate(playTime: Duration, epoch: Long): ActiveState {
        val activeState = audioItem?.playUpdate(playTime, epoch) ?: ActiveState.COMPLETE
        state = audioItem?.state ?: AudioItem.State.NULL
        if (activeState != ActiveState.COMPLETE) {
            if (state == AudioItem.State.PLAYING &&
                previousState != AudioItem.State.PLAYING &&
                previousState != AudioItem.State.PAUSED
            ) {
                val intent = Intent()
                intent.action = "uk.co.bbc.perceptivepodcasts.ttsbc"
                intent.putExtra("bisStart", true)
                intent.putExtra("TTSText", interpolatedTtsText)
                context.sendBroadcast(intent)
            }
            previousState = state
        }
        return activeState
    }

    override suspend fun prepare(): Boolean {
        val dynScriptParser = DynScriptParser()
        interpolatedTtsText = parseText(dynScriptParser) ?: return false
        if (interpolatedTtsText.isEmpty()) {
            return true
        }
        val filePath = renderTextToSpeechToFile(context, interpolatedTtsText, uId) ?: return false
        audioItem = createAudioItem(filePath)
        return audioItem?.prepare() ?: false
    }

    private fun parseText(dynScriptParser: DynScriptParser): String? {
        // replace  text properties with those outputted from data merchants, etc
        // Use <speak> tag to detect if TTSItem is an audio item.
        return if (!ttsText.contains("<speak")) {
            // Assume is a Dynscript, so process TTS as as XML.
            try {
                dynScriptParser.parse(dataMerchants, ttsText)
            } catch (e: XmlPullParserException) {
                null
            } catch (e: IOException) {
                null
            }
        } else {
            ttsText
        }
    }

    private fun createAudioItem(filePath: String): AudioItem {
        return AudioItem(
            inTime,
            1.0f,
            false,
            clipBegin,
            clipEnd,
            duration,
            volLeft,
            volRight,
            filePath,
            uId,
            ni03
        )
    }

    override fun pause() {
        audioItem?.pause()
    }

    override fun rewind() {
        audioItem?.rewind()
    }

    override fun release() {
        audioItem?.release()
    }

    override fun getId(): String {
        return uId
    }

    override fun deltaSeekActiveHelper(deltasMsPM: Long, epoch: Long, mode: SeekMode): Long {
        var actSeeked: Long? = null
        if (state == AudioItem.State.PLAYING || state == AudioItem.State.PAUSED) {
            actSeeked = audioItem?.deltaSeekRelevantPIs(deltasMsPM, epoch, mode)
        }
        return actSeeked ?: 0L
    }

}