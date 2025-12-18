package com.example.counturu.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

class CounterRepository(private val counterDao: CounterDao) {

    private val gson = Gson()

    val allCounters: Flow<List<Counter>> = counterDao.getAllCounters()
    val favoriteCounters: Flow<List<Counter>> = counterDao.getFavoriteCounters()
    val archivedCounters: Flow<List<Counter>> = counterDao.getArchivedCounters()
    val allCategories: Flow<List<String>> = counterDao.getAllCategories()

    suspend fun getCounterById(id: Long): Counter? {
        return counterDao.getCounterById(id)
    }

    suspend fun insertCounter(counter: Counter): Long {
        return counterDao.insertCounter(counter)
    }

    suspend fun updateCounter(counter: Counter) {
        counterDao.updateCounter(counter)
    }

    suspend fun deleteCounter(counter: Counter) {
        counterDao.deleteCounter(counter)
    }

    suspend fun toggleFavorite(counter: Counter) {
        counterDao.updateFavoriteStatus(counter.id, !counter.isFavorite)
    }

    suspend fun archiveCounter(counter: Counter) {
        counterDao.updateArchiveStatus(counter.id, true)
    }

    suspend fun unarchiveCounter(counter: Counter) {
        counterDao.updateArchiveStatus(counter.id, false)
    }

    fun searchCounters(query: String): Flow<List<Counter>> {
        return counterDao.searchCounters(query)
    }

    fun getCountersByCategory(category: String): Flow<List<Counter>> {
        return counterDao.getCountersByCategory(category)
    }

    suspend fun updateProgress(counter: Counter, newProgress: Int) {
        counterDao.updateProgress(counter.id, newProgress)
    }

    suspend fun addHistoryEntry(counter: Counter, action: String, value: Int) {
        val existingHistory = parseHistory(counter.history)
        val newEntry = CounterHistoryEntry(
            timestamp = System.currentTimeMillis(),
            action = action,
            value = value
        )
        val updatedHistory = existingHistory + newEntry
        val historyJson = gson.toJson(updatedHistory)
        counterDao.updateHistory(counter.id, historyJson)
    }

    fun parseHistory(historyJson: String?): List<CounterHistoryEntry> {
        if (historyJson.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<CounterHistoryEntry>>() {}.type
            gson.fromJson(historyJson, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

