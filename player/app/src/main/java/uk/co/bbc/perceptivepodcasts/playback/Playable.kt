package uk.co.bbc.perceptivepodcasts.playback

import android.util.Log
import uk.co.bbc.perceptivepodcasts.util.Duration

abstract class Playable {

    enum class ActiveState {
        NOT_STARTED, WAITING_FOR_IN_TIME, ACTIVE, COMPLETE
    }

    abstract fun getId(): String
    abstract fun playUpdate(playTime: Duration, epoch : Long): ActiveState
    abstract suspend fun prepare(): Boolean
    abstract suspend fun postPrepare( params : PPParams ) : PPResult

    var durationMs: Long = 0L
    var phtStartX1: Long = 0
    var phtEndX1: Long = 0

    protected var myStartEpoch : Long? = null

    open fun dump() {
        Log.d(
            "ActiveStateDump",
            "$_myActiveState ${getId()}"
        )
    }

    private var _myActiveState : ActiveState = ActiveState.NOT_STARTED

    open fun setActiveState(activeState: ActiveState) {
        _myActiveState = activeState
    }

    open fun getActiveState(): ActiveState {
        return _myActiveState
    }


    abstract fun pause()
    abstract fun rewind()
    abstract fun release()

    open fun isContainer() : Boolean {
        return false
    }

    open fun isLooping() : Boolean {
        return false
    }

    protected abstract fun getInTime() : Duration

    enum class SeekMode {
        QUERY,PERFORM
    }

    abstract fun deltaSeekActiveHelper(deltasMsPM : Long, epoch : Long, mode : SeekMode ) : Long

    fun deltaSeekRelevantPIs(deltasMsPM : Long, epoch : Long, mode : SeekMode  ) : Long {
        if (getActiveState() == ActiveState.COMPLETE) {
            return 0L
        } else if (getActiveState() == ActiveState.NOT_STARTED) {
            return 0L
        } else if (getActiveState() == ActiveState.WAITING_FOR_IN_TIME) {
            if (myStartEpoch != null) {
                val relEpoch = epoch - myStartEpoch!!
                if (deltasMsPM >= 0L) {
                    val amtLeft : Long = getInTime().asMillis() - relEpoch
                    if (amtLeft < 0L) {
                        return 0L
                    }
                    return deltasMsPM.coerceAtMost(amtLeft)
                } else {
                    return deltasMsPM.coerceAtLeast(-relEpoch)
                }
            } else {
                Log.e("PPP", "Waiting but no start epoch: ")
            }
        } else {
            return deltaSeekActiveHelper( deltasMsPM, epoch, mode )
        }
        return 0L
    }

}