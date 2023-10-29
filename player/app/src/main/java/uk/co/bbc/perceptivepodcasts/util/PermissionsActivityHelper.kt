package uk.co.bbc.perceptivepodcasts.util

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import uk.co.bbc.perceptivepodcasts.R

class PermissionsActivityHelper(
    private val activity: AppCompatActivity,
    private val requestCode: Int,
    private val onPermissionsGranted: Runnable
) : DefaultLifecycleObserver {

    private enum class UiJourneyState {
        NULL, REQUESTING, DIALOGUE_SHOWING
    }

    private var uiJourneyState = UiJourneyState.NULL

    fun activate() {
        activity.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (uiJourneyState == UiJourneyState.NULL) {
            if (checkForPermissions()) {
                onPermissionsGranted.run()
            } else {
                requestPermissions(requestCode)
            }
        }
    }

    private fun checkForPermissions(): Boolean {
        for (permission in allPermissions) {
            val isGranted = ActivityCompat.checkSelfPermission(activity, permission)
            if (isGranted != PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun areAllGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions(requestCode: Int) {
        uiJourneyState = UiJourneyState.REQUESTING
        val permissionsStrArray = allPermissions.toTypedArray()
        ActivityCompat.requestPermissions(activity, permissionsStrArray, requestCode)
    }

    // This needs to be called explicitly from Activity.onRequestPermissionsResult
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == this.requestCode && uiJourneyState == UiJourneyState.REQUESTING) {
            uiJourneyState = if (areAllGranted(grantResults)) {
                onPermissionsGranted.run()
                UiJourneyState.NULL
            } else {
                showDialogue()
                UiJourneyState.DIALOGUE_SHOWING
            }
        }
    }

    private fun showDialogue() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.permissions_issue_dialogue_title)
            .setMessage(R.string.permissions_issue_dialogue_message)
            .setCancelable(false)
            .setNegativeButton(R.string.permissions_issue_dialogue_quit) { dialog: DialogInterface, _: Int ->
                quitApp()
                dialog.dismiss()
            }
            .setPositiveButton(R.string.permissions_issue_dialogue_go_to_settings) { dialog: DialogInterface, _: Int ->
                openAppSettings()
                dialog.dismiss()
            }
            .setOnDismissListener {
                uiJourneyState = UiJourneyState.NULL
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:" + activity.packageName)
        activity.startActivity(intent)
    }

    private fun quitApp() {
        activity.finishAndRemoveTask()
    }

    companion object {
        private val allPermissions: List<String>

        init {
            val permissions = mutableListOf(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                permissions += Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                permissions += Manifest.permission.WRITE_EXTERNAL_STORAGE
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions += Manifest.permission.ACTIVITY_RECOGNITION
            }

            allPermissions = permissions
        }
    }
}