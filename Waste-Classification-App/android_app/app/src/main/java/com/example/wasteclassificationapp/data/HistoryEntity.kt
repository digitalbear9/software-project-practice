package com.example.wasteclassificationapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_records")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeText: String,
    val source: String,
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val confidence: Float,
    val suggestion: String,
    val modelName: String
)