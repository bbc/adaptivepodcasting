package uk.co.bbc.perceptivepodcasts.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import uk.co.bbc.perceptivepodcasts.R

const val GREETING_DISMISSED_KEY = "isFirstRun"
const val RSS_FEED_URL_KEY = "prefRssUrl"
const val USER_ACTIVITY_MONITORING_ENABLED_KEY = "userActivityMonitoring"
const val USER_NICKNAME_STRING_KEY = "prefUserNickname"
const val USER_SPEED_OVERRIDE_KEY = "prefUserSpeedOverride"
const val USER_TSI_KEY = "prefUserTimeSqueezeImportance"
const val TSI_DEFAULT_INDEX = 0

data class AppSettings(
    val greetingDismissed: Boolean,
    val rssFeedUrl: String,
    val userActivityMonitoringEnabled: Boolean,
    val userNicknameTTS: String,
    val userSpeedOv: Float,
    val userTSIindex: Int
)

class AppSettingsManager(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    val liveData = MutableLiveData(preferences.readSettings(context.defaultSettings()))

    val settings: AppSettings get() = liveData.value!!

    val userTsiGuiStrings : Array<String> = context.getResources().getStringArray(R.array.TSIGUIDescriptions)

    fun setRssFeedUrl(url: String) {
        liveData.value = liveData.value?.copy(rssFeedUrl = url)
        preferences.applyEdit { putString(RSS_FEED_URL_KEY, url) }
    }

    fun setUserNickname(nickname: String) {
        liveData.value = liveData.value?.copy(userNicknameTTS = nickname)
        preferences.applyEdit { putString(USER_NICKNAME_STRING_KEY, nickname) }
    }

    fun getUserNickname() : String {
        val nickname = preferences.getString(USER_NICKNAME_STRING_KEY, "").toString()
        return nickname
    }

    fun setUserSpeedOv(userspeedov: Float) {
        liveData.value = liveData.value?.copy(userSpeedOv = userspeedov)
        preferences.applyEdit { putFloat(USER_SPEED_OVERRIDE_KEY, userspeedov) }
    }

    fun getUserSpeedOv() : Float {
        val userspeedov : Float = preferences.getFloat(USER_SPEED_OVERRIDE_KEY,1.0f)
        return userspeedov
    }

    fun setUserTSIindex(usertsiindex: Int) {
        liveData.value = liveData.value?.copy(userTSIindex = usertsiindex)
        preferences.applyEdit { putInt(USER_TSI_KEY, usertsiindex) }
    }

    fun getUserTSIindex() : Int {
        val usertsiindex : Int = preferences.getInt(USER_TSI_KEY,TSI_DEFAULT_INDEX)
        return usertsiindex
    }

    fun getUserTSIStrings() : Array<String> {
        return userTsiGuiStrings
    }

    fun setGreetingDismissed() {
        liveData.value = liveData.value?.copy(greetingDismissed = true)
        preferences.applyEdit { putBoolean(GREETING_DISMISSED_KEY, true) }
    }

    fun setUserActivityMonitoringEnabled(enabled: Boolean) {
        liveData.value = liveData.value?.copy(userActivityMonitoringEnabled = enabled)
        preferences.applyEdit { putBoolean(USER_ACTIVITY_MONITORING_ENABLED_KEY, enabled) }
    }

}

private fun SharedPreferences.applyEdit(function: SharedPreferences.Editor.() -> Unit) {
    edit().apply {
        function()
        apply()
    }
}

private fun Context.defaultSettings(): AppSettings {
    return AppSettings(
        greetingDismissed = false,
        rssFeedUrl = getString(R.string.default_rss_feed_url),
        userActivityMonitoringEnabled = true,
        userNicknameTTS = "",
        userSpeedOv = 1.0f,
        userTSIindex = TSI_DEFAULT_INDEX
    )
}

private fun SharedPreferences.readSettings(default: AppSettings): AppSettings {
    return AppSettings(
        greetingDismissed = getBoolean(GREETING_DISMISSED_KEY, default.greetingDismissed),
        rssFeedUrl = getString(RSS_FEED_URL_KEY, default.rssFeedUrl)!!,
        userActivityMonitoringEnabled = getBoolean(USER_ACTIVITY_MONITORING_ENABLED_KEY,
            default.userActivityMonitoringEnabled),
        userNicknameTTS = getString(USER_NICKNAME_STRING_KEY,default.userNicknameTTS)!!,
        userSpeedOv = getFloat(USER_SPEED_OVERRIDE_KEY,default.userSpeedOv),
        userTSIindex = getInt(USER_TSI_KEY, default.userTSIindex)
    )
}
