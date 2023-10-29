package uk.co.bbc.perceptivepodcasts.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import uk.co.bbc.perceptivepodcasts.R

class APPrivacyNoticeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple_text_activity)
        val textView = findViewById<TextView>(R.id.text_view)
        textView.movementMethod = LinkMovementMethod.getInstance()
        val sp = HtmlCompat.fromHtml(
            getString(R.string.ap_privacy_notice),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        textView.text = sp
    }
}