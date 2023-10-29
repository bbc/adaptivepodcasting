package uk.co.bbc.perceptivepodcasts.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import uk.co.bbc.perceptivepodcasts.R
import uk.co.bbc.perceptivepodcasts.getApp
import uk.co.bbc.perceptivepodcasts.info.APPrivacyNoticeActivity
import uk.co.bbc.perceptivepodcasts.info.FurtherInformationActivity
import uk.co.bbc.perceptivepodcasts.info.TermsOfUseActivity
import uk.co.bbc.perceptivepodcasts.playback.UserSpeedOverrideHelper
import uk.co.bbc.perceptivepodcasts.theme.PerceptivepodcastsTheme
import uk.co.bbc.perceptivepodcasts.theme.TopBar
import androidx.compose.ui.text.input.*

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsManager = getApp().appSettingsManager
        setContent {
            SettingsContent(settingsManager)
        }
    }
}

@Composable
private fun SettingsContent(settingsManager: AppSettingsManager) {

    val context = LocalContext.current
    var feedUrlDialogueVisible by remember { mutableStateOf(false) }
    var deleteConfirmDialogueVisible by remember { mutableStateOf(false) }
    var userActivityMonitoringOn by remember {
        mutableStateOf(settingsManager.settings.userActivityMonitoringEnabled)
    }
    var userNicknameDialogueVisible by remember { mutableStateOf(false) }
    var userSpeedDialogueVisible by remember { mutableStateOf(false) }
    var tsiDialogueVisible by remember { mutableStateOf(false) }

    PerceptivepodcastsTheme {
        Scaffold(topBar = { TopBar(R.string.settings_activity_title) }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(top = 8.dp)
            ) {
                Section("Preferences")
                Option("RSS Feed URL", "Choose where to pull new podcast updates from") {
                    feedUrlDialogueVisible = true
                }
                Separator()
                Option("Delete all downloads", "Delete all downloaded files from your device") {
                    deleteConfirmDialogueVisible = true
                }
                Separator()
                UserActivityMonitoring(userActivityMonitoringOn) { enabled ->
                    userActivityMonitoringOn = enabled
                    settingsManager.setUserActivityMonitoringEnabled(enabled)
                }
                Separator()
                Option("User Nickname", "Choose a nickname for TTS") {
                    userNicknameDialogueVisible = true
                }
                Separator()
                Option("Speed Override", "Global playback speed multiplier") {
                    userSpeedDialogueVisible = true
                }
                Separator()
                Option("Time Squeeze Importance", "Select from Full, Short or Highlights") {
                    tsiDialogueVisible = true
                }
                Separator()
                Section("About")
                Option("Terms of use") {
                    context.startActivity(Intent(context, TermsOfUseActivity::class.java))
                }
                Separator()
                Option("Privacy policy") {
                    val urlString = context.getString(R.string.privacy_web_url)
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlString)))
                }
                Separator()
                Option("AP terms of use") {
                    val urlString = context.getString(R.string.terms_web_url)
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlString)))
                }
                Separator()
                Option("AP privacy notice") {
                    context.startActivity(Intent(context, APPrivacyNoticeActivity::class.java))
                }
                Separator()
                Option("Your own Podcast") {
                    context.startActivity(Intent(context, FurtherInformationActivity::class.java))
                }
            }
        }

        if(feedUrlDialogueVisible) {
            val url = settingsManager.settings.rssFeedUrl
            RssEditDialogue(url) { newUrl ->
                feedUrlDialogueVisible = false
                newUrl?.let {
                    settingsManager.setRssFeedUrl(it)
                }
            }
        } else if(userNicknameDialogueVisible) {
            val userNickname = settingsManager.settings.userNicknameTTS
            UserNicknameEditDialogue(userNickname) { newUserNicknameTTS ->
                userNicknameDialogueVisible = false
                newUserNicknameTTS?.let {
                    settingsManager.setUserNickname(it)
				}
			}
        } else if(userSpeedDialogueVisible) {
            val userSpeed = settingsManager.settings.userSpeedOv
            UserSpeedEditDialogue(
                speedov = userSpeed,
                minVal = UserSpeedOverrideHelper.minspeed,
                maxVal = UserSpeedOverrideHelper.maxspeed
            )
            { newUserSpeedOv ->
                userSpeedDialogueVisible = false
                newUserSpeedOv?.let {
                    if ( newUserSpeedOv != userSpeed) {
                        settingsManager.setUserSpeedOv(it)
                    }
                }
            }
        } else if(tsiDialogueVisible) {
            val tsiindex = settingsManager.getUserTSIindex()
            val tsiAllowedStrings = settingsManager.getUserTSIStrings()
            TSIEditDialogue(tsiindex, tsiAllowedStrings ) { index: Int? ->
                tsiDialogueVisible = false
                index?.let {
                    settingsManager.setUserTSIindex(index)
                }
            }
        } else if(deleteConfirmDialogueVisible) {
            DeleteAllDownloadsDialogue {
                deleteConfirmDialogueVisible = false
                if(it) {
                    context.getApp().channelManager.deleteAllDownloads()
                }
            }
        }
    }
}

@Composable
fun UserActivityMonitoring(isOn: Boolean, function: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1.0f)
            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "User Activity Monitoring",
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Enable or disable user activity monitoring",
                color = MaterialTheme.colors.secondary,
                style = MaterialTheme.typography.subtitle2
            )
        }
        val switchColors = SwitchDefaults.colors(
            checkedThumbColor = Color.Red,
            uncheckedThumbColor = Color.Gray,
            checkedTrackColor = Color.LightGray,
            uncheckedTrackColor = Color.LightGray
        )
        Switch(
            colors = switchColors,
            checked = isOn,
            onCheckedChange = function,
        )
    }
}

