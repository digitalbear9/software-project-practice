package com.example.wasteclassificationapp.ml

data class RecognitionResult(
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val suggestion: String,
    val confidence: Float
)