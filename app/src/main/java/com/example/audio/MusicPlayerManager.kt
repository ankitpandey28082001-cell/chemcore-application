package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import kotlin.math.sin

class MusicPlayerManager(private val context: Context) {

    private val tag = "MusicPlayerManager"
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0)
    val duration = _duration.asStateFlow()

    private val _trackTitle = MutableStateFlow("Ready to Play")
    val trackTitle = _trackTitle.asStateFlow()

    fun playFile(file: File, title: String = file.name) {
        try {
            stop()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.fromFile(file))
                isLooping = true
                setOnPreparedListener { mp ->
                    _duration.value = mp.duration
                    _trackTitle.value = title
                    mp.start()
                    _isPlaying.value = true
                    startProgressTracker()
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(tag, "MediaPlayer error: $what, $extra")
                    _isPlaying.value = false
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to play file", e)
        }
    }

    /**
     * Synthesizes and plays a relaxing, ambient 432Hz scientific focus chord (lo-fi study preview tone)
     * using PCM AudioTrack into a valid WAV file, guaranteeing real musical sound even offline.
     */
    fun playSynthesizedStudyTone(title: String = "432Hz Quantum Harmonic Focus"): File {
        val sampleRate = 44100
        val durationSeconds = 15
        val numSamples = sampleRate * durationSeconds
        val pcmData = ShortArray(numSamples)

        // Generate layered chords: Root (216 Hz), Fifth (324 Hz), Octave (432 Hz)
        val f1 = 216.0
        val f2 = 324.0
        val f3 = 432.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Gentle amplitude envelope
            val envelope = sin(Math.PI * (i.toDouble() / numSamples))
            val s1 = sin(2 * Math.PI * f1 * t) * 0.4
            val s2 = sin(2 * Math.PI * f2 * t) * 0.3
            val s3 = sin(2 * Math.PI * f3 * t) * 0.3
            val sample = ((s1 + s2 + s3) * envelope * Short.MAX_VALUE * 0.6).toInt()
            pcmData[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        val wavFile = File(context.cacheDir, "ambient_study_focus.wav")
        FileOutputStream(wavFile).use { out ->
            writeWavHeader(out, numSamples, sampleRate)
            for (s in pcmData) {
                out.write(s.toInt() and 0xFF)
                out.write((s.toInt() shr 8) and 0xFF)
            }
        }

        playFile(wavFile, title)
        return wavFile
    }

    fun togglePlayPause() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.pause()
                _isPlaying.value = false
            } else {
                mp.start()
                _isPlaying.value = true
                startProgressTracker()
            }
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun stop() {
        progressJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && mediaPlayer != null && _isPlaying.value) {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            _currentPosition.value = mp.currentPosition
                        }
                    }
                } catch (e: Exception) {
                    // Ignore state errors during release
                }
                delay(200)
            }
        }
    }

    private fun writeWavHeader(out: FileOutputStream, totalAudioLen: Int, sampleRate: Int) {
        val totalDataLen = totalAudioLen * 2 + 36
        val channels = 1
        val byteRate = sampleRate * channels * 2

        val header = ByteArray(44).apply {
            this[0] = 'R'.code.toByte(); this[1] = 'I'.code.toByte(); this[2] = 'F'.code.toByte(); this[3] = 'F'.code.toByte()
            this[4] = (totalDataLen and 0xff).toByte()
            this[5] = ((totalDataLen shr 8) and 0xff).toByte()
            this[6] = ((totalDataLen shr 16) and 0xff).toByte()
            this[7] = ((totalDataLen shr 24) and 0xff).toByte()
            this[8] = 'W'.code.toByte(); this[9] = 'A'.code.toByte(); this[10] = 'V'.code.toByte(); this[11] = 'E'.code.toByte()
            this[12] = 'f'.code.toByte(); this[13] = 'm'.code.toByte(); this[14] = 't'.code.toByte(); this[15] = ' '.code.toByte()
            this[16] = 16; this[17] = 0; this[18] = 0; this[19] = 0 // subchunk1 size
            this[20] = 1; this[21] = 0 // PCM = 1
            this[22] = channels.toByte(); this[23] = 0
            this[24] = (sampleRate and 0xff).toByte()
            this[25] = ((sampleRate shr 8) and 0xff).toByte()
            this[26] = ((sampleRate shr 16) and 0xff).toByte()
            this[27] = ((sampleRate shr 24) and 0xff).toByte()
            this[28] = (byteRate and 0xff).toByte()
            this[29] = ((byteRate shr 8) and 0xff).toByte()
            this[30] = ((byteRate shr 16) and 0xff).toByte()
            this[31] = ((byteRate shr 24) and 0xff).toByte()
            this[32] = 2; this[33] = 0 // block align
            this[34] = 16; this[35] = 0 // bits per sample
            this[36] = 'd'.code.toByte(); this[37] = 'a'.code.toByte(); this[38] = 't'.code.toByte(); this[39] = 'a'.code.toByte()
            val audioBytes = totalAudioLen * 2
            this[40] = (audioBytes and 0xff).toByte()
            this[41] = ((audioBytes shr 8) and 0xff).toByte()
            this[42] = ((audioBytes shr 16) and 0xff).toByte()
            this[43] = ((audioBytes shr 24) and 0xff).toByte()
        }
        out.write(header)
    }

    fun release() {
        stop()
        scope.cancel()
    }
}
