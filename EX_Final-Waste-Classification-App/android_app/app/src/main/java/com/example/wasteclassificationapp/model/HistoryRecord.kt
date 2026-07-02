package com.example.wasteclassificationapp.model

data class HistoryRecord(
    val timeText: String,
    val source: String,
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val confidence: Float,
    val suggestion: String
)