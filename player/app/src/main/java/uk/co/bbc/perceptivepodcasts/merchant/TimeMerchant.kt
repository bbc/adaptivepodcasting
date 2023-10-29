package uk.co.bbc.perceptivepodcasts.merchant

import java.time.ZonedDateTime

class TimeMerchant : DataMerchant {

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(dataFunc(param)) }
    }

    private fun dataFunc(param: String?): String {
        return param.let {
            val now = ZonedDateTime.now()
            when (it) {
                "ofDay", "of-day" -> now.hour.toPhaseString()
                "hours" -> now.hour.toString()
                "minutes" -> now.minute.toZeroPaddedString()
                else -> "${now.hour} ${now.minute.toZeroPaddedString()}"
            }
        }
    }
}

private fun Int.toZeroPaddedString(): String {
    return toString().padStart(2, '0')
}

private fun Int.toPhaseString(): String {
    return when (this) {
        in 6..11 -> "morning"
        in 12..18 -> "afternoon"
        in 19..22 -> "evening"
        else -> "night"
    }
}