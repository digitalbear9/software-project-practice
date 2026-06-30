package com.example.wasteclassificationapp.model

data class DetectedWasteObject(
    val index: Int,
    val boundingBoxText: String,
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val confidence: Float,
    val suggestion: String,
    val topCandidatesText: String,
    val isUncertain: Boolean,
    val uncertaintyReason: String
)