package com.example.wasteclassificationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.wasteclassificationapp.ml.ImageClassifier
import com.example.wasteclassificationapp.ml.RecognitionResult
import com.example.wasteclassificationapp.model.FeedbackRecord
import com.example.wasteclassificationapp.model.HistoryRecord
import com.example.wasteclassificationapp.ui.CameraScreen
import com.example.wasteclassificationapp.ui.HistoryScreen
import com.example.wasteclassificationapp.ui.HomeScreen
import com.example.wasteclassificationapp.ui.KnowledgeScreen
import com.example.wasteclassificationapp.ui.ResultScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var classifier: ImageClassifier

    private var currentScreen by mutableStateOf(AppScreen.HOME)
    private var latestResult by mutableStateOf<RecognitionResult?>(null)

    private var historyList by mutableStateOf<List<HistoryRecord>>(emptyList())
    private var feedbackList by mutableStateOf<List<FeedbackRecord>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifier = ImageClassifier(this)

        setContent {
            MaterialTheme {
                Surface {
                    when (currentScreen) {
                        AppScreen.HOME -> {
                            HomeScreen(
                                onStartRecognize = {
                                    currentScreen = AppScreen.CAMERA
                                },
                                onOpenHistory = {
                                    currentScreen = AppScreen.HISTORY
                                },
                                onOpenKnowledge = {
                                    currentScreen = AppScreen.KNOWLEDGE
                                }
                            )
                        }

                        AppScreen.CAMERA -> {
                            CameraScreen(
                                onImageCaptured = { bitmap ->
                                    val result = classifier.classify(bitmap)

                                    latestResult = result

                                    addHistory(
                                        result = result,
                                        source = "图片识别"
                                    )

                                    currentScreen = AppScreen.RESULT
                                },
                                onBackHome = {
                                    currentScreen = AppScreen.HOME
                                }
                            )
                        }

                        AppScreen.RESULT -> {
                            ResultScreen(
                                result = latestResult,
                                onRetry = {
                                    currentScreen = AppScreen.CAMERA
                                },
                                onBackHome = {
                                    currentScreen = AppScreen.HOME
                                },
                                onFeedbackCorrect = {
                                    latestResult?.let { result ->
                                        addFeedback(
                                            result = result,
                                            isCorrect = true
                                        )
                                    }
                                },
                                onFeedbackWrong = {
                                    latestResult?.let { result ->
                                        addFeedback(
                                            result = result,
                                            isCorrect = false
                                        )
                                    }
                                }
                            )
                        }

                        AppScreen.HISTORY -> {
                            HistoryScreen(
                                historyList = historyList,
                                onClearHistory = {
                                    historyList = emptyList()
                                },
                                onBackHome = {
                                    currentScreen = AppScreen.HOME
                                }
                            )
                        }

                        AppScreen.KNOWLEDGE -> {
                            KnowledgeScreen(
                                onBackHome = {
                                    currentScreen = AppScreen.HOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun addHistory(
        result: RecognitionResult,
        source: String
    ) {
        val timeText = getCurrentTimeText()

        val record = HistoryRecord(
            timeText = timeText,
            source = source,
            label = result.label,
            labelCn = result.labelCn,
            wasteCategory = result.wasteCategory,
            confidence = result.confidence,
            suggestion = result.suggestion
        )

        historyList = listOf(record) + historyList
    }

    private fun addFeedback(
        result: RecognitionResult,
        isCorrect: Boolean
    ) {
        val timeText = getCurrentTimeText()

        val record = FeedbackRecord(
            timeText = timeText,
            label = result.label,
            labelCn = result.labelCn,
            confidence = result.confidence,
            isCorrect = isCorrect
        )

        feedbackList = listOf(record) + feedbackList
    }

    private fun getCurrentTimeText(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
    }
}

enum class AppScreen {
    HOME,
    CAMERA,
    RESULT,
    HISTORY,
    KNOWLEDGE
}