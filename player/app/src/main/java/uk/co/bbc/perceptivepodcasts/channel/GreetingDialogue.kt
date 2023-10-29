package uk.co.bbc.perceptivepodcasts.channel

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.co.bbc.perceptivepodcasts.R

@Composable
fun GreetingDialogue(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.greeting_dialogue_title),
                    style = MaterialTheme.typography.h6,
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.intro_popup),
                    style = MaterialTheme.typography.body1,
                )
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss() },
                        content = {
                            Text(
                                text = stringResource(R.string.dialogue_ok)
                            )
                        }
                    )
                }
            }
        }
    }
}