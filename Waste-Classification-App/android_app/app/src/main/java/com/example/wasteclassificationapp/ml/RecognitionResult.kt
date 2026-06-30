package com.example.wasteclassificationapp.ml

data class RecognitionResult(
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val suggestion: String,
    val confidence: Float,
    val topCandidates: List<PredictionCandidate> = emptyList(),
    val top2Gap: Float = 1.0f,
    val isUncertain: Boolean = false,
    val uncertaintyReason: String = "",
    val imageQuality: ImageQualityResult = ImageQualityResult()
)