@Composable
private fun RssEditDialogue(url: String, onDismiss: (String?) -> Unit) {

    var workingUrl by remember { mutableStateOf(url) }

    Dialog(onDismissRequest = { onDismiss(null) }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = "RSS Url:",
                        style = MaterialTheme.typography.h6,
                    )
                    Spacer(Modifier.size(16.dp))
                    OutlinedTextField(
                        value = workingUrl,
                        onValueChange = { workingUrl = it },
                        label = { Text("URL") },
                    )
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss(null) },
                        content = { Text("CANCEL") },
                    )
                    TextButton(
                        onClick = { onDismiss(workingUrl) },
                        content = { Text("OK") },
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteAllDownloadsDialogue(onDismiss: (Boolean) -> Unit) {
    Dialog(onDismissRequest = { onDismiss(false) }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.delete_all_confirm_title),
                    style = MaterialTheme.typography.h6,
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = "Delete all downloaded files from this app on your device?",
                    style = MaterialTheme.typography.body1,
                )
                Row(
                    Modifier.padding(8.dp).fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss(false) },
                        content = { Text("CANCEL") },
                    )
                    TextButton(
                        onClick = { onDismiss(true) },
                        content = { Text("OK") },
                    )
                }
            }
        }
    }
}

@Composable
private fun UserNicknameEditDialogue(nickname: String, onDismiss: (String?) -> Unit) {

    var workingNickname by remember { mutableStateOf(nickname) }

    Dialog(onDismissRequest = { onDismiss(null) }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = "User Nickname:",
                        style = MaterialTheme.typography.h6,
                    )
                    Spacer(Modifier.size(16.dp))
                    OutlinedTextField(
                        value = workingNickname,
                        onValueChange = { workingNickname = it },
                        label = { Text("Nickname") },
                    )
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss(null) },
                        content = { Text("CANCEL") },
                    )
                    TextButton(
                        onClick = { onDismiss(workingNickname) },
                        content = { Text("OK") },
                    )
                }
            }
        }
    }
}

@Composable
private fun UserSpeedEditDialogue(
    speedov: Float,
    minVal: Float,
    maxVal: Float,
    onDismiss: (Float?) -> Unit
){
    var workingSpeedTextValue by remember { mutableStateOf(speedov.toString()) }
    var isValueValid by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = { if (isValueValid) onDismiss(workingSpeedTextValue.toFloat()) }
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = "User Speed Override:",
                        style = MaterialTheme.typography.h6,
                    )
                    Spacer(Modifier.size(16.dp))
                    OutlinedTextField(
                        value = workingSpeedTextValue,
                        onValueChange = { newValue ->
                            val indperiod1 = newValue.indexOfFirst { it == '.' } // or -1
                            workingSpeedTextValue = newValue.filterIndexed { ind, c ->
                                c.isDigit() || ( (c == '.') && ( (indperiod1 != -1) && (ind == indperiod1) ) )
                            }
                            isValueValid = try {
                                val floatVal = newValue.toFloat()
                                (floatVal >= minVal) && (floatVal <= maxVal)
                            } catch (e: NumberFormatException) {
                                false
                            }
                        },
                        label = { Text("Select a speed from "+
                                minVal.toString() + " to " +
                                maxVal.toString() )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.None,
                        ),

                        )
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss(null) },
                        content = { Text("CANCEL") },
                    )
                    TextButton(
                        onClick = { if (isValueValid) onDismiss(workingSpeedTextValue.toFloat()) },
                        content = { Text("OK") },
                    )
                }
            }
        }
    }
}

@Composable
private fun TSIEditDialogue(tsiindex: Int, tsiAllowedStrings : Array<String>, onDismiss: (Int?) -> Unit) {

    var workingIndex by remember { mutableStateOf(tsiindex) }

    Dialog(onDismissRequest = { onDismiss(null) }) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = "TSI code:",
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(Modifier.size(16.dp))
                    // Create a RadioButton for each item in the list
                    for (i in 0 until tsiAllowedStrings.size) {
                        Row {
                            RadioButton(
                                selected = i == workingIndex,
                                onClick = {
                                    // Update the selected index
                                    workingIndex = i
                                },
                                modifier = Modifier.padding(0.dp)

                            )
                            Text(
                                text = tsiAllowedStrings[i],
                                modifier = Modifier.padding(13.dp),
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = { onDismiss(null) },
                        content = { Text("CANCEL") },
                    )
                    TextButton(
                        onClick = { onDismiss(workingIndex) },
                        content = { Text("OK") },
                    )
                }
            }
        }
    }
}

@Composable
private fun Separator() {
    Divider(
        color = Color.Gray,
        thickness = 1.dp)
}

@Composable
private fun Section(name: String) {
    Text(
        modifier = Modifier.padding(
            top = 8.dp,
            start = 16.dp,
        ),
        text = name,
        color = MaterialTheme.colors.secondary,
        style = MaterialTheme.typography.body2
    )
}

@Composable
private fun Option(name: String, description: String? = null, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(1.0f)
            .clickable { onClick() }
            .padding(
                start = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            )
    ) {
        Text(
            text = name,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body2
        )
        description?.let {
            Text(
                text = it,
                color = MaterialTheme.colors.primaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}

