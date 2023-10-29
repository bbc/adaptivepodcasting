package uk.co.bbc.perceptivepodcasts.channel

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uk.co.bbc.perceptivepodcasts.podcast.MediaItem

sealed class PodcastListClickEvent(val mediaItem: MediaItem)
class PodcastDownloadEvent(mediaItem: MediaItem): PodcastListClickEvent(mediaItem)
class PodcastPlayEvent(mediaItem: MediaItem): PodcastListClickEvent(mediaItem)

@Composable
fun PodcastList(
    state: ChannelState?,
    onEvent: (PodcastListClickEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        state?.channelItems?.forEach {
            item {
                PodcastRow(it, onEvent)
                Divider(
                    color = Color.Gray,
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
private fun PodcastRow(item: ChannelItem, onEvent: (PodcastListClickEvent) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .setupRowClickActions(item, onEvent)
        .padding(8.dp)
    ) {
        val name = item.mediaItem.name ?: ""
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.size(100.dp)
        ) {
            PodcastIcon(item, name)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body1
            )
            item.mediaItem.description?.let { description ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.primaryVariant,
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }
    }
}

@Composable
private fun PodcastIcon(
    item: ChannelItem,
    name: String
) {
    when (item.state) {
        DownloadState.NULL, DownloadState.DOWNLOAD_ERROR -> {
            Icon(
                imageVector = Icons.Filled.Download,
                tint = MaterialTheme.colors.secondary,
                contentDescription = name,
                modifier = Modifier.padding(28.dp)
            )
        }
        DownloadState.DOWNLOADING -> {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(24.dp),
                strokeWidth = 6.dp
            )
        }
        DownloadState.DOWNLOADED -> PodcastImage(item, name)
    }
}

private fun Modifier.setupRowClickActions(
    item: ChannelItem,
    onEvent: (PodcastListClickEvent) -> Unit
): Modifier {
    return when (item.state) {
        DownloadState.DOWNLOADING -> this
        DownloadState.DOWNLOADED -> {
            clickable { onEvent(PodcastPlayEvent(item.mediaItem)) }
        }
        DownloadState.NULL,
        DownloadState.DOWNLOAD_ERROR -> {
            clickable { onEvent(PodcastDownloadEvent(item.mediaItem)) }
        }
    }
}

@Composable
private fun PodcastImage(item: ChannelItem, name: String) {
    item.bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = name,
            contentScale = ContentScale.Crop
        )
    }
}
