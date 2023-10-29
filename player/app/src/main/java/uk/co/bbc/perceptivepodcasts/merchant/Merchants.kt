package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context

class Merchants(context: Context) {
    val userLocationMerchant = LocationMerchant(context)
    val ownerNameMerchant = OwnerNameMerchant(context)
    val contactsMerchant = ContactsMerchant(context)
    val batteryPercentageMerchant = BatteryMerchant(context)
    val proximityMerchant = ProximityMerchant(context)
    val lightMerchant = LightMerchant(context)
    val elseMerchant = ElseMerchant()
    val timeMerchant = TimeMerchant()
    val headphonesMerchant = HeadphonesMerchant(context)
    val mediaVolumeMerchant = VolumeMerchant(context)
    val ringerModeMerchant = RingerModeMerchant(context)
    val activityMerchant = ActivityMerchant(context)
    val userLanguageMerchant = LanguageMerchant()
    val dateMerchant = DateMerchant()
}
