package com.example.wasteclassificationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.wasteclassificationapp.data.FeedbackEntity
import com.example.wasteclassificationapp.data.HistoryEntity
import com.example.wasteclassificationapp.ml.ImageClassifier
import com.example.wasteclassificationapp.ml.RecognitionResult
import com.example.wasteclassificationapp.ui.CameraScreen
import com.example.wasteclassificationapp.ui.HistoryScreen
import com.example.wasteclassificationapp.ui.HomeScreen
import com.example.wasteclassificationapp.ui.KnowledgeScreen
import com.example.wasteclassificationapp.ui.ModelSettingScreen
import com.example.wasteclassificationapp.ui.ResultScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.wasteclassificationapp.ui.AssistantScreen
import com.example.wasteclassificationapp.ui.RealTimeScreen
import com.example.wasteclassificationapp.ui.FeedbackScreen
import com.example.wasteclassificationapp.ui.StatisticsScreen
class MainActivity : ComponentActivity() {

    private lateinit var classifier: ImageClassifier

    private var currentScreen by mutableStateOf(AppScreen.HOME)
    private var latestResult by mutableStateOf<RecognitionResult?>(null)

    private var historyList by mutableStateOf<List<HistoryEntity>>(emptyList())
    private var feedbackList by mutableStateOf<List<FeedbackEntity>>(emptyList())

    private var currentModelFileName by mutableStateOf(
        "waste_classification_mobilenetv2_v1_float32.tflite"
    )

    private var currentModelName by mutableStateOf("Float32")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifier = ImageClassifier(this, currentModelFileName)

        setContent {
            MaterialTheme {
                Surface {
                    when (currentScreen) {
                        AppScreen.HOME -> {
                            HomeScreen(
                                onStartRecognize = {
                                    currentScreen = AppScreen.CAMERA
                                },
                                onOpenRealTime = {
                                    currentScreen = AppScreen.REAL_TIME
                                },
                                onOpenHistory = {
                                    currentScreen = AppScreen.HISTORY
                                },
                                onOpenStatistics = {
                                    currentScreen = AppScreen.STATISTICS
                                },
                                onOpenKnowledge = {
                                    currentScreen = AppScreen.KNOWLEDGE
                                },
                                onOpenFeedback = {
                                    currentScreen = AppScreen.FEEDBACK
                                },
                                onOpenModelSetting = {
                                    currentScreen = AppScreen.MODEL_SETTING
                                },
                                onOpenAssistant = {
                                    currentScreen = AppScreen.ASSISTANT
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

                        AppScreen.STATISTICS -> {
                            StatisticsScreen(
                                historyList = historyList,
                                onBackHome = {
                                    currentScreen = AppScreen.HOME
                                }
                            )
                        }

                        AppScreen.FEEDBACK -> {
                            FeedbackScreen(
                                feedbackList = feedbackList,
                                onClearFeedback = {
                                    feedbackList = emptyList()
                                },
                                onExportFeedback = {
                                    exportFeedbackCsv(feedbackList)
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

                        AppScreen.MODEL_SETTING -> {
                            ModelSettingScreen(
                                currentModelName = currentModelName,
                                onUseFloat32Model = {
                                    switchModel(
                                        modelFileName = "waste_classification_mobilenetv2_v1_float32.tflite",
                                        modelName = "Float32"
                                    )
                                },
                                onUseDynamicRangeModel = {
                                    switchModel(
                                        modelFileName = "waste_classification_mobilenetv2_v1_dynamic_range.tflite",
                                        modelName = "Dynamic Range"
                                    )
                                },
                                onBackHome = {
                                    currentScreen = AppScreen.HOME
                                }
                            )
                        }

                        AppScreen.ASSISTANT -> {
                            AssistantScreen(
                                onBackHome = {
                                    currentScreen = AppScreen.HOME
                                }
                            )
                        }

                        AppScreen.REAL_TIME -> {
                            RealTimeScreen(
                                lifecycleOwner = this@MainActivity,
                                onAnalyzeBitmap = { bitmap ->
                                    classifier.classify(bitmap)
                                },
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

    private fun switchModel(
        modelFileName: String,
        modelName: String
    ) {
        if (::classifier.isInitialized) {
            classifier.close()
        }

        currentModelFileName = modelFileName
        currentModelName = modelName

        classifier = ImageClassifier(this, currentModelFileName)
    }

    private fun addHistory(
        result: RecognitionResult,
        source: String
    ) {
        val record = HistoryEntity(
            timeText = getCurrentTimeText(),
            source = source,
            label = result.label,
            labelCn = result.labelCn,
            wasteCategory = result.wasteCategory,
            confidence = result.confidence,
            suggestion = result.suggestion,
            modelName = currentModelName
        )

        historyList = listOf(record) + historyList
    }

    private fun addFeedback(
        result: RecognitionResult,
        isCorrect: Boolean
    ) {
        val record = FeedbackEntity(
            timeText = getCurrentTimeText(),
            label = result.label,
            labelCn = result.labelCn,
            wasteCategory = result.wasteCategory,
            confidence = result.confidence,
            isCorrect = isCorrect,
            modelName = currentModelName
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

        if (::classifier.isInitialized) {
            classifier.close()
        }
    }

    private fun exportFeedbackCsv(feedbackList: List<FeedbackEntity>) {
        val csvBuilder = StringBuilder()

        csvBuilder.append("time,label,labelCn,wasteCategory,confidence,isCorrect,modelName\n")

        feedbackList.forEach { record ->
            csvBuilder.append(record.timeText).append(",")
            csvBuilder.append(record.label).append(",")
            csvBuilder.append(record.labelCn).append(",")
            csvBuilder.append(record.wasteCategory).append(",")
            csvBuilder.append(record.confidence).append(",")
            csvBuilder.append(record.isCorrect).append(",")
            csvBuilder.append(record.modelName).append("\n")
        }

        val sendIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            type = "text/csv"
            putExtra(android.content.Intent.EXTRA_TEXT, csvBuilder.toString())
        }

        val shareIntent = android.content.Intent.createChooser(
            sendIntent,
            "导出反馈样本"
        )

        startActivity(shareIntent)
    }
}

enum class AppScreen {
    HOME,
    CAMERA,
    REAL_TIME,
    RESULT,
    HISTORY,
    STATISTICS,
    KNOWLEDGE,
    FEEDBACK,
    MODEL_SETTING,
    ASSISTANT
}