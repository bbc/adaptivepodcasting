package uk.co.bbc.perceptivepodcasts.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

// Support functions for handling the weird deprecation-without-alternative situation
// surrounding API 33 and getSerializable and getSerializableExtra
// See: https://android-review.googlesource.com/c/platform/frameworks/support/+/2214807
inline fun <reified T: Serializable> Intent.compatGetSerializableExtra(key: String): T? {
    return if (Build.VERSION.SDK_INT >= 33) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        when(val extra = getSerializableExtra(key)) {
            is T? -> extra
            else -> null
        }
    }
}

inline fun <reified T: Serializable> Bundle.compatGetSerializable(key: String): T? {
    return if(Build.VERSION.SDK_INT >= 33) {
        getSerializable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        when(val item = getSerializable(key)) {
            is T? -> item
            else -> null
        }
    }
}