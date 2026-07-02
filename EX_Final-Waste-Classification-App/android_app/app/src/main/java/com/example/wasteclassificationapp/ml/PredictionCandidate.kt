package com.example.wasteclassificationapp.ml

data class PredictionCandidate(
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val confidence: Float
)