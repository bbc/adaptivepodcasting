package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import uk.co.bbc.perceptivepodcasts.getApp

class UserSpeedOverrideHelper (val context : Context) {
    private val eps = 0.05f

    fun getMult() : Float {
        var mult = context.getApp().appSettingsManager.settings.userSpeedOv
        if (mult !in 1.0f-eps .. 1.0f+eps) {
            if( mult < minspeed ) {
                mult = minspeed
            }
            if( mult > maxspeed ) {
                mult = maxspeed
            }
        } else {
            mult = 1.0f
        }
        return mult
    }

    companion object {
        const val minspeed = 0.5f
        const val maxspeed = 2.0f
    }
}