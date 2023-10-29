package uk.co.bbc.perceptivepodcasts.useractivity

import com.google.android.gms.location.DetectedActivity

fun Int?.toActivityString() = when (this) {
    DetectedActivity.IN_VEHICLE -> "vehicle"
    DetectedActivity.ON_BICYCLE -> "cycling"
    DetectedActivity.RUNNING -> "running"
    DetectedActivity.ON_FOOT, DetectedActivity.WALKING -> "walking"
    DetectedActivity.TILTING -> "tilting"
    DetectedActivity.STILL -> "still"
    else -> "unknown"
}