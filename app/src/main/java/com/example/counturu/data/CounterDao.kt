package com.example.counturu.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters ORDER BY targetDateTime ASC")
    fun getAllCounters(): Flow<List<Counter>>

    @Query("SELECT * FROM counters WHERE isFavorite = 1 ORDER BY targetDateTime ASC")
    fun getFavoriteCounters(): Flow<List<Counter>>

    @Query("SELECT * FROM counters WHERE id = :id")
    suspend fun getCounterById(id: Long): Counter?

    @Insert
    suspend fun insertCounter(counter: Counter): Long

    @Update
    suspend fun updateCounter(counter: Counter)

    @Delete
    suspend fun deleteCounter(counter: Counter)

    @Query("UPDATE counters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
}

