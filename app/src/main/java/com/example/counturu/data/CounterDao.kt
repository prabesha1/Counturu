package com.example.counturu.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters WHERE isArchived = 0 ORDER BY targetDateTime ASC")
    fun getAllCounters(): Flow<List<Counter>>

    @Query("SELECT * FROM counters WHERE isFavorite = 1 AND isArchived = 0 ORDER BY targetDateTime ASC")
    fun getFavoriteCounters(): Flow<List<Counter>>

    @Query("SELECT * FROM counters WHERE isArchived = 1 ORDER BY targetDateTime DESC")
    fun getArchivedCounters(): Flow<List<Counter>>

    @Query("SELECT * FROM counters WHERE category = :category AND isArchived = 0 ORDER BY targetDateTime ASC")
    fun getCountersByCategory(category: String): Flow<List<Counter>>

    @Query("SELECT * FROM counters WHERE title LIKE '%' || :query || '%' AND isArchived = 0 ORDER BY targetDateTime ASC")
    fun searchCounters(query: String): Flow<List<Counter>>

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

    @Query("UPDATE counters SET isArchived = :isArchived WHERE id = :id")
    suspend fun updateArchiveStatus(id: Long, isArchived: Boolean)

    @Query("UPDATE counters SET currentProgress = :progress WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int)

    @Query("UPDATE counters SET history = :history WHERE id = :id")
    suspend fun updateHistory(id: Long, history: String?)

    @Query("SELECT DISTINCT category FROM counters WHERE category IS NOT NULL AND isArchived = 0")
    fun getAllCategories(): Flow<List<String>>
}

