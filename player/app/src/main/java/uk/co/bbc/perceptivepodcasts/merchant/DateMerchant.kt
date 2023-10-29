package uk.co.bbc.perceptivepodcasts.merchant

import java.time.Month
import java.time.ZonedDateTime

class DateMerchant : DataMerchant {

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread {
            val query = param.toDateMerchantQuery()
            val result = ZonedDateTime.now().query(query)
            onComplete(result)
        }
    }

}

enum class DateMerchantQuery {
    Month,
    Season,
    Date,
    Day,
    Year
}

fun String?.toDateMerchantQuery(): DateMerchantQuery {
    return when (this) {
        "month" -> DateMerchantQuery.Month
        "season" -> DateMerchantQuery.Season
        "date" -> DateMerchantQuery.Date
        "day" -> DateMerchantQuery.Day
        "year" -> DateMerchantQuery.Year
        else -> DateMerchantQuery.Date
    }
}

fun ZonedDateTime.query(query: DateMerchantQuery): String {
    return when (query) {
        DateMerchantQuery.Month -> month.name.lowercase()
        DateMerchantQuery.Season -> month.season()
        DateMerchantQuery.Date -> dayOfMonth.toString()
        DateMerchantQuery.Day -> dayOfWeek.name.lowercase()
        DateMerchantQuery.Year -> year.toString()
    }
}

private fun Month.season(): String {
    return when(this) {
        Month.DECEMBER, Month.JANUARY, Month.FEBRUARY -> "winter"
        Month.MARCH, Month.APRIL, Month.MAY -> "spring"
        Month.JUNE, Month.JULY, Month.AUGUST -> "summer"
        Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> "autumn"
    }
}
