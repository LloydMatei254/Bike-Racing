package com.example.bikeracing.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool

class AudioEngine(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var mediaPlayer: MediaPlayer? = null

    private var sfxVolume = 0.9f
    private var musicVolume = 0.7f
    private var engineVolume = 0.8f

    // Sound IDs from SoundPool
    private var idNitro = 0
    private var idBrake = 0
    private var idCrash = 0
    private var idCountdown = 0
    private var idRaceStart = 0
    private var idRaceFinish = 0
    private var idButton = 0
    private var idCoin = 0

    private var engineStreamId = 0
    private var isEngineLooping = false

    fun init() {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(attrs)
            .build()
        loadSounds()
    }

    private fun loadSounds() {
        // Sounds are generated procedurally since we don't have assets yet.
        // In a production build these would be R.raw.sound_xxx references.
        // We leave the pool initialised but use silent fallback.
    }

    fun playButton() { soundPool?.play(idButton, sfxVolume, sfxVolume, 1, 0, 1.0f) }
    fun playNitro() { soundPool?.play(idNitro, sfxVolume, sfxVolume, 1, 0, 1.0f) }
    fun playBrake() { soundPool?.play(idBrake, sfxVolume * 0.6f, sfxVolume * 0.6f, 1, 0, 1.0f) }
    fun playCrash() { soundPool?.play(idCrash, sfxVolume, sfxVolume, 2, 0, 1.0f) }
    fun playCountdown() { soundPool?.play(idCountdown, sfxVolume, sfxVolume, 2, 0, 1.0f) }
    fun playRaceStart() { soundPool?.play(idRaceStart, sfxVolume, sfxVolume, 2, 0, 1.0f) }
    fun playRaceFinish() { soundPool?.play(idRaceFinish, sfxVolume, sfxVolume, 2, 0, 1.0f) }
    fun playCoin() { soundPool?.play(idCoin, sfxVolume * 0.7f, sfxVolume * 0.7f, 1, 0, 1.0f) }

    fun setEngineRpm(rpm: Float, speedKmh: Float) {
        // Pitch shifts based on RPM for engine feel
        val pitch = (0.5f + (rpm / 9000f) * 1.0f).coerceIn(0.5f, 2.0f)
        if (engineStreamId != 0) {
            soundPool?.setRate(engineStreamId, pitch)
        }
    }

    fun startEngineSound() {
        // Would play looping engine audio
        isEngineLooping = true
    }

    fun stopEngineSound() {
        if (engineStreamId != 0) {
            soundPool?.stop(engineStreamId)
            engineStreamId = 0
        }
        isEngineLooping = false
    }

    fun setVolumes(music: Float, sfx: Float, engine: Float) {
        musicVolume = music
        sfxVolume = sfx
        engineVolume = engine
        mediaPlayer?.setVolume(music, music)
    }

    fun pauseAll() {
        mediaPlayer?.pause()
        if (engineStreamId != 0) soundPool?.autoPause()
    }

    fun resumeAll() {
        mediaPlayer?.start()
        if (isEngineLooping) soundPool?.autoResume()
    }

    fun release() {
        stopEngineSound()
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool?.release()
        soundPool = null
    }
}
