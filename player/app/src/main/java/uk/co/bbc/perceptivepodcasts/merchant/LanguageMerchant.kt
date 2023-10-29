package uk.co.bbc.perceptivepodcasts.merchant

import java.util.*

class LanguageMerchant : DataMerchant {
    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(languageFunc(param)) }
    }

    private fun languageFunc(param: String?): String? {
        return if (param == "language-code") {
            val languageCode = Locale.getDefault().toString().lowercase(Locale.getDefault())
            if (languageCode != "") languageCode else null
        } else {
            val language = Locale.getDefault().displayLanguage.lowercase(Locale.getDefault())
            if (language != "") language else null
        }
    }

}