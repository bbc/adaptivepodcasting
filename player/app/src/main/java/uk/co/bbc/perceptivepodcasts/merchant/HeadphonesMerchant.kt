package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.*
import android.media.AudioManager

class HeadphonesMerchant(context: Context) : DataMerchant {

    private val audio: AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(dataFunc()) }
    }

    private fun dataFunc(): String {
        val outputDevices = audio.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val anyConnectedHeadphones = outputDevices.any { it.isHeadphones() }
        return if (anyConnectedHeadphones) {
            "headphones"
        } else {
            "no headphones"
        }
    }

    private fun AudioDeviceInfo.isHeadphones(): Boolean {
        return when (type) {
            TYPE_WIRED_HEADPHONES,
            TYPE_WIRED_HEADSET,
            TYPE_HEARING_AID,
            TYPE_BLUETOOTH_A2DP,
            TYPE_USB_HEADSET,
            TYPE_BLE_HEADSET,
            TYPE_BLUETOOTH_SCO -> true

            else -> false
        }
    }
}
