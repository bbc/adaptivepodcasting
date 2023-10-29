package uk.co.bbc.perceptivepodcasts.info

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import uk.co.bbc.perceptivepodcasts.R

class FurtherInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_further_information)
        val furtherInformationView = findViewById<TextView>(R.id.further_information)
        val furtherInformationString = getString(R.string.content_further_information)
        val furtherInformationSpannable = SpannableString(furtherInformationString)
        furtherInformationSpannable.setSpan(
            URLSpan("https://www.w3.org/AudioVideo/"),
            211,
            280,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        furtherInformationSpannable.setSpan(
            URLSpan("https://www.bbc.co.uk/makerbox/tools/adaptive-podcasting"),
            488,
            496,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        furtherInformationSpannable.setSpan(
            URLSpan("https://www.bbc.co.uk/rd/blog/2022-09-adaptive-podcasting"),
            506,
            515,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        furtherInformationView.text = furtherInformationSpannable
        furtherInformationView.movementMethod = LinkMovementMethod.getInstance()
    }
}