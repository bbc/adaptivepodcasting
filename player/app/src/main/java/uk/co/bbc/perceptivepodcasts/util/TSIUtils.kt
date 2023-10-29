package uk.co.bbc.perceptivepodcasts.util

import android.content.Context
import uk.co.bbc.perceptivepodcasts.R
import uk.co.bbc.perceptivepodcasts.getApp

class TSIUtils {

    private fun tsiStringToInt03(context: Context, tsiAttribString: String?): Int {
        val tsiAllLevels by lazy {
            context.resources.getStringArray(R.array.TSIAcceptedValues)
        }
        assert(tsiAllLevels.size == 4)
        if (tsiAttribString.isNullOrEmpty()) {
            return -1
        }
        return tsiAllLevels.indexOfFirst { it == tsiAttribString.lowercase() }
    }

    fun shouldItemBeIncludedTSI(context: Context, tsiAttribString: String?): Boolean {
        val tsiAllLevels by lazy {
            context.resources.getStringArray(R.array.TSIAcceptedValues)
        }
        assert(tsiAllLevels.size == 4)

        val attribIndex = tsiStringToInt03(context, tsiAttribString)
        if (attribIndex !in 0..3) {
            return true
        }

        val userTsiIndex = context.getApp().appSettingsManager.getUserTSIindex()

        return when (userTsiIndex) {
            1 -> attribIndex != 3
            2 -> !(attribIndex == 2 || attribIndex == 3)
            else -> true
        }
    }

}