package uk.co.bbc.perceptivepodcasts.merchant

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LocationMerchant(private val context: Context) : DataMerchant {

    private val tag = "LocationMerchant"

    override fun getData(param: String?, onComplete: (String?) -> Unit) {
        invokeOnCheckerThread { onComplete(locationFunction(param)) }
    }

    private fun locationFunction(param: String?): String? {
        val mFusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        val doneLatch = CountDownLatch(1)
        val acquiredLocation = HashMap<String, String>()
        if (context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(tag, "getting last known location")
            Log.d(tag, "Location = " + mFusedLocationClient.lastLocation)

            mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                Log.d(tag, "Success!")

                // Got last known location
                if (location != null) {
                    Log.d(tag, "location not null")
                    val geocoder = Geocoder(context, Locale.getDefault())
                    try {
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses!!.size > 0) {
                            acquiredLocation["country"] = addresses[0].countryCode
                            acquiredLocation["locality"] = addresses[0].locality
                            Log.d(tag, "Location found")
                            Log.d(
                                tag,
                                "Locality :" + acquiredLocation["locality"] + " Country: " + acquiredLocation["country"]
                            )
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    doneLatch.countDown()
                } else {      //if is null
                    Log.d(tag, "Location is null")
                }
            }
            // Will be called if isSuccess not called, will still call code after try/catch if no location found
            mFusedLocationClient.lastLocation.addOnCompleteListener { doneLatch.countDown() }
        }
        try {
            doneLatch.await(2500, TimeUnit.MILLISECONDS)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val locality = acquiredLocation["locality"]
        if (param != null) {
            if (locality != null) {
                if (param == "city") {
                    return locality.lowercase(Locale.getDefault())
                }
            } else { //if location is off or hasn't been found for another reason
                return null
            }
        }
        if (locality != null) {
            when (locality) {
                "GB" -> return "uk"
                "US" -> return "usa"
                "IT" -> return "italy"
            }
            val country = acquiredLocation["country"]
            return country?.lowercase(Locale.getDefault())
        } else { //if location is off or hasn't been found for another reason
            return null
        }
    }

}