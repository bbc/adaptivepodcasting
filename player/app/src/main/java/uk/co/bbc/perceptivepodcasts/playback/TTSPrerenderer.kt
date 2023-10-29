package uk.co.bbc.perceptivepodcasts.playback

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.io.File
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private class TTSPrerenderer(
    private val context: Context,
    private val utterance: String,
    private val id: String,
) {
    private var mts: TextToSpeech? = null

    private fun inInit(status: Int, continuation: Continuation<String?>) {
        val mts = this.mts

        if (mts != null && status == TextToSpeech.SUCCESS) {
            // Remove all non-alphanumeric characters from destination filepath
            val filename = id.replace("[^a-zA-Z0-9]".toRegex(), "") + ".wav"
            val destFile = File(context.cacheDir, filename)
            mts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {}
                override fun onDone(utteranceId: String) {
                    continuation.resume(destFile.path)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String) {
                    continuation.resume(null)
                }
            })
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, id)
            val enqueueResult = mts.synthesizeToFile(utterance, params, destFile, id)
            if (enqueueResult != TextToSpeech.SUCCESS) {
                continuation.resume(null)
            }
        } else {
            continuation.resume(null)
        }
    }

    suspend fun start(): String? {
        return suspendCoroutine { continuation  ->
            mts = TextToSpeech(context) { status: Int ->
                inInit(status, continuation)
            }
        }
    }
}

suspend fun renderTextToSpeechToFile(context: Context, utterance: String, id: String): String? {
    return TTSPrerenderer(context, utterance, id).start()
}