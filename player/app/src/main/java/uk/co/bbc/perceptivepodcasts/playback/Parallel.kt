package uk.co.bbc.perceptivepodcasts.playback

import android.util.Log
import uk.co.bbc.perceptivepodcasts.util.Duration

// Collection of playable items which will be played simultaneously
class Parallel(
    inTime: Duration,
    playableItems: List<Playable>,
    uId: String,
    val description: String
) : Playable() {

    private val playableParentHelper = PlayableParentHelper(
        inTime = inTime,
        playableItems = playableItems,
        uId = uId,
        childPlayUpdate = this::childPlayUpdate,
        parentType = ParentType.Parallel
    )

    override fun getInTime(): Duration {
        return playableParentHelper.getInTime()
    }

    override fun getActiveState(): ActiveState {
        return playableParentHelper.getActiveState()
    }

    override fun setActiveState(activeState: ActiveState) {
        return playableParentHelper.setActiveState(activeState)
    }

    override suspend fun postPrepare(params: PPParams): PPResult {
        val result = playableParentHelper.postPrepare(params)
        if (params is PPParams.UserSpeedOverride) {
            durationMs = result.durationMs
        }
        return result
    }

    override fun playUpdate(playTime: Duration, epoch: Long): ActiveState {
        var myActiveState = getActiveState()
        if (myActiveState === ActiveState.NOT_STARTED) {
            myStartEpoch = epoch
            myActiveState = ActiveState.WAITING_FOR_IN_TIME
        }
        if (myActiveState === ActiveState.WAITING_FOR_IN_TIME && (playTime.cmp(getInTime()) >= 0)) {
            myActiveState = ActiveState.ACTIVE
            Log.d("ParS", description)
        }
        val cpState = playableParentHelper.playUpdate(playTime, epoch)
        if (cpState === ActiveState.COMPLETE) {
            myActiveState = ActiveState.COMPLETE
        }
        setActiveState(myActiveState)
        return cpState
    }

    override fun dump() {
        playableParentHelper.dump()
    }

    private fun childPlayUpdate(childPlayTime: Duration, epoch: Long): ActiveState {
        val cpActiveState = playableParentHelper.getActiveState()
        if (cpActiveState === ActiveState.COMPLETE) {
            return cpActiveState
        }
        var allComplete = true
        var anyActive = false
        for (item in playableParentHelper.playableItems) {
            val childActive = item.playUpdate(childPlayTime, epoch)
            if (childActive === ActiveState.ACTIVE) {
                anyActive = true
            }
            if (childActive != ActiveState.COMPLETE) {
                allComplete = false
            }
        }

        return if (allComplete) {
            ActiveState.COMPLETE
        } else if (anyActive) {
            ActiveState.ACTIVE
        } else {
            cpActiveState
        }
    }

    override fun getId(): String {
        return playableParentHelper.getId()
    }

    override suspend fun prepare(): Boolean {
        return playableParentHelper.prepare()
    }

    override fun pause() {
        playableParentHelper.pause()
    }

    override fun rewind() {
        playableParentHelper.rewind()
        setActiveState(ActiveState.NOT_STARTED)
        myStartEpoch = 0L
    }

    override fun release() {
        playableParentHelper.release()
        setActiveState(ActiveState.NOT_STARTED)
    }

    override fun isContainer(): Boolean {
        return true
    }

    override fun deltaSeekActiveHelper(deltasMsPM: Long, epoch: Long, mode: SeekMode): Long {
        return playableParentHelper.deltaSeekActiveHelper(deltasMsPM, epoch, mode)
    }


}