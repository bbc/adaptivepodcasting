package uk.co.bbc.perceptivepodcasts.playback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import uk.co.bbc.perceptivepodcasts.util.ttsSanitize
import uk.co.bbc.perceptivepodcasts.util.allowInfiniteLines

class TtsBroadcastReceiver(private val currActivityView : View) : BroadcastReceiver() {
    private var ttsToast: Toast? = null
    private var ttsSnackBar: Snackbar? = null
    private val useSnackBar = true
    override fun onReceive(context: Context, intent: Intent) {
        val isstart = intent.getBooleanExtra("bisStart", false)
        val ttsTextUnSanitized : CharSequence = intent.getStringExtra("TTSText") as CharSequence
        val ttsText : CharSequence = ttsSanitize(ttsTextUnSanitized)
        if (useSnackBar) {
            if (isstart) {
                if (ttsSnackBar != null) {
                    ttsSnackBar?.dismiss()
                    ttsSnackBar = null
                }
                ttsSnackBar = Snackbar.make(currActivityView, ttsText, Snackbar.LENGTH_LONG)
                ttsSnackBar?.allowInfiniteLines()
                ttsSnackBar?.show()
            } else {
                if (ttsSnackBar != null) {
                    ttsSnackBar?.dismiss()
                    ttsSnackBar = null
                }
            }
        } else {
            if (isstart) {
                if (ttsToast != null) {
                    ttsToast?.cancel()
                    ttsToast = null
                }
                ttsToast = Toast.makeText(context, ttsText, Toast.LENGTH_LONG)
                ttsToast?.show()
            } else {
                if (ttsToast != null) {
                    ttsToast?.cancel()
                    ttsToast = null
                }
            }
        }
    }
}