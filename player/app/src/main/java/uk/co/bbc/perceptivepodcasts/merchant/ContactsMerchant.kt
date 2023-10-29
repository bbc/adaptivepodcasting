package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.provider.ContactsContract

class ContactsMerchant(private val context: Context) : DataMerchant {

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(getContactsFunction()) }
    }

    private fun getContactsFunction(): String {
        val cursor = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        val count = cursor!!.count
        cursor.close()
        return count.toString()
    }
}