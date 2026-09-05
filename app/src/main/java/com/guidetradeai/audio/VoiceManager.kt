package com.guidetradeai.audio
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Locale
import android.util.Base64
import java.io.File

class VoiceManager(
    private val context: Context,
    private val supabase: SupabaseClient
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var mediaPlayer: MediaPlayer? = null
    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device")
            return
        }
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onResults(results: android.os.Bundle?) {
                _isListening.value = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()
                if (!text.isNullOrBlank()) onResult(text) else onError("Could not understand. Please try again.")
            }
            override fun onError(error: Int) {
                _isListening.value = false
                val msg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Nothing heard. Tap to try again."
                    SpeechRecognizer.ERROR_NETWORK -> "Network error. Check connection."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Timed out. Tap to try again."
                    else -> "Speech error. Please try again."
                }
                onError(msg)
            }
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        }
        _isListening.value = true
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    private fun cleanTextForSpeech(text: String): String {
        return text
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("`{1,3}[^`]*`{1,3}"), "")
            .replace(Regex("#{1,6}\\s+"), "")
            .replace(Regex("\\[([^]]+)]\\([^)]+\\)"), "$1")
            .replace(Regex("\\n{2,}"), ". ")
            .trim()
    }

    suspend fun speak(
        text: String,
        onDone: suspend () -> Unit = {},
        onError: suspend () -> Unit = {}
    ) {
        val cleaned = cleanTextForSpeech(text)
        if (cleaned.isBlank()) { onDone(); return }
        Log.d("VoiceManager", "TTS request: $cleaned")

        try {
            val response = supabase.functions.invoke(
                "text-to-speech",
                buildJsonObject { put("text", JsonPrimitive(cleaned)) }
            )
            val data = response.bodyAsText()
            Log.d("VoiceManager", "TTS response: $data")
            val json = Json.parseToJsonElement(data).jsonObject

            if (json["error"]?.jsonPrimitive?.content == "VOICE_DISABLED") {
                Log.w("VoiceManager", "Voice disabled in settings")
                onDone(); return
            }

            val audioBase64 = json["audio"]?.jsonPrimitive?.content
                ?: run { 
                    Log.e("VoiceManager", "No audio in TTS response")
                    onError(); return 
                }

            val audioBytes = Base64.decode(audioBase64, Base64.DEFAULT)
            playAudio(audioBytes, onDone, onError)

        } catch (e: Exception) {
            Log.e("VoiceManager", "TTS failed", e)
            onError()
        }
    }

    private fun playAudio(
        audioBytes: ByteArray,
        onDone: suspend () -> Unit,
        onError: suspend () -> Unit
    ) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()

            val tempFile = File.createTempFile("tts_", ".mp3", context.cacheDir)
            tempFile.writeBytes(audioBytes)

            mediaPlayer?.apply {
                setDataSource(tempFile.absolutePath)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .build()
                )
                setOnCompletionListener {
                    tempFile.delete()
                    GlobalScope.launch(Dispatchers.Main) {
                        onDone()
                    }
                }
                setOnErrorListener { _, _, _ -> 
                    GlobalScope.launch(Dispatchers.Main) {
                        onError()
                    }
                    true 
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("VoiceManager", "MediaPlayer failed", e)
            GlobalScope.launch(Dispatchers.Main) {
                onError()
            }
        }
    }

    fun stopSpeaking() {
        try {
            mediaPlayer?.let { if (it.isPlaying) it.stop() }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("VoiceManager", "stopSpeaking failed", e)
        }
    }

    fun isSpeaking(): Boolean = mediaPlayer?.isPlaying == true

    fun destroy() {
        speechRecognizer?.destroy()
        mediaPlayer?.release()
    }
}
