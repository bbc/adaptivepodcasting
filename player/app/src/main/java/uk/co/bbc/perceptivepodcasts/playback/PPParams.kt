package uk.co.bbc.perceptivepodcasts.playback

sealed interface PPParams {
    object UnsetMode: PPParams
    data class UserSpeedOverride(val speedMultiplier: Float): PPParams
    data class NarrativeImportance(val sliderOverride: Float): PPParams
}

data class PPResult(
    val durationMs: Long = 0L
)
