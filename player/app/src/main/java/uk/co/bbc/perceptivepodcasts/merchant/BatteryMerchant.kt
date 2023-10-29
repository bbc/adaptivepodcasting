package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryMerchant(private val context: Context) : DataMerchant {

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(batteryFunc(param)) }
    }

    private fun batteryFunc(param: String?): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        val chargePlug = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val wirelessCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS

        // Determine percentage
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = level / scale.toFloat()

        return when (param) {
            "coarse", "level" ->
                if (batteryPct > 0.66) {
                    "high"
                } else if (batteryPct > 0.33) {
                    "medium"
                } else {
                    "low"
                }

            "isCharging", "is-charging" ->
                if (usbCharge || acCharge || wirelessCharge) {
                    "charging"
                } else {
                    "not charging"
                }

            "chargingMethod", "charging-method" ->
                if (usbCharge) {
                    return "USB charging"
                } else if (acCharge) {
                    return "mains charging"
                } else if (wirelessCharge) {
                    return "wireless charging"
                } else {
                    return "not charging"
                }

            else -> return (batteryPct * 100).toInt().toString() + "%"
        }
    }
}