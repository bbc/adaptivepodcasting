package uk.co.bbc.perceptivepodcasts.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import uk.co.bbc.perceptivepodcasts.R

class TermsOfUseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_use)
        val termsOfUseView = findViewById<TextView>(R.id.terms_of_use)
        val termsOfUseString = getString(R.string.content_terms_of_use)
        val termsOfUseSpannable = SpannableString(termsOfUseString)
        termsOfUseSpannable.setSpan(
            URLSpan("http://www.bbc.co.uk/taster"),
            207,
            213,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        termsOfUseSpannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:taster@bbc.co.uk")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }, 266, 282, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        termsOfUseView.text = termsOfUseSpannable
        termsOfUseView.movementMethod = LinkMovementMethod.getInstance()
    }

}