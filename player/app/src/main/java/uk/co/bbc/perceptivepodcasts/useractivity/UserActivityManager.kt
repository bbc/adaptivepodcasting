package uk.co.bbc.perceptivepodcasts.useractivity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER
import com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT
import com.google.android.gms.location.DetectedActivity.*
import uk.co.bbc.perceptivepodcasts.getApp

private const val TAG = "UerActivity"

private const val REQUEST_CODE_INTENT_ACTIVITY_TRANSITION = 1
private const val REQUEST_CODE_INTENT_ACTIVITY = 2
private const val LAST_ACTIVITY_KEY = "lastActivity"

class ActivityTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            val enteringActivity = result?.transitionEvents?.find {
                it.transitionType == ACTIVITY_TRANSITION_ENTER
            }
            enteringActivity?.let {
                context.getApp().userActivityManager.onActivityEntered(it.activityType)
            }
        }
        if(ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val activity = result?.mostProbableActivity
            activity?.let {
                context.getApp().userActivityManager.onActivityEntered(it.type)
            }
        }
    }
}

class UserActivityManager(private val context: Context,
                          private val storage: SharedPreferences) {

    var lastActivityType: Int? = readLast()

    private val client  = ActivityRecognition.getClient(context)
    private val appSettingsManager = context.getApp().appSettingsManager

    private var permissionsConfirmed = false
    private var enabled = false

    init {
        appSettingsManager.liveData.observeForever {
            enabled = it.userActivityMonitoringEnabled
            if(enabled && permissionsConfirmed) {
                start()
            } else {
                stop()
            }
        }
    }

    fun onPermissionConfirmed() {
        permissionsConfirmed = true
        if(enabled) {
            start()
        }
    }

    private fun start() {
        requestForUpdates()
    }

    private fun stop() {
        deregisterForUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun requestForUpdates() {
        Log.d(TAG, "Starting UserActivity monitoring")
        val request = ActivityTransitionRequest(getTransitions())

        val transitionPendingIntent = getPendingIntent(REQUEST_CODE_INTENT_ACTIVITY_TRANSITION)
        client.requestActivityTransitionUpdates(request, transitionPendingIntent)
            .addOnFailureListener { e -> e.printStackTrace() }

        val activityPendingIntent = getPendingIntent(REQUEST_CODE_INTENT_ACTIVITY)
        client.requestActivityUpdates(500, activityPendingIntent)
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    @SuppressLint("MissingPermission")
    private fun deregisterForUpdates() {
        Log.d(TAG, "Stopping UserActivity monitoring")
        val transitionPendingIntent = getPendingIntent(REQUEST_CODE_INTENT_ACTIVITY_TRANSITION)
        client.removeActivityTransitionUpdates(transitionPendingIntent)
            .addOnFailureListener { e -> e.printStackTrace() }
        val activityPendingIntent = getPendingIntent(REQUEST_CODE_INTENT_ACTIVITY)
        client.removeActivityTransitionUpdates(activityPendingIntent)
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    private fun getPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(context, ActivityTransitionReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // See: https://stackoverflow.com/questions/69862244/android-activityrecognition-and-immutable-pendingintent
            FLAG_UPDATE_CURRENT or FLAG_MUTABLE
        } else {
            FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }

    fun onActivityEntered(activityType: Int) {
        Log.d(TAG, "Activity started: ${activityType.toActivityString()}")
        lastActivityType = activityType
        storage.edit().putInt(LAST_ACTIVITY_KEY, activityType).apply()
    }

    private fun readLast(): Int? {
        return if(storage.contains(LAST_ACTIVITY_KEY)) {
            storage.getInt(LAST_ACTIVITY_KEY, UNKNOWN)
        } else {
            null
        }
    }
}

private fun getTransitions(): List<ActivityTransition> {
    return listOf(
        activityTransition(IN_VEHICLE, ACTIVITY_TRANSITION_ENTER),
        activityTransition(IN_VEHICLE, ACTIVITY_TRANSITION_EXIT),
        activityTransition(WALKING, ACTIVITY_TRANSITION_ENTER),
        activityTransition(WALKING, ACTIVITY_TRANSITION_EXIT),
        activityTransition(STILL, ACTIVITY_TRANSITION_ENTER),
        activityTransition(STILL, ACTIVITY_TRANSITION_EXIT),
        activityTransition(RUNNING, ACTIVITY_TRANSITION_ENTER),
        activityTransition(RUNNING, ACTIVITY_TRANSITION_EXIT)
    )
}

private fun activityTransition(type: Int, transition: Int) = ActivityTransition.Builder()
    .setActivityType(type)
    .setActivityTransition(transition)
    .build()
