package com.example.wasteclassificationapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {

    @Query("SELECT * FROM feedback_records ORDER BY id DESC")
    fun getAllFeedback(): Flow<List<FeedbackEntity>>

    @Insert
    suspend fun insertFeedback(record: FeedbackEntity)

    @Query("DELETE FROM feedback_records")
    suspend fun clearFeedback()
}