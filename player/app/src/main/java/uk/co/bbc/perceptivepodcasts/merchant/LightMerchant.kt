package uk.co.bbc.perceptivepodcasts.merchant

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class LightMerchant(context: Context): DataMerchant, SensorEventListener {

    private var lightValue: Float = 0.0f

    init {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        // TODO: Unregister!
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(getLightString()) }
    }

    private fun getLightString(): String {
        return if (lightValue < 10.0) "dark" else "light"
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_LIGHT) {
            lightValue = sensorEvent.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
    }

}
