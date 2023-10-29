package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import uk.co.bbc.perceptivepodcasts.merchant.Merchants

object PlayerBuilder {

    private const val MERCHANT_ID_USER_NAME = "USER_NAME"
    private const val MERCHANT_ID_USER_LOCATION = "USER_LOCATION"
    private const val MERCHANT_ID_USER_CONTACTS = "USER_CONTACTS"
    private const val MERCHANT_ID_BATTERY = "BATTERY"
    private const val MERCHANT_ID_PROXIMITY = "PROXIMITY"
    private const val MERCHANT_ID_ELSE = "ELSE"
    private const val MERCHANT_ID_LIGHT = "LIGHT"
    private const val MERCHANT_ID_TIME = "TIME"
    private const val MERCHANT_ID_HEADPHONES = "HEADPHONES"
    private const val MERCHANT_ID_MEDIA_VOLUME = "MEDIA_VOLUME"
    private const val MERCHANT_ID_DEVICE_MODE = "DEVICE_MODE"
    private const val MERCHANT_ID_ACTIVITY = "ACTIVITY"
    private const val MERCHANT_ID_USER_LANGUAGE = "USER_LANGUAGE"
    private const val MERCHANT_ID_DATE = "DATE"

    fun build(context: Context, mainScope: CoroutineScope): PerceptivePlayer {
        val appContext = context.applicationContext
        val merchants = Merchants(appContext)
        val perceptivePlayer = PerceptivePlayer(appContext, mainScope)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_USER_NAME, merchants.ownerNameMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_USER_CONTACTS, merchants.contactsMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_BATTERY, merchants.batteryPercentageMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_USER_LOCATION, merchants.userLocationMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_PROXIMITY, merchants.proximityMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_LIGHT, merchants.lightMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_TIME, merchants.timeMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_ELSE, merchants.elseMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_ACTIVITY, merchants.activityMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_HEADPHONES, merchants.headphonesMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_DEVICE_MODE, merchants.ringerModeMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_MEDIA_VOLUME, merchants.mediaVolumeMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_USER_LANGUAGE, merchants.userLanguageMerchant)
        perceptivePlayer.addDataMerchant(MERCHANT_ID_DATE, merchants.dateMerchant)
        return perceptivePlayer
    }
}