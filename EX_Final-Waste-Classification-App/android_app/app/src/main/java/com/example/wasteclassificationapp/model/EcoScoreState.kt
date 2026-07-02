package com.example.wasteclassificationapp.model

data class EcoScoreState(
    val score: Int = 0,
    val checkInDays: Int = 0,
    val lastCheckInDate: String = ""
)