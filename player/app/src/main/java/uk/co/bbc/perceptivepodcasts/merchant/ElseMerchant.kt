package uk.co.bbc.perceptivepodcasts.merchant

class ElseMerchant : DataMerchant {
    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(elseFunc()) }
    }

    private fun elseFunc(): String {
        return "else"
    }
}