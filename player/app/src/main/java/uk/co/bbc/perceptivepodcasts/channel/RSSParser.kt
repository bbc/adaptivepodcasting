package uk.co.bbc.perceptivepodcasts.channel

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import uk.co.bbc.perceptivepodcasts.podcast.MediaItem
import uk.co.bbc.perceptivepodcasts.podcast.MediaItemType
import uk.co.bbc.perceptivepodcasts.podcast.asMediaItemType
import java.io.IOException
import java.io.InputStream

object RSSParser {

    // XML NameSpace
    private val ns: String? = null

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

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(input: InputStream): PodcastChannel? {
        return input.use {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            readRSS(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readRSS(parser: XmlPullParser): PodcastChannel? {
        parser.require(XmlPullParser.START_TAG, ns, "rss")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "channel") {
                return readChannel(parser)
            } else {
                skip(parser)
            }
        }
        return null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readChannel(parser: XmlPullParser): PodcastChannel {
        parser.require(XmlPullParser.START_TAG, ns, "channel")
        val mediaItemsList: MutableList<MediaItem> = ArrayList()
        var title: String? = null
        var lastBuildDate: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> {
                    title = parser.nextText()
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                "lastBuildDate" -> {
                    lastBuildDate = parser.nextText()
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                "item" -> mediaItemsList.add(readItem(parser))
                else -> skip(parser)
            }
        }
        return PodcastChannel(mediaItemsList, title!!, lastBuildDate!!)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readItem(parser: XmlPullParser): MediaItem {
        parser.require(XmlPullParser.START_TAG, ns, "item")
        var title: String? = null
        var guid: String? = null
        var summary: String? = null
        var description: String? = null
        var url: String? = null
        var type = MediaItemType.UNKNOWN
        var md5: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "title" -> {
                    title = parser.nextText()
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                "guid" -> {
                    guid = parser.nextText()
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                "summary" -> {
                    summary = parser.nextText()
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                "description" -> {
                    description = parser.nextText()
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                "pubDate" -> {
                    parser.nextText()
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                "enclosure" -> {
                    url = parser.getAttributeValue(ns, "url")
                    val typeString = parser.getAttributeValue(ns, "type")
                    type = typeString.asMediaItemType()
                    md5 = parser.getAttributeValue(ns, "md5")
                    if (parser.eventType != XmlPullParser.END_TAG) {
                        parser.nextTag()
                    }
                }
                else -> skip(parser)
            }
        }
        return MediaItem(title, guid, url, type, summary, description, md5)
    }
}