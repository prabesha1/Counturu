package com.example.counturu.data

import kotlinx.coroutines.flow.Flow

class CounterRepository(private val counterDao: CounterDao) {

    val allCounters: Flow<List<Counter>> = counterDao.getAllCounters()
    val favoriteCounters: Flow<List<Counter>> = counterDao.getFavoriteCounters()

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
}

