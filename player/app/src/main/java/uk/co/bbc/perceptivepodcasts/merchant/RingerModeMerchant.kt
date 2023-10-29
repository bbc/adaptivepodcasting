package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.media.AudioManager

class RingerModeMerchant(context: Context) : DataMerchant {

    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(ringerData()) }
    }

    private fun ringerData(): String? {
        return when (audio.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "silent"
            AudioManager.RINGER_MODE_VIBRATE -> "vibrate"
            AudioManager.RINGER_MODE_NORMAL -> "normal"
            else -> null
        }
    }
}