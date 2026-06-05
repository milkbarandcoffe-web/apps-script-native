package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WebAppDao {
    @Query("SELECT * FROM web_apps ORDER BY isPinned DESC, lastUsedTimestamp DESC, name ASC")
    fun getAllApps(): Flow<List<WebApp>>

    @Query("SELECT * FROM web_apps WHERE id = :id LIMIT 1")
    suspend fun getAppById(id: Long): WebApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: WebApp): Long

    @Update
    suspend fun updateApp(app: WebApp)

    @Delete
    suspend fun deleteApp(app: WebApp)
}
