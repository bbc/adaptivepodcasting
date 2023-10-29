package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import android.os.Vibrator
import uk.co.bbc.perceptivepodcasts.util.Duration

@Suppress("DEPRECATION")
class VibrateItem(
    context: Context,
    private var inTime: Duration,
    private var duration: Duration,
    private val uId: String
) : Playable() {

    private var endTime: Duration = inTime.plus(duration)
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private var vibrating = false
    private var msDone = 0L

    override fun getInTime(): Duration {
        return inTime
    }

    override suspend fun postPrepare(params: PPParams): PPResult {
        msDone = 0L
        return when (params) {
            is PPParams.UserSpeedOverride -> {
                val invmult = 1.0F / params.speedMultiplier
                inTime = inTime.times(invmult)
                duration = duration.times(invmult)
                endTime = inTime.plus(duration)
                durationMs = endTime.asMillis()
                PPResult(durationMs)
            }
            is PPParams.NarrativeImportance,
            PPParams.UnsetMode -> {
                PPResult()
            }
        }
    }

    override fun playUpdate(playTime: Duration, epoch: Long): ActiveState {
        msDone = 0L
        var myActiveState = getActiveState()
        if (myActiveState === ActiveState.NOT_STARTED) {
            myStartEpoch = epoch
            myActiveState = ActiveState.WAITING_FOR_IN_TIME
        }
        if (playTime.cmp(endTime) >= 0 && vibrating) {
            vibrator.cancel()
            vibrating = false
            myActiveState = ActiveState.COMPLETE
        } else if (playTime.cmp(inTime) >= 0) {
            if (!vibrating) {
                myActiveState = ActiveState.ACTIVE
                endTime = playTime.plus(duration)
                vibrator.vibrate(duration.asMillis())
                vibrating = true
            }
            msDone = playTime.minus(inTime).asMillis()
        }
        setActiveState(myActiveState)
        return myActiveState
    }

    override suspend fun prepare(): Boolean {
        return true
    }

    override fun pause() {
        vibrator.cancel()
        vibrating = false
    }

    override fun release() {
        pause()
        setActiveState(ActiveState.NOT_STARTED)
    }

    override fun rewind() {
        pause()
        setActiveState(ActiveState.NOT_STARTED)
        myStartEpoch = 0L
    }

    override fun getId(): String {
        return uId
    }

    override fun deltaSeekActiveHelper(deltasMsPM: Long, epoch: Long, mode: SeekMode): Long {
        val amtLeft: Long = duration.asMillis() - msDone
        if (deltasMsPM >= 0L) {
            if (amtLeft <= 0L) {
                if (mode === SeekMode.PERFORM) {
                    vibrator.cancel()
                    vibrating = false
                    setActiveState(ActiveState.COMPLETE)
                }
                return 0L
            }
            return deltasMsPM.coerceAtMost(amtLeft)
        }
        return deltasMsPM.coerceAtLeast(-amtLeft)
    }
}