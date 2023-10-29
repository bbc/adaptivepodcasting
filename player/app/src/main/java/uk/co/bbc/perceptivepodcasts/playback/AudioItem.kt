package uk.co.bbc.perceptivepodcasts.playback

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.Log
import uk.co.bbc.perceptivepodcasts.playback.PPParams.UserSpeedOverride
import uk.co.bbc.perceptivepodcasts.util.Duration

// Playable item specifically for audio responses
class AudioItem(
    private var inTime: Duration,
    private var speed: Float,
    private val loop: Boolean,
    private val clipBegin: Duration,
    private val clipEnd: Duration,
    private var duration: Duration,
    private val volLeft: Float,
    private val volRight: Float,
    private var audioUri: String,
    private val uId: String,
    private val ni03: Int = NarrativeImportance.defaultItemNIIndex03
) : Playable() {

    private var prevState: State = State.NULL
    private val tag = AudioItem::class.java.name
    private val mPlayer = MediaPlayer().apply {
        val niMult: Float = NarrativeImportance.volMultFromNI01(
            NarrativeImportance.currentSliderValue01F,
            ni03
        )
        setVolume(volLeft * niMult, volRight * niMult)
    }

    var state: State = State.NULL
    private var endTime: Duration? = null

    enum class State {
        NULL, INITIALISING, WAITING_FOR_IN_TIME, PLAYING, PAUSED, ERROR, COMPLETED
    }

    override fun getInTime(): Duration {
        return inTime
    }

    override fun getActiveState(): ActiveState {
        return when (state) {
            State.PLAYING, State.PAUSED -> {
                ActiveState.ACTIVE
            }

            State.NULL, State.INITIALISING -> {
                ActiveState.NOT_STARTED
            }

            State.WAITING_FOR_IN_TIME -> {
                ActiveState.WAITING_FOR_IN_TIME
            }

            else -> ActiveState.COMPLETE
        }
    }

    override fun getId(): String {
        return uId
    }

    override suspend fun postPrepare(params: PPParams): PPResult {
        return when (params) {
            is UserSpeedOverride -> {
                val mult = params.speedMultiplier
                speed *= mult
                inTime = inTime.times(1.0F / mult)
                duration = duration.times(1.0F / mult)

                val statedDurL: Long = duration.asMillis()
                val mediaDurL: Long
                val clipBeginL: Long = clipBegin.asMillis()
                var clipEndL: Long = clipEnd.asMillis()
                var clipDurL: Long
                val inTimeL: Long = inTime.asMillis()

                val mmr = MediaMetadataRetriever()
                val durationStr: String?
                try {
                    mmr.setDataSource(this.audioUri)
                    durationStr =
                        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    mediaDurL = durationStr?.toLong() ?: 0L

                    if (mediaDurL in 1 until clipEndL) {
                        clipEndL = mediaDurL
                    }
                    if (clipEndL == 0L) {
                        clipEndL = mediaDurL
                    }
                    clipDurL = if (clipEndL > 0L && clipBeginL <= clipEndL) {
                        clipEndL - clipBeginL
                    } else {
                        clipEndL
                    }
                    if (statedDurL in 1 until clipDurL) {
                        clipDurL = statedDurL
                    }
                } catch (ex: Exception) {
                    clipDurL = clipEndL - clipBeginL
                }
                if (loop) {
                    if (statedDurL > 0) {
                        clipDurL = statedDurL
                    }
                }
                durationMs = clipDurL + inTimeL
                PPResult(durationMs)
            }

            is PPParams.NarrativeImportance -> {
                val sliderVal = params.sliderOverride
                val debThisAudioItem: AudioItem = this
                debThisAudioItem.let {
                    val mult = NarrativeImportance.volMultFromNI01(
                        sliderVal,
                        it.ni03
                    )
                    val newVolLeft = it.volLeft * mult
                    val newVolRight = it.volRight * mult
                    it.mPlayer.setVolume(newVolLeft, newVolRight)
                }
                PPResult()
            }

            else -> PPResult()
        }
    }

    override fun playUpdate(playTime: Duration, epoch: Long): ActiveState {
        if (getActiveState() === ActiveState.NOT_STARTED) {
            myStartEpoch = epoch
            setActiveState(ActiveState.WAITING_FOR_IN_TIME)
        }
        when (state) {
            State.NULL -> {
                initialise()
            }

            State.INITIALISING -> {
            }

            State.WAITING_FOR_IN_TIME -> {
                if (playTime.cmp(inTime) >= 0) {
                    setActiveState(ActiveState.ACTIVE)
                    phtStartX1 = epoch
                    endTime = playTime + duration
                    mPlayer.start()
                    state = State.PLAYING
                }
            }

            State.PLAYING -> {
                setActiveState(updatePlay(playTime))
            }

            State.PAUSED -> {
                // calling play update resumes
                // a paused audio item
                mPlayer.start()
                state = State.PLAYING
            }

            State.COMPLETED, State.ERROR -> {
                if (prevState != state) {
                    phtEndX1 = epoch
                    prevState = state
                }
                setActiveState(ActiveState.COMPLETE)
            }
        }
        return getActiveState()
    }

    private fun updatePlay(playTime: Duration): ActiveState {
        return if (checkClipEnd() || checkSmilDuration(playTime)) {
            mPlayer.stop()
            state = State.COMPLETED
            ActiveState.COMPLETE
        } else {
            ActiveState.ACTIVE
        }
    }

    private fun checkSmilDuration(playTime: Duration): Boolean {
        if (!duration.isZero) {
            if (playTime.cmp(endTime!!) >= 0) {
                return true
            }
        }
        return false
    }

    private fun checkClipEnd(): Boolean {
        return !clipEnd.isZero && mPlayer.currentPosition > clipEnd.asMillis()
    }

    override suspend fun prepare(): Boolean {
        return true
    }

    private fun initialise() {
        mPlayer.setOnCompletionListener {
            state = State.COMPLETED
        }
        mPlayer.setOnPreparedListener { mp: MediaPlayer ->
            setPlaySpeed(speed)
            mPlayer.isLooping = loop
            mPlayer.pause()
            val seekTime = getInitSeekTime(mp)
            mp.seekTo(seekTime.asMillis().toInt())
        }
        mPlayer.setOnSeekCompleteListener {
            if (state != State.ERROR && state != State.COMPLETED) {
                state = State.WAITING_FOR_IN_TIME
            }
        }
        state = State.INITIALISING
        try {
            mPlayer.setDataSource(audioUri)
            mPlayer.prepare()
        } catch (e: Exception) {
            state = State.ERROR
            Log.e("AudioItemInitialise", e.message ?: ("Bad file: $audioUri"))
        }
    }

    private fun getInitSeekTime(mp: MediaPlayer): Duration {
        return if (clipBegin.asMillis() < mp.duration) {
            clipBegin
        } else Duration.ZERO
    }

    override fun pause() {
        if (state == State.PLAYING) {
            mPlayer.pause()
            state = State.PAUSED
        }
    }

    private fun setPlaySpeed(speed: Float) {
        try {
            mPlayer.playbackParams = mPlayer.playbackParams.setSpeed(speed)
        } catch (e: Exception) {
            Log.e(tag, "setPlaySpeed: ", e)
        }
    }

    override fun release() {
        rewind()
        mPlayer.release()
    }

    override fun rewind() {
        if (state == State.PLAYING) {
            mPlayer.stop()
        }
        mPlayer.reset()
        state = State.NULL
        myStartEpoch = 0L
        setActiveState(ActiveState.NOT_STARTED)
    }

    override fun deltaSeekActiveHelper(deltasMsPM: Long, epoch: Long, mode: SeekMode): Long {
        var curPos = 0L
        var newPos = 0L
        if (state == State.PLAYING || state == State.PAUSED) {
            val statedDuration: Long = duration.asMillis()
            prevState = State.NULL // force redisplay epoch
            if (!loop) {
                curPos = mPlayer.currentPosition.toLong()
                val dur: Long = mPlayer.duration.toLong()

                var maxPos = dur
                val minPos = clipBegin.asMillis()
                if (!clipEnd.isZero) {
                    maxPos = kotlin.math.min(dur, clipEnd.asMillis())
                }
                if (statedDuration > 0 && (maxPos > (minPos + statedDuration))) {
                    maxPos = minPos + statedDuration
                }
                newPos = (curPos + deltasMsPM)
                if (newPos < minPos) {
                    newPos = minPos
                }
                if (newPos > maxPos) {
                    newPos = maxPos
                }
                if (mode === SeekMode.PERFORM) {
                    mPlayer.setOnSeekCompleteListener(null)
                    mPlayer.seekTo(newPos.toInt())
                }
            } else {
                // looping - max seek is calculated from curPos wrt inTime and duration
                assert(statedDuration > 0)
                if (myStartEpoch != null) {
                    val relEpoch = epoch - myStartEpoch!!
                    val inTimeL: Long = inTime.asMillis()
                    var amtDone = relEpoch - inTimeL
                    if (amtDone < 0) amtDone = 0
                    var amtRemaining = statedDuration - amtDone
                    if (amtRemaining < 0L) amtRemaining = 0L
                    assert(amtDone >= 0)
                    assert(amtRemaining <= statedDuration)
                    return if (deltasMsPM >= 0L) {
                        deltasMsPM.coerceAtMost(amtRemaining)
                    } else {
                        deltasMsPM.coerceAtLeast(-amtDone)
                    }
                }
            }
        }
        return newPos - curPos
    }

    override fun isLooping(): Boolean {
        return loop
    }


}