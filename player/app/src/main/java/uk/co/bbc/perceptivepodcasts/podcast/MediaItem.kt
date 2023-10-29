package uk.co.bbc.perceptivepodcasts.podcast

import java.io.Serializable

enum class MediaItemType {
    ZIP,
    WAV,
    MP3,
    OGG,
    UNKNOWN
}

data class MediaItem(
    val name: String?,
    val guid: String?,
    val link: String?,
    val type: MediaItemType?,
    val summary: String?,
    val description: String?,
    val md5: String?
) : Serializable

fun String.asMediaItemType(): MediaItemType {
    return when (this) {
        "application/zip" -> MediaItemType.ZIP
        "audio/wav" -> MediaItemType.WAV
        "audio/mpeg" -> MediaItemType.MP3
        "audio/ogg" -> MediaItemType.OGG
        else -> MediaItemType.UNKNOWN
    }
}

fun MediaItemType.isZip(): Boolean {
    return this == MediaItemType.ZIP
}

fun MediaItemType.isMediaAsset(): Boolean {
    return !this.isZip()
}

fun MediaItemType.fileExtension(): String {
    return when (this) {
        MediaItemType.ZIP -> ".zip"
        MediaItemType.WAV -> ".wav"
        MediaItemType.MP3 -> ".mp3"
        MediaItemType.OGG -> ".ogg"
        MediaItemType.UNKNOWN -> ""
    }
}


