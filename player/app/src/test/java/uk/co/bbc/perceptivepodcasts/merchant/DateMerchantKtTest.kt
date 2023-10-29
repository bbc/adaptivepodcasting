package uk.co.bbc.perceptivepodcasts.merchant

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DateMerchantKtTest {

    private fun String.toZonedDateTime(): ZonedDateTime {
        val f: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d HH:mm:ss z uuuu")
        return ZonedDateTime.parse(this, f)
    }

    @Test
    internal fun testMonth() {
        val expected = "march"
        val dateTime = "Mar 14 16:02:37 GMT 2011".toZonedDateTime()
        val actual = dateTime.query(DateMerchantQuery.Month)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testSeasonSpring() {
        val expected = "spring"
        val dateTime = "Mar 14 16:02:37 GMT 2011".toZonedDateTime()
        val actual = dateTime.query(DateMerchantQuery.Season)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testSeasonSummer() {
        val expected = "summer"
        val dateTime = "Aug 14 16:02:37 GMT 2011".toZonedDateTime()
        val actual = dateTime.query(DateMerchantQuery.Season)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testDate() {
        val expected = "14"
        val dateTime = "Mar 14 16:02:37 GMT 2011".toZonedDateTime()
        val actual = dateTime.query(DateMerchantQuery.Date)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testDay() {
        val expected = "monday"
        val dateTime = "Mar 14 16:02:37 GMT 2011".toZonedDateTime()
        val actual = dateTime.query(DateMerchantQuery.Day)
        assertEquals(expected, actual)
    }

    @Test
    internal fun testYear() {
        val expected = "2011"
        val dateTime = "Mar 14 16:02:37 GMT 2011".toZonedDateTime()
        val actual = dateTime.query(DateMerchantQuery.Year)
        assertEquals(expected, actual)
    }

}
