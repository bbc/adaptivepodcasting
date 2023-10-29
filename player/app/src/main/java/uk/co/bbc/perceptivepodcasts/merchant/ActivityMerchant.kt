package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import uk.co.bbc.perceptivepodcasts.getApp
import uk.co.bbc.perceptivepodcasts.useractivity.toActivityString

class ActivityMerchant(private val context: Context) : DataMerchant {

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(activityFunc()) }
    }

    private fun activityFunc(): String? {
        return context.getApp().userActivityManager.lastActivityType?.toActivityString()
    }
}

