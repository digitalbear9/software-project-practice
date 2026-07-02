package com.example.wasteclassificationapp.ml

data class ImageQualityResult(
    val brightness: Float = 0.0f,
    val contrast: Float = 0.0f,
    val sharpness: Float = 0.0f,
    val isTooDark: Boolean = false,
    val isTooBright: Boolean = false,
    val isLowContrast: Boolean = false,
    val isBlurry: Boolean = false,
    val qualityLevel: String = "未知",
    val suggestions: List<String> = emptyList()
)