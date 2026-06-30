package com.example.wasteclassificationapp.model

data class FeedbackRecord(
    val timeText: String,
    val label: String,
    val labelCn: String,
    val confidence: Float,
    val isCorrect: Boolean
)