package com.guidetradeai.voice

import android.media.MediaPlayer
import android.util.Base64
import java.io.File

class AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var currentFile: File? = null

    fun playBase64Audio(base64Audio: String, onCompletion: () -> Unit = {}) {
        try {
            val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
            val tempFile = File.createTempFile("audio_", ".mp3", null)
            tempFile.writeBytes(audioBytes)

            release()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepareAsync()
                setOnPreparedListener { mp -> mp.start() }
                setOnCompletionListener {
                    onCompletion()
                    release()
                }
            }
            currentFile = tempFile
        } catch (e: Exception) {
            onCompletion()
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (e: Exception) {
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
        } catch (e: Exception) {
        } finally {
            release()
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentFile?.delete()
        currentFile = null
    }

    val isPlaying: Boolean
        get() = try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
}
