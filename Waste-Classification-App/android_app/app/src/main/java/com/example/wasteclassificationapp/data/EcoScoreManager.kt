package com.example.wasteclassificationapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.wasteclassificationapp.model.EcoScoreState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.ecoScoreDataStore by preferencesDataStore(
    name = "eco_score"
)

class EcoScoreManager(
    private val context: Context
) {
    private val scoreKey = intPreferencesKey("score")
    private val checkInDaysKey = intPreferencesKey("check_in_days")
    private val lastCheckInDateKey = stringPreferencesKey("last_check_in_date")

    val ecoScoreFlow: Flow<EcoScoreState> = context.ecoScoreDataStore.data.map { preferences ->
        EcoScoreState(
            score = preferences[scoreKey] ?: 0,
            checkInDays = preferences[checkInDaysKey] ?: 0,
            lastCheckInDate = preferences[lastCheckInDateKey] ?: ""
        )
    }

    suspend fun checkInToday(): Boolean {
        val today = getTodayText()
        var success = false

        context.ecoScoreDataStore.edit { preferences ->
            val lastCheckInDate = preferences[lastCheckInDateKey] ?: ""

            if (lastCheckInDate != today) {
                val oldScore = preferences[scoreKey] ?: 0
                val oldCheckInDays = preferences[checkInDaysKey] ?: 0

                preferences[scoreKey] = oldScore + 2
                preferences[checkInDaysKey] = oldCheckInDays + 1
                preferences[lastCheckInDateKey] = today

                success = true
            }
        }

        return success
    }

    suspend fun addScore(value: Int) {
        context.ecoScoreDataStore.edit { preferences ->
            val oldScore = preferences[scoreKey] ?: 0
            preferences[scoreKey] = oldScore + value
        }
    }

    suspend fun resetScore() {
        context.ecoScoreDataStore.edit { preferences ->
            preferences[scoreKey] = 0
            preferences[checkInDaysKey] = 0
            preferences[lastCheckInDateKey] = ""
        }
    }

    fun getTodayText(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}