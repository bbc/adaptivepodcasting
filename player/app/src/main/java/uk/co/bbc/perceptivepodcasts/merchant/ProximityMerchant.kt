package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ProximityMerchant(context: Context): DataMerchant, SensorEventListener {

    private var proximityValue: Float = 0.0f

    init {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(proximityString()) }
    }

    private fun proximityString(): String {
        return if (proximityValue.toDouble() == 0.0) {
            "near"
        } else {
            "far"
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_PROXIMITY) {
            proximityValue = sensorEvent.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
    }

}
