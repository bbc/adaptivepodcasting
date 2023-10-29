package uk.co.bbc.perceptivepodcasts.playback

import uk.co.bbc.perceptivepodcasts.podcast.MediaItem
import uk.co.bbc.perceptivepodcasts.util.Duration

interface PlaybackController {
    fun pause()
    fun play()
    fun prepare(mItem: MediaItem)
    fun handleSkip(deltaMsPM : Duration?, onSkipCompleted : () -> Unit )
    fun release()
}