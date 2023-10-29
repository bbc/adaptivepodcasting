package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import uk.co.bbc.perceptivepodcasts.R
import kotlin.math.pow

object NarrativeImportance {
    private const val NAllowedStrings = 4
    private const val initialSliderValue = 0.0F
    private const val minSliderFloat = 0.0F
    private const val maxSliderFloat = 1.0F
    private const val dbMinus3 : Float = 0.5F
    const val defaultMult : Float = dbMinus3
    const val defaultItemNIIndex03 : Int = 1

    var isNIUsedInThisSMIL : Boolean = false
    var currentSliderValue01F : Float = initialSliderValue

    fun volMultFromNI01(ni01FromSlider: Float, itemNIIndex03: Int): Float {
        assert(ni01FromSlider in minSliderFloat..maxSliderFloat)
        assert(itemNIIndex03 in -1..NAllowedStrings)
        return when (itemNIIndex03) {
            -1 -> defaultMult
            0 -> defaultMult * (3.0f * ni01FromSlider).dbToFrac() // 3dB boost from default
            1 -> defaultMult // default -3dB
            2 -> defaultMult * (-12.0f * ni01FromSlider).dbToFrac() // 12dB cut from default
            3 -> defaultMult * (-48.0f * ni01FromSlider).dbToFrac() // 48dB cut from default
            else -> defaultMult
        }
    }

    fun niStringToInt03(context: Context, niAttribString: String?): Int {
        val niAllLevels by lazy { context.resources.getStringArray(R.array.NIAcceptedValues) }
        assert(niAllLevels.size == NAllowedStrings)
        return if (niAttribString.isNullOrEmpty()) {
            -1
        } else {
            val attr = niAttribString.lowercase()
            niAllLevels.indexOfFirst { it == attr }
        }
    }

    private fun Float.dbToFrac(): Float {
        return 10.0.pow(toDouble() / 10.0).toFloat()
    }
}