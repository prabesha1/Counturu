package com.example.counturu.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.counturu.data.Counter
import com.example.counturu.data.CounterDatabase
import com.example.counturu.data.CounterHistoryEntry
import com.example.counturu.data.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CounterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CounterRepository
    private val context = application.applicationContext

    // Sound pool for audio effects
    private var soundPool: SoundPool? = null

    val allCounters: StateFlow<List<Counter>>
    val favoriteCounters: StateFlow<List<Counter>>
    val archivedCounters: StateFlow<List<Counter>>
    val allCategories: StateFlow<List<String>>

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        val counterDao = CounterDatabase.getDatabase(application).counterDao()
        repository = CounterRepository(counterDao)

        allCounters = repository.allCounters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        favoriteCounters = repository.favoriteCounters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        archivedCounters = repository.archivedCounters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allCategories = repository.allCategories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        initializeSoundPool()
    }

    private fun initializeSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    fun insertCounter(counter: Counter) {
        viewModelScope.launch {
            repository.insertCounter(counter)
            playHapticFeedback()
        }
    }

    fun updateCounter(counter: Counter) {
        viewModelScope.launch {
            repository.updateCounter(counter)
        }
    }

    fun deleteCounter(counter: Counter) {
        viewModelScope.launch {
            repository.deleteCounter(counter)
            playHapticFeedback()
        }
    }

    fun toggleFavorite(counter: Counter) {
        viewModelScope.launch {
            repository.toggleFavorite(counter)
            playHapticFeedback()
        }
    }

    fun archiveCounter(counter: Counter) {
        viewModelScope.launch {
            repository.archiveCounter(counter)
            playHapticFeedback()
        }
    }

    fun unarchiveCounter(counter: Counter) {
        viewModelScope.launch {
            repository.unarchiveCounter(counter)
        }
    }

    fun updateProgress(counter: Counter, newProgress: Int) {
        viewModelScope.launch {
            repository.updateProgress(counter, newProgress)
            repository.addHistoryEntry(counter, "progress", newProgress)
            playHapticFeedback()
        }
    }

    fun incrementProgress(counter: Counter) {
        val newProgress = counter.currentProgress + 1
        updateProgress(counter, newProgress)
    }

    fun decrementProgress(counter: Counter) {
        if (counter.currentProgress > 0) {
            val newProgress = counter.currentProgress - 1
            updateProgress(counter, newProgress)
        }
    }

    fun getCounterHistory(counter: Counter): List<CounterHistoryEntry> {
        return repository.parseHistory(counter.history)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleHapticFeedback() {
        _hapticEnabled.value = !_hapticEnabled.value
    }

    fun toggleSoundEffects() {
        _soundEnabled.value = !_soundEnabled.value
    }

    fun setCurrentUser(username: String) {
        _currentUser.value = username
    }

    fun signOut() {
        _currentUser.value = null
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            kotlinx.coroutines.delay(500)
            _isRefreshing.value = false
        }
    }

    private fun playHapticFeedback() {
        if (!_hapticEnabled.value) return

        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } catch (e: Exception) {
            // Ignore vibration errors
        }
    }


    override fun onCleared() {
        super.onCleared()
        soundPool?.release()
        soundPool = null
    }
}

