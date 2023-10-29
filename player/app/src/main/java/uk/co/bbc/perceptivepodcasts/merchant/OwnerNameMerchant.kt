package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.provider.ContactsContract
import uk.co.bbc.perceptivepodcasts.getApp

class OwnerNameMerchant(private val context: Context) : DataMerchant {

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread {
            onComplete(getOwnerNameFunction())
        }
    }

    private fun getOwnerNameFunction(): String {
        return getContactsDisplayName() ?: context.getApp().appSettingsManager.getUserNickname()
    }

    private fun getContactsDisplayName(): String? {
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        val cursor = context.contentResolver.query(
            ContactsContract.Profile.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        val name: String? = if (cursor != null && cursor.moveToFirst()) {
            cursor.getString(1)
        } else {
            null
        }
        cursor?.close()
        return name
    }
}