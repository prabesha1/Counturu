package com.example.counturu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.counturu.data.Counter
import com.example.counturu.data.CounterDatabase
import com.example.counturu.data.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CounterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CounterRepository

    val allCounters: StateFlow<List<Counter>>
    val favoriteCounters: StateFlow<List<Counter>>

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

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
    }

    fun insertCounter(counter: Counter) {
        viewModelScope.launch {
            repository.insertCounter(counter)
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
        }
    }

    fun toggleFavorite(counter: Counter) {
        viewModelScope.launch {
            repository.toggleFavorite(counter)
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setCurrentUser(username: String) {
        _currentUser.value = username
    }

    fun signOut() {
        _currentUser.value = null
    }
}

