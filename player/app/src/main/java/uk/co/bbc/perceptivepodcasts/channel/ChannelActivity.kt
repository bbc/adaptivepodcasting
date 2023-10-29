package uk.co.bbc.perceptivepodcasts.channel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import uk.co.bbc.perceptivepodcasts.R
import uk.co.bbc.perceptivepodcasts.getApp
import uk.co.bbc.perceptivepodcasts.podcast.MEDIA_ITEM_FRAGMENT_KEY
import uk.co.bbc.perceptivepodcasts.podcast.MediaDetailActivity
import uk.co.bbc.perceptivepodcasts.podcast.MediaItem
import uk.co.bbc.perceptivepodcasts.settings.SettingsActivity
import uk.co.bbc.perceptivepodcasts.theme.TopBar
import uk.co.bbc.perceptivepodcasts.theme.PerceptivepodcastsTheme
import uk.co.bbc.perceptivepodcasts.util.PermissionsActivityHelper

private const val PODCAST_DOWNLOAD_PERMISSIONS_REQUEST = 2

class ChannelActivity : AppCompatActivity() {

    private val channelRepo: ChannelRepo by lazy { getApp().channelManager }
    private val permissionsHelper: PermissionsActivityHelper by lazy { createPermissionsHelper() }
    private val userActivityManager by lazy { getApp().userActivityManager }
    private val settingsManager by lazy { getApp().appSettingsManager }

    private lateinit var zipPodcastImportLauncher: ActivityResultLauncher<Void?>

    private fun createPermissionsHelper(): PermissionsActivityHelper {
        return PermissionsActivityHelper(this, PODCAST_DOWNLOAD_PERMISSIONS_REQUEST) {
            channelRepo.refreshPodcastList()
            userActivityManager.onPermissionConfirmed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val data = channelRepo.liveData.observeAsState()
            val greetingDismissed = isGreetingDismissed().observeAsState()
            ChannelView(
                state = data.value,
                greetingDismissed = greetingDismissed.value,
                onPodcastEvent = this::onPodcastClicked,
                onMenuEvent = this::onMenuClicked,
                onImportResultDismissed = this::clearImportState,
                onGreetingDismissed = { settingsManager.setGreetingDismissed() }
            )
        }
        permissionsHelper.activate()
        zipPodcastImportLauncher = registerForActivityResult(OpenZipDocument()) { uri ->
            uri?.let { channelRepo.addPodcastFromFile(it) }
        }
    }

    private fun isGreetingDismissed(): LiveData<Boolean> {
        return Transformations.map(settingsManager.liveData) { it.greetingDismissed }
    }

    private fun onPodcastClicked(event: PodcastListClickEvent) {
        when(event) {
            is PodcastDownloadEvent -> channelRepo.downloadPodcast(event.mediaItem)
            is PodcastPlayEvent -> launchPodcastView(event.mediaItem)
        }
    }

    private fun onMenuClicked(event: MenuEvent) {
        when(event) {
            ImportPodcastEvent -> zipPodcastImportLauncher.launch(null)
            ReloadChannelEvent -> channelRepo.refreshPodcastList()
            ShowSettingsEvent -> launchSettingsView()
        }
    }

    private fun clearImportState() {
        channelRepo.clearImportState()
    }

    private fun launchSettingsView() {
        startActivity(Intent(applicationContext, SettingsActivity::class.java))
    }

    private fun launchPodcastView(mediaItem: MediaItem) {
        val intent = Intent(this, MediaDetailActivity::class.java)
        intent.putExtra(MEDIA_ITEM_FRAGMENT_KEY, mediaItem)
        this.startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsHelper.onRequestPermissionsResult(requestCode, grantResults)
    }

}

@Composable
private fun ChannelView(
    state: ChannelState?,
    greetingDismissed: Boolean?,
    onPodcastEvent: (PodcastListClickEvent) -> Unit,
    onMenuEvent: (MenuEvent) -> Unit,
    onImportResultDismissed: () -> Unit,
    onGreetingDismissed: () -> Unit
) {
    PerceptivepodcastsTheme {
        Scaffold(
            topBar = { PodcastListTopBar(state) },
            floatingActionButton = { FloatingActionMenu(onMenuEvent) }
        ) {
            Box(modifier = Modifier.padding(it).fillMaxSize()) {
                PodcastList(state, onPodcastEvent)
            }
        }
        ImportResultDialogue(state, onImportResultDismissed)
        if(greetingDismissed != true) {
            GreetingDialogue { onGreetingDismissed() }
        }
    }
}

@Composable
private fun PodcastListTopBar(state: ChannelState?) {
    Box {
        TopBar(R.string.app_name)
        ImportProgressIndicator(state)
    }
}

// specific activity result contract for opening zip files
class OpenZipDocument : ActivityResultContract<Void?, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: Void?): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).setType("application/zip")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
    }
}
