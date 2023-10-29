package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager

enum class OnFocusChangeAction {
    NO_ACTION, PAUSE, PLAY
}

class AudioFocusHelper(context: Context, private val focusRequest: AudioFocusRequest) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var resumeOnFocusGain: Boolean = false

    fun abandonAudioFocus() {
        audioManager.abandonAudioFocusRequest(focusRequest)
        resumeOnFocusGain = false
    }

    fun requestFocus(): Boolean {
        resumeOnFocusGain = false
        return when (audioManager.requestAudioFocus(focusRequest)) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                true
            }
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                resumeOnFocusGain = true
                false
            }
            else -> false
        }
    }

    fun resolveFocusChanged(isPlaying: Boolean, focusChange: Int): OnFocusChangeAction {
        return when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN ->
                if (resumeOnFocusGain) {
                    resumeOnFocusGain = false
                    OnFocusChangeAction.PLAY
                } else {
                    OnFocusChangeAction.NO_ACTION
                }
            AudioManager.AUDIOFOCUS_LOSS -> {
                resumeOnFocusGain = false
                OnFocusChangeAction.PAUSE
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                resumeOnFocusGain = isPlaying
                OnFocusChangeAction.PAUSE
            }
            else -> OnFocusChangeAction.NO_ACTION
        }
    }

}

fun buildFocusRequest(onFocusChanged: (Int) -> Unit): AudioFocusRequest {
    val afChangeListener = AudioManager.OnAudioFocusChangeListener { onFocusChanged(it) }
    return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
        setAudioAttributes(AudioAttributes.Builder().run {
            setUsage(AudioAttributes.USAGE_MEDIA)
            setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            build()
        })
        setAcceptsDelayedFocusGain(true)
        setOnAudioFocusChangeListener(afChangeListener)
        build()
    }
}
