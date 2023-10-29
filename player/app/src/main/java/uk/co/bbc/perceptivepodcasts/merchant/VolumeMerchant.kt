package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.media.AudioManager

class VolumeMerchant(context: Context) : DataMerchant {

    private val audio: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(this::volumeDataFunc.invoke(param)) }
    }

    private fun volumeDataFunc(param: String?): String {
        val audioManager = audio
        val volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val percentage = volume.toFloat() / max.toFloat() * 100
        return if (param == "coarse" || param == "level") {
            when {
                percentage < 40 -> "low"
                percentage < 75 -> "medium"
                else -> "high"
            }
        } else { //no param
            percentage.toInt().toString() + "%"
        }
    }
}