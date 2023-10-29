package uk.co.bbc.perceptivepodcasts.playback

import android.util.Log
import uk.co.bbc.perceptivepodcasts.util.Duration

// Collection of playable items which will be played sequentially
class Sequence(
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
        parentType = ParentType.Sequential
    )

    private var playIndex = 0
    private var childStartTime = Duration.ZERO

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
        val ppResult = playableParentHelper.postPrepare(params)
        if (params is PPParams.UserSpeedOverride) {
            durationMs = ppResult.durationMs
        }
        return ppResult
    }

    override fun playUpdate(playTime: Duration, epoch: Long): ActiveState {
        var myActiveState = getActiveState()
        if (myActiveState === ActiveState.NOT_STARTED) {
            myStartEpoch = epoch
            myActiveState = ActiveState.WAITING_FOR_IN_TIME
        }
        if (myActiveState === ActiveState.WAITING_FOR_IN_TIME && (playTime.cmp(getInTime()) >= 0)) {
            myActiveState = ActiveState.ACTIVE
            Log.d("SeqS", description)
        }
        val cpState = playableParentHelper.playUpdate(playTime, epoch)
        if (cpState === ActiveState.COMPLETE) {
            myActiveState = ActiveState.COMPLETE
        }
        setActiveState(myActiveState) // smelly
        return cpState
    }

    override fun dump() {
        playableParentHelper.dump()
    }

    private fun childPlayUpdate(playTime: Duration, epoch: Long): ActiveState {
        val childPlayTime = playTime.minus(childStartTime)
        var cpActiveState: ActiveState
        if (playIndex < playableParentHelper.playableItems.size) {
            cpActiveState =
                playableParentHelper.playableItems[playIndex].playUpdate(childPlayTime, epoch)
            if (cpActiveState === ActiveState.COMPLETE) {
                playIndex++
                childStartTime = playTime
                cpActiveState = childPlayUpdate(playTime, epoch)
                // note: rather than Sequence keeping track of its children explicitly,
                // it uses recursive calls
            }
        } else {
            cpActiveState = ActiveState.COMPLETE
        }
        return cpActiveState
    }

    override fun rewind() {
        playableParentHelper.rewind()
        childStartTime = Duration.ZERO
        playIndex = 0
        setActiveState(ActiveState.NOT_STARTED)
        myStartEpoch = 0L
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

    override fun release() {
        playableParentHelper.release()
        setActiveState(ActiveState.NOT_STARTED)
    }

    override fun isContainer(): Boolean {
        return true
    }

    override fun deltaSeekActiveHelper(deltasMsPM: Long, epoch: Long, mode: SeekMode): Long {
        return playableParentHelper.deltaSeekRelevantPIs(deltasMsPM, epoch, mode)
    }

}