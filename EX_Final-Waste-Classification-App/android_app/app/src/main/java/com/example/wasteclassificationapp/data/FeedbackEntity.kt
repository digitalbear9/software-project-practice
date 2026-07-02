package com.example.wasteclassificationapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback_records")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeText: String,
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val confidence: Float,
    val isCorrect: Boolean,
    val modelName: String
)