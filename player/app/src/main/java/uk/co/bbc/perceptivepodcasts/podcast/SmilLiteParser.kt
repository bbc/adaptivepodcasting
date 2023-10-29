package uk.co.bbc.perceptivepodcasts.podcast

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import uk.co.bbc.perceptivepodcasts.merchant.DataMerchant
import uk.co.bbc.perceptivepodcasts.playback.*
import uk.co.bbc.perceptivepodcasts.util.Duration
import uk.co.bbc.perceptivepodcasts.util.Duration.Companion.ZERO
import uk.co.bbc.perceptivepodcasts.util.Duration.Companion.fromMillis
import uk.co.bbc.perceptivepodcasts.util.Duration.Companion.fromSecondsString
import uk.co.bbc.perceptivepodcasts.util.TSIUtils
import java.io.IOException
import java.io.InputStream

// XML Parser Class
class SmilLiteParser(
    private val context: Context,
    private val dataMerchants: HashMap<String, DataMerchant>, // Path to podcast parent directory
    private val parentDir: String
) {
    private val tag = "XML Parser"

    // XML NameSpace
    private val ns: String? = null
    private var customTests: HashMap<String?, CustomTest>? = null

    private inner class CustomTest(
        var id: String?,
        private val merchantId: String?,
        private val action: String?,
        private val acceptedValues: ArrayList<String>
    ) {
        private var result: Boolean? = null
        fun setResult(result: Boolean) {
            this.result = result
        }

        // Executes data merchant which relates to this test & compares accepted values with result
        fun execute(): Boolean {
            val merchant = dataMerchants[merchantId]
            Log.d("merchantId", merchantId!!)
            if (merchant == null) {
                Log.e(tag, "Merchant null for id $merchantId")
                return false
            } else {
                merchant.getData(action) { result1: String? ->
                    for (value in acceptedValues) {
                        if (result1 == value) {
                            setResult(true)
                            return@getData
                        }
                    }
                    setResult(false)
                }
            }
            while (result == null) {
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            Log.d(tag, "Custom Test: $id Result: $result")
            return result!!
        }
    }

    // Utility method for extracting SSML from TTS
    @Throws(XmlPullParserException::class, IOException::class)
    fun getInnerXml(parser: XmlPullParser): String {
        Log.d(tag, "getInnerXml parser " + parser.text)
        val sb = StringBuilder()
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> {
                    depth--
                    if (depth > 0) {
                        Log.d(tag, "getInnerXml END_TAG " + parser.text)
                        sb.append("</").append(parser.name).append(">")
                    }
                }
                XmlPullParser.START_TAG -> {
                    depth++
                    val attrs = StringBuilder()
                    var i = 0
                    while (i < parser.attributeCount) {
                        attrs.append(parser.getAttributeName(i)).append("=\"")
                            .append(parser.getAttributeValue(i)).append("\" ")
                        i++
                    }
                    Log.d(tag, "getInnerXml START_TAG " + parser.text)
                    sb.append("<").append(parser.name).append(" ").append(attrs).append(">")
                }
                else -> {
                    Log.d(tag, "getInnerXml DEFAULT " + parser.text)
                    sb.append(parser.text)
                }
            }
        }
        val content = sb.toString()
        Log.d(tag, "getInnerXml content $content")
        return content
    }

    // Utility method for skipping unwanted tags
    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        check(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    // Utility method for converting time strings in seconds to longs in milliseconds (e.g "1.5sec" to 1500L)
    private fun parseSeconds(longAttribute: String): Duration {
        return fromSecondsString(longAttribute.replace("[^\\d.]".toRegex(), ""))
    }

    // Utility method for converting volume strings into floats
    private fun parseFloatAttribute(floatAttribute: String): Float {
        // Remove non numerical characters from volume string
        return floatAttribute.replace("[^\\d.]".toRegex(), "").toFloat()
    }

    // Utility method for converting strings into boolean
    private fun parseBooleanAttribute(boolAttribute: String): Boolean {
        return java.lang.Boolean.parseBoolean(boolAttribute)
    }

    // Utility method to parse hh:mm:ss:mmm time strings
    private fun timeStringToMillisHHMMSSmmm(timeString: String): Int {
        // new ver allowing HH:MM:SS:mmm where mmm 0..999
        val secondsInHour = 3600
        val secondsInMinute = 60
        val millisecondsInSecond = 1000
        val timeStringSingles = timeString.split(":".toRegex()).toTypedArray()
        val timeFloatSingles = ArrayList<Float>()
        for (single in timeStringSingles) {
            var tryParse : Float? = single.toFloatOrNull()
            if (tryParse==null) {
                // probably suffixed with "sec"
                val indexOfSec = single.indexOf("sec", 0, true)
                if (indexOfSec <= 0) {
                    continue
                } else {
                    val r = IntRange(0,indexOfSec-1)
                    val tmp = single.substring(r)
                    tryParse = tmp.toFloatOrNull()
                    if (tryParse == null) {
                        continue
                    } else {
                        timeFloatSingles.add(tryParse)
                    }
                }
            } else {
                timeFloatSingles.add(tryParse)
            }
        }
        var i = timeFloatSingles.size
        var overallSeconds = 0.0F
        for (single in timeFloatSingles) {
            when (i) {
                3 -> {
                    overallSeconds += single * secondsInHour
                    Log.d(tag, "Hours Detected: $single")
                }
                2 -> {
                    overallSeconds += single * secondsInMinute
                    Log.d(tag, "Minutes Detected: $single")
                }
                1 -> {
                    overallSeconds += single
                    Log.d(tag, "Seconds Detected: $single")
                }
                else -> {}
            }
            i--
        }
        return (overallSeconds * millisecondsInSecond).toInt()
    }



    // Utility method for getting all relevant attributes from playable item tags
    private fun getAttributes(parser: XmlPullParser): HashMap<String, String?> {
        val typeParams = HashMap<String, String?>()
        typeParams["customTest"] = parser.getAttributeValue(null, "customTest")
        typeParams["inTime"] = parser.getAttributeValue(null, "inTime")
        typeParams["speed"] = parser.getAttributeValue(null, "speed")
        typeParams["duration"] = parser.getAttributeValue(null, "duration")
        typeParams["loop"] = parser.getAttributeValue(null, "loop")
        typeParams["type"] = parser.getAttributeValue(null, "type")
        typeParams["src"] = parentDir + "/" + parser.getAttributeValue(null, "src")
        typeParams["volLeft"] = parser.getAttributeValue(null, "volLeft")
        typeParams["volRight"] = parser.getAttributeValue(null, "volRight")
        typeParams["id"] = parser.getAttributeValue(null, "id")
        typeParams["relatedMerchant"] = parser.getAttributeValue(null, "relatedMerchant")
        typeParams["relatedAction"] = parser.getAttributeValue(null, "relatedAction")
        typeParams["clipBegin"] = parser.getAttributeValue(null, "clipBegin")
        typeParams["clipEnd"] = parser.getAttributeValue(null, "clipEnd")
        typeParams["tsi"] = parser.getAttributeValue(null, "tsi")
        typeParams["sqi"] = parser.getAttributeValue(null, "sqi")
        typeParams["ni"] = parser.getAttributeValue(null, "ni")
        typeParams["description"] = parser.getAttributeValue(null, "description")
        return typeParams
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: InputStream): Playable? {
        return input.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            readTopLevel(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readTopLevel(parser: XmlPullParser): Playable? {
        parser.require(XmlPullParser.START_TAG, ns, "smil")
        NarrativeImportance.isNIUsedInThisSMIL = false
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "head") {
                readHead(parser)
            } else if (name == "body") {
                return readBody(parser)
            }
        }
        return null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readHead(parser: XmlPullParser) {
        Log.d(tag, "Reading Head")
        parser.require(XmlPullParser.START_TAG, ns, "head")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "customAttributes") {
                customTests = readCustomAttributes(parser)
            } else {
                skip(parser)
            }
        }
        Log.d(tag, "Head Finished")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readBody(parser: XmlPullParser): Playable? {
        Log.d(tag, "Reading Body")
        parser.require(XmlPullParser.START_TAG, ns, "body")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "seq" -> {
                    return readSequence(
                        parser,
                        StringBuilder("MainSeq"),
                        0,
                        getAttributes(parser)
                    )
                }
                "par" -> {
                    return readParallel(
                        parser,
                        StringBuilder("MainPar"),
                        0,
                        getAttributes(parser)
                    )
                }
                "!--" -> {
                    return readComment(parser)
                }
                else -> {
                    skip(parser)
                }
            }
        }
        return null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCustomAttributes(parser: XmlPullParser): HashMap<String?, CustomTest> {
        Log.d(tag, "Reading Custom Attributes")
        val customTests = HashMap<String?, CustomTest>()
        parser.require(XmlPullParser.START_TAG, ns, "customAttributes")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "customTest") {
                val customTest = readCustomTest(parser, getAttributes(parser))
                customTests[customTest.id] = customTest
            } else {
                skip(parser)
            }
        }
        Log.d(tag, "Custom Attributes Finished")
        return customTests
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCustomTest(
        parser: XmlPullParser,
        typeParams: HashMap<String, String?>
    ): CustomTest {
        parser.require(XmlPullParser.START_TAG, ns, "customTest")
        val acceptedValues = ArrayList<String>()
        val relatedMerchant = typeParams["relatedMerchant"]
        val relatedAction = typeParams["relatedAction"]
        val id = typeParams["id"]
        Log.d(tag, "Reading Custom Test: $id")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            Log.d(tag, "$name found")
            if (name == "acceptedValue") {
                val acceptedValue = parser.nextText()
                acceptedValues.add(acceptedValue)
                Log.d(tag, "Accepted Value: $acceptedValue added")
                if (parser.eventType != XmlPullParser.END_TAG) {
                    parser.nextTag()
                }
            }
        }
        Log.d(tag, "Custom Test Finished")
        return CustomTest(id, relatedMerchant, relatedAction, acceptedValues)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readSwitch(parser: XmlPullParser, parentID: StringBuilder, count: Int): Playable? {
        parser.require(XmlPullParser.START_TAG, ns, "switch")
        var returnElement: Playable? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            Log.i(tag, "Switch Looping")
            val name = parser.name
            val typeParams = getAttributes(parser)
            val testName = typeParams["customTest"]

            // If this element's test passes, return a new instance of the element
            if (customTests!![testName]!!.execute()) {
                Log.i(tag, "Custom Test: $testName triggered")
                when (name) {
                    "seq" -> {
                        Log.i(tag, "returning sequence")
                        returnElement = readSequence(parser, parentID, count, typeParams)
                    }
                    "par" -> returnElement = readParallel(parser, parentID, count, typeParams)
                    "audio" -> returnElement = readAudio(parser, parentID, count, typeParams)
                }
            } else {
                skip(parser)
            }
        }
        return returnElement
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readSequence(
        parser: XmlPullParser,
        parentID: StringBuilder,
        count: Int,
        typeParams: HashMap<String, String?>
    ): Sequence {

        // Required for parser config
        parser.require(XmlPullParser.START_TAG, ns, "seq")
        val inTime = typeParams["inTime"]?.let { fromMillis(timeStringToMillisHHMMSSmmm(it).toLong()) } ?: ZERO
        val playableItems = mutableListOf<Playable>()

        // Generate unique ID based on position in XML tree
        val uId: String = if (count > 0) {
            parentID.append(", Seq ").append(count).toString()
        } else {
            parentID.toString()
        }

        var tsiString : String = typeParams["tsi"] ?: ""
        if ( tsiString.isEmpty()) {
            tsiString = typeParams["sqi"] ?: ""
        }
        val shouldIncludeViaTSI : Boolean
        val tmp : Boolean =
            TSIUtils().shouldItemBeIncludedTSI(context,tsiString)
        if(tmp) {
            shouldIncludeViaTSI = true
        }else{
            shouldIncludeViaTSI = false
            Log.d(tag, "$uId children will be skipped due to tsi $tsiString")
        }

        val description = typeParams["description"] ?: ""

        // Counter for number of child elements per parent element
        var i = 1

        // Iterate over child tags
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val attributes = getAttributes(parser)
            val pName = parser.name
            if (!shouldIncludeViaTSI) {
                skip(parser)
            } else {
                val item = when (pName) {
                    "switch" -> readSwitch(parser, StringBuilder(uId), i)
                    "seq" -> readSequence(parser, StringBuilder(uId), i, attributes)
                    "par" -> readParallel(parser, StringBuilder(uId), i, attributes)
                    "audio" -> readAudio(parser, StringBuilder(uId), i, attributes)
                    "vibrate" -> readVibrate(parser, StringBuilder(uId), i, attributes)
                    "!--" -> readComment(parser)
                    else -> null
                }
                item?.let { playableItems.add(item) }
                i++
            }

        }

        // Once child tags have been found and instantiated, store inside new parent sequence
        return Sequence(inTime, playableItems, uId, description )
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readParallel(
        parser: XmlPullParser,
        parentID: StringBuilder,
        count: Int,
        typeParams: HashMap<String, String?>
    ): Parallel {

        // Required for parser config
        parser.require(XmlPullParser.START_TAG, ns, "par")
        val inTime = typeParams["inTime"]?.let { fromMillis(timeStringToMillisHHMMSSmmm(it).toLong()) } ?: ZERO
        val playableItems = mutableListOf<Playable>()

        // Generate unique ID based on position in XML tree
        val uId: String = if (count > 0) {
            parentID.append(", Par ").append(count).toString()
        } else {
            parentID.toString()
        }

        var tsiString : String = typeParams["tsi"] ?: ""
        if (tsiString.isEmpty() ) {
            tsiString = typeParams["sqi"] ?: ""
        }
        val shouldIncludeViaTSI = TSIUtils().shouldItemBeIncludedTSI(context,tsiString)
        val description = typeParams["description"] ?: ""

        // Counter for number of child elements per parent element
        var i = 1

        // Iterate over child tags
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val attributes = getAttributes(parser)
            val pName = parser.name
            if (!shouldIncludeViaTSI) {
                skip(parser)
            } else {
                val item = when (pName) {
                    "switch" -> readSwitch(parser, StringBuilder(uId), i)
                    "seq" -> readSequence(parser, StringBuilder(uId), i, attributes)
                    "par" -> readParallel(parser, StringBuilder(uId), i, attributes)
                    "audio" -> readAudio(parser, StringBuilder(uId), i, attributes)
                    "vibrate" -> readVibrate(parser, StringBuilder(uId), i, attributes)
                    "!--" -> readComment(parser)
                    else -> null
                }
                item?.let { playableItems.add(it) }
                i++
            }
        }

        // Once child tags have been found and instantiated, store inside new parent parallel
        return Parallel(inTime, playableItems, uId, description)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readAudio(
        parser: XmlPullParser,
        parentID: StringBuilder,
        count: Int,
        typeParams: HashMap<String, String?>
    ): Playable {

        // Required for parser config
        parser.require(XmlPullParser.START_TAG, ns, "audio")

        // Default playing params
        val type = typeParams["type"]

        // TTS is generally loud compared to other files
        val defaultVol : Float = if(type == "tts") {
            0.25f*NarrativeImportance.defaultMult
        } else {
            1.0f*NarrativeImportance.defaultMult
        }
        // Handle timing and volume attributes
        val inTime = typeParams["inTime"]?.let { fromMillis(timeStringToMillisHHMMSSmmm(it).toLong()) } ?: ZERO
        val speed = typeParams["speed"]?.let { parseFloatAttribute(it) } ?: 1.0f
        var loop = typeParams["loop"]?.let { parseBooleanAttribute(it) } ?: false
        val clipBegin = typeParams["clipBegin"]?.let { fromMillis(timeStringToMillisHHMMSSmmm(it).toLong()) } ?: ZERO
        val clipEnd = typeParams["clipEnd"]?.let { fromMillis(timeStringToMillisHHMMSSmmm(it).toLong()) } ?: ZERO
        val duration = typeParams["duration"]?.let { fromMillis(timeStringToMillisHHMMSSmmm(it).toLong()) } ?: ZERO
        val volLeft = typeParams["volLeft"]?.let { parseFloatAttribute(it) } ?: defaultVol
        val volRight = typeParams["volRight"]?.let { parseFloatAttribute(it) } ?: defaultVol

        if (duration == ZERO) {
            loop = false
        }

        val ni03 : Int = typeParams["ni"]?.let {
            var possNi03 = NarrativeImportance.niStringToInt03(context, it)
            if (possNi03 > -1) {
                NarrativeImportance.isNIUsedInThisSMIL = true
            } else {
                possNi03 = NarrativeImportance.defaultItemNIIndex03
            }
            possNi03
        } ?: NarrativeImportance.defaultItemNIIndex03

        // Return TTS specific object if type attribute is "tts"
        if (type != null) {
            if (type == "tts") {

                // Grab any nested XML and treat as speech to be synthesized (SSML)
                Log.d("SSML", parser.toString())
                val ttsText = getInnerXml(parser)
                Log.d("SSML", ttsText)
                val uId = parentID.append(", TTSItem ").append(count).toString()
                return TTSItem(
                    context,
                    inTime,
                    clipBegin,
                    clipEnd,
                    duration,
                    volLeft,
                    volRight,
                    ttsText,
                    uId,
                    dataMerchants,
                    ni03
                )
            }
        }

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            skip(parser)
        }

        val audioUri: String? = typeParams["src"]
        Log.d(tag, "Audio Item Found")
        Log.d(tag, "URI: $audioUri")
        val toCheckURIFile = true
        if (toCheckURIFile) {
            val mmr = MediaMetadataRetriever()
            try {
                mmr.setDataSource(audioUri)
            } catch (ex: Exception) {
                Log.e("SmilLiteParser", ex.message ?: ("Bad file: " + audioUri.toString()))
                throw XmlPullParserException(ex.message)
            }
        }
        val uId = parentID.append(", AudioItem ").append(count).toString()
        return AudioItem(
            inTime,
            speed,
            loop,
            clipBegin,
            clipEnd,
            duration,
            volLeft,
            volRight,
            audioUri!!,
            uId,
            ni03
        )
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readVibrate(
        parser: XmlPullParser,
        parentID: StringBuilder,
        count: Int,
        typeParams: HashMap<String, String?>
    ): VibrateItem {

        parser.require(XmlPullParser.START_TAG, ns, "vibrate")

        val inTime = ZERO
        val duration = typeParams["duration"]?.let { parseSeconds(it) } ?: ZERO

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            skip(parser)
        }

        val uId = parentID.append(", VibItem ").append(count).toString()
        return VibrateItem(context, inTime, duration, uId)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readComment(
        parser: XmlPullParser
    ): VibrateItem? {

        parser.require(XmlPullParser.START_TAG, ns, "!--")

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            skip(parser)
        }
        return null
    }

}