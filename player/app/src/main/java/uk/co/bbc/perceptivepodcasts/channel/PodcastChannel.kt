package uk.co.bbc.perceptivepodcasts.channel

import uk.co.bbc.perceptivepodcasts.podcast.MediaItem

data class PodcastChannel(
    val mediaItemList: List<MediaItem>,
    val title: String,
    val lastBuildDate: String
)