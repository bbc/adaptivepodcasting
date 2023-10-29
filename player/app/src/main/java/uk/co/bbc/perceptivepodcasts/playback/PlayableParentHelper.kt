package uk.co.bbc.perceptivepodcasts.playback

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import uk.co.bbc.perceptivepodcasts.util.Duration

enum class ParentType {
    Parallel,
    Sequential
}

// Helper for playable parents
class PlayableParentHelper(
    private val inTime: Duration,
    var playableItems: List<Playable>,
    private val uId: String,
    private val childPlayUpdate: (Duration, Long) -> ActiveState,
    private val parentType: ParentType
) : Playable() {

    private val tag = PlayableParentHelper::class.simpleName

    public override fun getInTime(): Duration {
        return inTime
    }

    override suspend fun postPrepare(params: PPParams): PPResult {
        return if (playableItems.isNotEmpty()) {
            val results = postPrepareAllChildren(params)
            when (params) {
                is PPParams.UserSpeedOverride -> {
                    when (parentType) {
                        ParentType.Parallel -> {
                            val debMyMax = results.maxOf { it.durationMs }
                            PPResult(debMyMax + inTime.asMillis())
                        }

                        ParentType.Sequential -> {
                            val debMySum = results.sumOf { it.durationMs }
                            PPResult(debMySum + inTime.asMillis())
                        }
                    }
                }

                is PPParams.NarrativeImportance, PPParams.UnsetMode -> {
                    PPResult()
                }
            }
        } else PPResult()
    }

    override fun getId(): String {
        return uId
    }

    override suspend fun prepare(): Boolean {
        return if (playableItems.isEmpty()) {
            Log.w(tag, "prepare: Zero Children in Parent with ID:$uId")
            true
        } else {
            prepareAllChildren()
        }
    }

    private suspend fun prepareAllChildren(): Boolean {
        return coroutineScope {
            val prepareTasks = playableItems.map { async { it.prepare() } }
            val results = prepareTasks.awaitAll()
            results.all { it }
        }
    }

    private suspend fun postPrepareAllChildren(params: PPParams): List<PPResult> {
        return coroutineScope {
            val prepareTasks = playableItems.map { async { it.postPrepare(params) } }
            val debResults = prepareTasks.awaitAll()
            debResults
        }
    }

    override fun dump() {
        for (child in playableItems) {
            child.dump()
        }
    }

    override fun playUpdate(playTime: Duration, epoch: Long): ActiveState {
        var myActiveState = getActiveState()
        if (myActiveState === ActiveState.NOT_STARTED) {
            myStartEpoch = epoch
            myActiveState = ActiveState.WAITING_FOR_IN_TIME
        }
        if (myActiveState === ActiveState.WAITING_FOR_IN_TIME && playTime.cmp(inTime) >= 0) {
            myActiveState = ActiveState.ACTIVE
        }
        if (myActiveState === ActiveState.ACTIVE) {
            val localPlayTime = playTime.minus(inTime)
            val cpActiveState = childPlayUpdate(localPlayTime, epoch)
            if (cpActiveState === ActiveState.COMPLETE) {
                myActiveState = ActiveState.COMPLETE
            }
        }
        setActiveState(myActiveState) // smelly!!!
        return myActiveState
    }

    override fun pause() {
        playableItems.forEach { it.pause() }
    }

    override fun rewind() {
        playableItems.forEach { it.rewind() }
        setActiveState(ActiveState.NOT_STARTED)
        myStartEpoch = 0L
    }

    override fun release() {
        playableItems.forEach { it.release() }
        setActiveState(ActiveState.NOT_STARTED)
        myStartEpoch = 0L
    }

    override fun deltaSeekActiveHelper(deltasMsPM: Long, epoch: Long, mode: SeekMode): Long {
        var actSeeked = 0L
        if (mode === SeekMode.QUERY) {
            actSeeked = deltasMsPM
        }
        if (playableItems.isEmpty()) {
            return deltasMsPM
        } else {
            for (child in playableItems) {
                val cstate = child.getActiveState()
                if (cstate == ActiveState.COMPLETE) {
                    continue
                }
                when (parentType) {
                    ParentType.Sequential -> {
                        if (cstate == ActiveState.NOT_STARTED) {
                            break
                        }
                        val childActSeeked = child.deltaSeekRelevantPIs(deltasMsPM, epoch, mode)
                        actSeeked = childActSeeked
                        break
                    }
                    ParentType.Parallel -> {
                        val childActSeeked = child.deltaSeekRelevantPIs(deltasMsPM, epoch, mode)
                        if (mode === SeekMode.PERFORM) {
                            actSeeked = actSeeked.coerceAtLeast(childActSeeked)
                        } else if (mode === SeekMode.QUERY) {
                            actSeeked = actSeeked.coerceAtMost(childActSeeked)
                        }
                    }
                }
            }
        }
        return actSeeked
    }


}