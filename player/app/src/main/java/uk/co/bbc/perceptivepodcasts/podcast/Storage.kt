package uk.co.bbc.perceptivepodcasts.podcast

import android.content.Context

enum class Asset {
    AUDIO,
    IMAGES
}

fun Context.podcastsDirectory(): String {
    return "$filesDir/podcasts"
}

fun Context.smallBitmapPath(mediaItem: MediaItem): String {
    return "${podcastsDirectory()}/${mediaItem.guid}/IMAGES/small.jpg"
}

fun Context.mediaItemLocation(guid: String): String {
    return "${podcastsDirectory()}/$guid"
}

fun Context.mediaItemLocation(mediaItem: MediaItem): String {
    return "${podcastsDirectory()}/${mediaItem.guid}"
}

fun Context.mediaItemLocation(mediaItem: MediaItem, asset: Asset): String {
    val root = mediaItemLocation(mediaItem)
    val subDir = when (asset) {
        Asset.AUDIO -> "AUDIO"
        Asset.IMAGES -> "IMAGES"
    }
    return "$root/$subDir/"
}

fun Context.downloadCopyLocation(mediaItem: MediaItem): String {
    return mediaItemLocation(mediaItem) + "_download"
}
