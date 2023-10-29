package uk.co.bbc.perceptivepodcasts.playback

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import uk.co.bbc.perceptivepodcasts.merchant.DataMerchant
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

typealias MerchantMap = HashMap<String, DataMerchant>

sealed class DynScriptElement
class StaticScript(val staticScript: String?): DynScriptElement()
class DynQuery(val merchantId: String?, val action: String?): DynScriptElement()

class DynScriptParser {

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(dataMerchants: HashMap<String, DataMerchant>, xmlString: String): String {
        val dynQueries = parseXml(xmlString)
        val scripts = freezeScripts(dynQueries, dataMerchants)
        val output = StringBuilder()
        for (staticScript in scripts) {
            output.append(staticScript.staticScript)
        }
        return output.toString()
    }

    private fun parseXml(xmlString: String): List<DynScriptElement> {
        val scripts = mutableListOf<DynScriptElement>()
        val parser = buildParser(xmlString)
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "text" -> scripts.add(parseText(parser))
                "dynquery" -> scripts.add(parseDynQuery(parser))
                else -> parser.skip()
            }
        }

        return scripts
    }

    private fun freezeScripts(dynQueries: List<DynScriptElement>, dataMerchants: MerchantMap): List<StaticScript> {
        return dynQueries.map {
            when(it) {
                is DynQuery -> {
                    val dataMerchant = dataMerchants[it.merchantId]
                        ?: throw XmlPullParserException("Could not find merchant ${it.merchantId}")
                    dataMerchant.freezeSynchronously(it)
                }
                is StaticScript -> it
            }
        }
    }

    private fun parseDynQuery(parser: XmlPullParser): DynQuery {
        return DynQuery(
            parser.getAttributeValue(null, "merchantId"),
            parser.getAttributeValue(null, "relatedAction")
        )
    }

    private fun parseText(parser: XmlPullParser): StaticScript {
        return StaticScript(parser.nextText())
    }

    private fun buildParser(xmlString: String): XmlPullParser {
        val stream = ByteArrayInputStream(xmlString.toByteArray())
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(stream, null)
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, null, "dynscript")
        return parser
    }

}

@Throws(XmlPullParserException::class, IOException::class)
private fun XmlPullParser.skip() {
    check(eventType == XmlPullParser.START_TAG)
    var depth = 1
    while (depth != 0) {
        when (next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}

private fun DataMerchant.freezeSynchronously(query: DynQuery): StaticScript {
    val complete = AtomicBoolean(false)
    var result: String? = null

    getData(query.action) {
        result = it
        complete.set(true)
    }

    // TODO: remove this
    while (!complete.get()) {
        try {
            Thread.sleep(10)
        } catch (ignored: InterruptedException) {
        }
    }

    return StaticScript(result)
}
