package uk.co.bbc.perceptivepodcasts.info

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import uk.co.bbc.perceptivepodcasts.R

class LegalWarningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_warning)
        val legalWarningView = findViewById<TextView>(R.id.legal_warning)
        val legalWarningString = getString(R.string.content_legal_warning)
        val legalWarningSpannable = SpannableString(legalWarningString)
        legalWarningSpannable.setSpan(
            URLSpan("https://www.bbc.co.uk/rd/projects/perceptive-radio"),
            179,
            195,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        legalWarningSpannable.setSpan(
            URLSpan("https://www.bbc.co.uk/rd/object-based-media"),
            199,
            229,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        legalWarningSpannable.setSpan(
            URLSpan("https://www.bbc.co.uk/privacy"),
            508,
            534,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        legalWarningView.text = legalWarningSpannable
        legalWarningView.movementMethod = LinkMovementMethod.getInstance()
    }
}