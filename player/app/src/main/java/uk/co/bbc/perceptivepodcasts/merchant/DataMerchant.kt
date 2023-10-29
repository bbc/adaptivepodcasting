package uk.co.bbc.perceptivepodcasts.merchant

import android.os.Handler
import android.os.HandlerThread

interface DataMerchant {
    fun getData(param: String?, onComplete: (String?) -> Unit = {})
}

fun invokeOnCheckerThread(function: () -> Unit) {
    val aggChecker = HandlerThread("aggChecker")
    if (aggChecker.state == Thread.State.NEW) {
        aggChecker.start()
    }
    Handler(aggChecker.looper).post {
        function()
    }
}
