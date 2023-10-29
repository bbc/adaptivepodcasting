package uk.co.bbc.perceptivepodcasts.util

import android.os.Debug
import android.os.SystemClock
import kotlin.jvm.internal.Intrinsics.Kotlin

class Duration(private val milliseconds: Long) {
    fun asMillis(): Long {
        return milliseconds
    }

    operator fun minus(duration: Duration): Duration {
        return Duration(milliseconds - duration.asMillis())
    }

    operator fun plus(delta: Duration): Duration {
        return Duration(milliseconds + delta.milliseconds)
    }

    operator fun times(scale: Float): Duration {
        return Duration( (milliseconds.toFloat() * scale).toLong() )
    }


    fun cmp(duration: Duration): Long {
        return milliseconds - duration.milliseconds
    }

    fun isNegative() : Boolean {
        return milliseconds < 0L
    }

    val isZero: Boolean
        get() = milliseconds == 0L

    companion object {
        @JvmField
        val ZERO = Duration(0)

        @JvmStatic
        fun fromSecondsString(secondsString: String): Duration {
            val millis = secondsString.toDouble() * 1e3
            return Duration(millis.toLong())
        }

        @JvmStatic
        fun uptime(): Duration {
            return Duration(SystemClock.uptimeMillis())
        }


        @JvmStatic
        fun fromMillis(duration: Long): Duration {
            return Duration(duration)
        }
    }
}