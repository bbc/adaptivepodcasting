package uk.co.bbc.perceptivepodcasts.podcast

import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.widget.*
import android.widget.SeekBar.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import uk.co.bbc.perceptivepodcasts.App
import uk.co.bbc.perceptivepodcasts.R
import uk.co.bbc.perceptivepodcasts.channel.ChannelActivity
import uk.co.bbc.perceptivepodcasts.getApp
import uk.co.bbc.perceptivepodcasts.playback.*
import uk.co.bbc.perceptivepodcasts.playback.PlaybackManager.PlayState
import uk.co.bbc.perceptivepodcasts.playback.PlaybackManager.PlayerState
import uk.co.bbc.perceptivepodcasts.util.Duration
import uk.co.bbc.perceptivepodcasts.util.compatGetSerializableExtra

class MediaDetailActivity : AppCompatActivity() {

    private val app: App by lazy { this.getApp() }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }
    private val progressBar: ProgressBar by lazy { findViewById(R.id.prepareProgressBar) }
    private val playbackController: PlaybackController by lazy { app.playbackController }
    private val mediaItem: MediaItem by lazy { mediaItemFromIntent() }
    private var ttsbroadcastReceiver: TtsBroadcastReceiver? = null
    private val niSlider: SeekBar by lazy {findViewById(R.id.seekBarNI)}
    private var prevNIJob : Job = Job()
    private val editTextNumberXP : EditText by lazy { findViewById(R.id.editTextNumberXP) }
    private val buttonXP1 : Button by lazy { findViewById(R.id.buttonXP1) }
    private val buttonXP2 : Button by lazy { findViewById(R.id.buttonXP2) }
    private val buttonXP3 : Button by lazy { findViewById(R.id.buttonXP3) }
    private val tvppp : TextView by lazy { findViewById(R.id.tvPPP) }

    var xpMillis : Long = 0 // resources arent avail yet, nor in init... so default to 0 for now

    init {
        prevNIJob.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_detail)
        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)
        setSupportActionBar(toolbar)
        volumeControlStream = AudioManager.STREAM_MUSIC

        // Show the Up button in the action bar.
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        app.playerLiveData.observe(this, this::onPlaybackState)

        val debTRY_BC_TTS = true
        if (debTRY_BC_TTS) {
            val intentFilter = IntentFilter()
            intentFilter.addAction("uk.co.bbc.perceptivepodcasts.ttsbc")
            val v : View = findViewById(android.R.id.content)
            ttsbroadcastReceiver = TtsBroadcastReceiver(v)
            registerReceiver(ttsbroadcastReceiver, intentFilter)
        }

        findViewById<View>(R.id.seekBarNI).visibility = INVISIBLE
        findViewById<View>(R.id.seekLabel).visibility = INVISIBLE
        findViewById<View>(R.id.seekLabel2).visibility = INVISIBLE
        niSlider.max = 100
        niSlider.progress = (NarrativeImportance.currentSliderValue01F * niSlider.max.toFloat()).toInt()
        niSlider.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromuser: Boolean) {
                assert(progress >= 0 && progress <= niSlider.max)
                NarrativeImportance.currentSliderValue01F = progress.toFloat() / niSlider.max.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val action = PPParams.NarrativeImportance(NarrativeImportance.currentSliderValue01F)
                val pbc : PlaybackManager = applicationContext.getApp().playbackController as PlaybackManager
                val topLevel : Playable? = pbc.getPlayer()?.topLevel
                if ( prevNIJob.isActive) {
                    return
                }
                val currNIJob = MainScope().launch {

                    topLevel?.postPrepare(action)

                }
                prevNIJob = currNIJob
            }
        })

        editTextNumberXP.setText(getString(R.string.def_xp_secs))
        try {
            xpMillis = editTextNumberXP.text.toString().toLong()*1000
        }
        catch(ignored : Exception) {
        }

        fun enableSkipButtons() {
            buttonXP1.isEnabled = true
            buttonXP2.isEnabled = true
            buttonXP3.isEnabled = true
        }

        fun disableSkipButtons() {
            buttonXP1.isEnabled = false
            buttonXP2.isEnabled = false
            buttonXP3.isEnabled = false
        }

        fun onSkipComplete() {
            enableSkipButtons()
        }

        buttonXP1.setOnClickListener { // reStart
            val pbc : PlaybackManager = applicationContext.getApp().playbackController as PlaybackManager
            disableSkipButtons()
            pbc.handleSkip(null, { onSkipComplete() } ) // null signifies reStart
        }

        buttonXP2.setOnClickListener {  // fwd
            val pbc : PlaybackManager = applicationContext.getApp().playbackController as PlaybackManager
            disableSkipButtons()
            pbc.handleSkip(Duration.fromMillis(this.xpMillis), { onSkipComplete() } )
        }

        buttonXP3.setOnClickListener { // skipBack
            val pbc : PlaybackManager = applicationContext.getApp().playbackController as PlaybackManager
            disableSkipButtons()
            pbc.handleSkip(Duration.fromMillis(this.xpMillis*-1), { onSkipComplete() } )
        }

        editTextNumberXP.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    try {
                        xpMillis = s.toString().toLong()*1000
                    }
                    catch(ignored : Exception) {
                    }
                }
        })

        tvppp.text = "0%"

        (getApp().playbackController as PlaybackManager).mldPlaybackProgress
            .observe(this) {
            onPercentageChanged( (getApp().playbackController as PlaybackManager).mldPlaybackProgress.getValue()!!)
        }


        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = MediaDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MEDIA_ITEM_FRAGMENT_KEY, mediaItem)
                }
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.media_detail_container, fragment)
                .commit()
        }
    }

    private fun mediaItemFromIntent(): MediaItem {
        return intent.compatGetSerializableExtra(MEDIA_ITEM_FRAGMENT_KEY)
            ?: throw IllegalStateException("MediaDetailActivity $this started with no media item")
    }


    private fun onPercentageChanged( pppdata : PlaybackManager.PlaybackProgress) {
        tvppp.text = (pppdata.pc).toInt().toString() + "%"
    }

    private fun onPlaybackState(playerState: PlayerState) {
        val playingMediaItem = playerState.mediaItem
        if (playingMediaItem == null || playingMediaItem.guid != mediaItem.guid) {
            NarrativeImportance.isNIUsedInThisSMIL = false
            playbackController.prepare(mediaItem)
        } else {
            when (playerState.playState) {
                PlayState.NULL -> {}
                PlayState.PREPARING -> {
                    NarrativeImportance.isNIUsedInThisSMIL = false
                    findViewById<View>(R.id.seekBarNI).visibility = INVISIBLE
                    findViewById<View>(R.id.seekLabel).visibility = INVISIBLE
                    findViewById<View>(R.id.seekLabel2).visibility = INVISIBLE
                    Toast.makeText(this, "Preparing podcast files...", Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.VISIBLE
                    fab.visibility = INVISIBLE
                }
                PlayState.PAUSED, PlayState.PREPARED, PlayState.COMPLETED -> {
                    progressBar.visibility = View.GONE
                    fab.playState()
                    val niBeingUsed = NarrativeImportance.isNIUsedInThisSMIL
                    if (!niBeingUsed) {
                        val mode = INVISIBLE
                        findViewById<View>(R.id.seekBarNI).visibility = mode
                        findViewById<View>(R.id.seekLabel).visibility = mode
                        findViewById<View>(R.id.seekLabel2).visibility = mode
                    } else {
                        niSlider.visibility = VISIBLE
                        findViewById<View>(R.id.seekLabel).visibility = VISIBLE
                        findViewById<View>(R.id.seekLabel2).visibility = VISIBLE
                    }
                }
                PlayState.PREPARE_FAILED -> {
                    fab.failedState()
                    progressBar.visibility = View.GONE
                    findViewById<View>(R.id.seekBarNI).visibility = GONE
                    findViewById<View>(R.id.seekLabel).visibility = GONE
                    findViewById<View>(R.id.seekLabel2).visibility = GONE
                }
                PlayState.PLAYING -> {
                    fab.playingState()
                }
            }
        }
    }

    private fun FloatingActionButton.playingState() {
        backgroundTintList = ContextCompat.getColorStateList(
            applicationContext,
            R.color.colorAccent
        )
        setImageResource(android.R.drawable.ic_media_pause)
        isEnabled = true
        setOnClickListener { playbackController.pause() }
    }

    private fun FloatingActionButton.failedState() {
        setImageResource(android.R.drawable.ic_media_play)
        isEnabled = false
        visibility = View.VISIBLE
    }

    private fun FloatingActionButton.playState() {
        setImageResource(android.R.drawable.ic_media_play)
        visibility = View.VISIBLE
        bringToFront()
        //change play button colour when ready
        backgroundTintList =
            ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)
        isEnabled = true
        setOnClickListener { playbackController.play() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, Intent(this, ChannelActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        if (isFinishing) {
            playbackController.release()
        }
        super.onDestroy()
    }
}