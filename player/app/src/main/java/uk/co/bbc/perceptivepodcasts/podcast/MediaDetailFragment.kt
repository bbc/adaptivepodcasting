package uk.co.bbc.perceptivepodcasts.podcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import uk.co.bbc.perceptivepodcasts.R
import uk.co.bbc.perceptivepodcasts.channel.ChannelItem
import uk.co.bbc.perceptivepodcasts.channel.ChannelState
import uk.co.bbc.perceptivepodcasts.getApp
import uk.co.bbc.perceptivepodcasts.util.compatGetSerializable
import java.io.IOException
import kotlin.collections.set

const val MEDIA_ITEM_FRAGMENT_KEY = "media_item"

/**
 * A fragment representing a single Media detail screen.
 * This fragment is either contained in a [uk.co.bbc.perceptivepodcasts.channel.MediaListActivity]
 * in two-pane mode (on tablets) or a [uk.co.bbc.perceptivepodcasts.channel.MediaListActivity]
 * on handsets.
 */
class MediaDetailFragment : Fragment() {

    private val mItem: MediaItem by lazy { mediaItemFromArgs() }
    private val textView: TextView by lazy { requireView().findViewById(R.id.media_detail) }
    private val imageView: ImageView by lazy { requireView().findViewById(R.id.mdImg) }
    private val expListView: ExpandableListView by lazy { requireView().findViewById(R.id.lvExp) }
    private val app by lazy { requireContext().getApp() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.media_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app.channelManager.liveData.observe(viewLifecycleOwner) { onChannelState(it) }
        val appBarLayout = requireActivity().findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
        appBarLayout?.title = mItem.name
    }

    private fun mediaItemFromArgs(): MediaItem {
        return requireArguments().compatGetSerializable(MEDIA_ITEM_FRAGMENT_KEY)
            ?: throw IllegalStateException("MediaDetailFragment $this started with no media item")
    }

    private fun onChannelState(channelState: ChannelState) {
        val channelItems = channelState
            .channelItems
            .filter { (mediaItem): ChannelItem -> mediaItem.guid == mItem.guid }

        if (channelItems.isNotEmpty()) {
            val (mediaItem, _, bitmap) = channelItems[0]
            textView.text = mediaItem.description
            imageView.setImageBitmap(bitmap)
            expListView.setAdapter(createAdapter())
        }
    }

    private fun createAdapter(): PodcastInfoAdapter {
        val expandableListHeaders: MutableList<String> = ArrayList()
        val expandableListChildItems = HashMap<String, List<String>>()
        try {
            val podcastInfo = parseItemManifest(getManifestPath())
            if (podcastInfo != null) {
                for (creditGroup in podcastInfo.creditGroups) {
                    val groupName = "+ " + creditGroup.name
                    expandableListHeaders.add(groupName)
                    val creditNames: MutableList<String> = ArrayList()
                    for (credit in creditGroup.credits) {
                        val creditName = credit.name
                        creditNames.add(creditName)
                    }
                    expandableListChildItems[groupName] = creditNames
                }
            }
        } catch (e: IOException) {
            val missingJsonToast = Toast.makeText(
                requireContext(),
                "Cannot open: Missing manifest.JSON file",
                Toast.LENGTH_LONG
            )
            missingJsonToast.show()
        }
        return PodcastInfoAdapter(requireContext(), expandableListHeaders, expandableListChildItems)
    }

    private fun getManifestPath(): String {
        return requireContext().filesDir.toString() + "/" + "podcasts" + "/" + mItem.guid
    }
}