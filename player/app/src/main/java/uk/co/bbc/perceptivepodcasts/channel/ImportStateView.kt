package uk.co.bbc.perceptivepodcasts.channel

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ImportResultDialogue(state: ChannelState?, onDismissed: () -> Unit) {
    when(state?.importState) {
        ImportState.IMPORT_SUCCESS -> {
            state.channelItems.firstOrNull()?.let {
                MessageDialogue("Podcast ${it.mediaItem.name} imported.") {
                    onDismissed()
                }
            }
        }
        ImportState.IMPORT_FAILURE -> {
            MessageDialogue("There was an error importing the podcast.") {
                onDismissed()
            }
        }
        ImportState.IMPORTING, ImportState.NULL, null -> {}
    }
}

@Composable
private fun MessageDialogue(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.h6,
                )
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        content = { Text("OK") },
                    )
                }
            }
        }
    }
}

@Composable
fun ImportProgressIndicator(state: ChannelState?) {

    val importing = state?.importState == ImportState.IMPORTING
    val refreshing = state?.channelRefreshState == ChannelRefreshState.REFRESHING

    if (importing || refreshing) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondary
        )
    }
}
