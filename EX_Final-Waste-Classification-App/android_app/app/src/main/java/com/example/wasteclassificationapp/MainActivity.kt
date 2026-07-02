package com.example.wasteclassificationapp

import android.Manifest
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.wasteclassificationapp.data.EcoScoreManager
import com.example.wasteclassificationapp.data.FeedbackEntity
import com.example.wasteclassificationapp.data.HistoryEntity
import com.example.wasteclassificationapp.data.ReminderScheduler
import com.example.wasteclassificationapp.ml.ImageClassifier
import com.example.wasteclassificationapp.ml.RecognitionResult
import com.example.wasteclassificationapp.model.EcoScoreState
import com.example.wasteclassificationapp.model.OptimizationSample
import com.example.wasteclassificationapp.model.OptimizationSampleRepository
import com.example.wasteclassificationapp.ui.AssistantScreen
import com.example.wasteclassificationapp.ui.BarcodeScanScreen
import com.example.wasteclassificationapp.ui.CameraScreen
import com.example.wasteclassificationapp.ui.CampusRuleScreen
import com.example.wasteclassificationapp.ui.DisposalPointScreen
import com.example.wasteclassificationapp.ui.EcoScoreScreen
import com.example.wasteclassificationapp.ui.FeedbackScreen
import com.example.wasteclassificationapp.ui.HistoryScreen
import com.example.wasteclassificationapp.ui.KnowledgeScreen
import com.example.wasteclassificationapp.ui.LearningHubScreen
import com.example.wasteclassificationapp.ui.MainHomeScreen
import com.example.wasteclassificationapp.ui.MistakeScreen
import com.example.wasteclassificationapp.ui.ModelEvaluationScreen
import com.example.wasteclassificationapp.ui.ModelHubScreen
import com.example.wasteclassificationapp.ui.ModelSettingScreen
import com.example.wasteclassificationapp.ui.MultiObjectDetectionScreen
import com.example.wasteclassificationapp.ui.OptimizationSampleScreen
import com.example.wasteclassificationapp.ui.ProfileHubScreen
import com.example.wasteclassificationapp.ui.QuizScreen
import com.example.wasteclassificationapp.ui.RealTimeScreen
import com.example.wasteclassificationapp.ui.RecognitionHubScreen
import com.example.wasteclassificationapp.ui.ReminderSettingScreen
import com.example.wasteclassificationapp.ui.ResultScreen
import com.example.wasteclassificationapp.ui.SearchScreen
import com.example.wasteclassificationapp.ui.SpecialWasteScreen
import com.example.wasteclassificationapp.ui.StatisticsScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var classifier: ImageClassifier

    private var currentScreen by mutableStateOf(AppScreen.HOME)
    private var parentHubScreen by mutableStateOf(AppScreen.HOME)
    private var latestResult by mutableStateOf<RecognitionResult?>(null)

    private var historyList by mutableStateOf<List<HistoryEntity>>(emptyList())
    private var feedbackList by mutableStateOf<List<FeedbackEntity>>(emptyList())

    private var currentModelFileName by mutableStateOf(
        "waste_classification_mobilenetv2_v1_float32.tflite"
    )

    private var currentModelName by mutableStateOf("Float32")

    private var pendingNotificationAction: (() -> Unit)? = null

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                pendingNotificationAction?.invoke()
            }

            pendingNotificationAction = null
        }

    private fun runWithNotificationPermission(action: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            action()
            return
        }

        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            action()
        } else {
            pendingNotificationAction = action
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }

    private fun isHubScreen(screen: AppScreen): Boolean {
        return screen == AppScreen.HOME ||
            screen == AppScreen.RECOGNITION_HUB ||
            screen == AppScreen.LEARNING_HUB ||
            screen == AppScreen.MODEL_HUB ||
            screen == AppScreen.PROFILE_HUB
    }

    private fun navigateToHub(screen: AppScreen) {
        currentScreen = screen
        parentHubScreen = screen
    }

    private fun navigateToDetail(screen: AppScreen) {
        if (isHubScreen(currentScreen)) {
            parentHubScreen = currentScreen
        }

        currentScreen = screen
    }

    private fun goBackToParentHub() {
        currentScreen = parentHubScreen
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifier = ImageClassifier(this, currentModelFileName)

        setContent {
            val ecoScoreManager = remember {
                EcoScoreManager(this@MainActivity)
            }

            val ecoScoreState = ecoScoreManager.ecoScoreFlow.collectAsState(
                initial = EcoScoreState()
            )

            val coroutineScope = rememberCoroutineScope()

            val todayText = ecoScoreManager.getTodayText()

            val optimizationSamples = OptimizationSampleRepository.buildSamples(
                historyList = historyList,
                feedbackList = feedbackList
            )

            BackHandler(enabled = currentScreen != AppScreen.HOME) {
                if (isHubScreen(currentScreen)) {
                    navigateToHub(AppScreen.HOME)
                } else {
                    goBackToParentHub()
                }
            }

            @Composable
            fun CurrentScreenContent() {
                Surface {
                    when (currentScreen) {
                        AppScreen.HOME -> {
                            MainHomeScreen(
                                ecoScore = ecoScoreState.value.score,
                                historyCount = historyList.size,
                                feedbackCount = feedbackList.size,
                                optimizationSampleCount = optimizationSamples.size,
                                onStartRecognize = {
                                    navigateToDetail(AppScreen.CAMERA)
                                },
                                onOpenRealTime = {
                                    navigateToDetail(AppScreen.REAL_TIME)
                                },
                                onOpenSearch = {
                                    navigateToDetail(AppScreen.SEARCH)
                                },
                                onOpenDisposalPoint = {
                                    navigateToDetail(AppScreen.DISPOSAL_POINT)
                                },
                                onOpenModelEvaluation = {
                                    navigateToDetail(AppScreen.MODEL_EVALUATION)
                                }
                            )
                        }

                        AppScreen.RECOGNITION_HUB -> {
                            RecognitionHubScreen(
                                onStartRecognize = {
                                    navigateToDetail(AppScreen.CAMERA)
                                },
                                onOpenRealTime = {
                                    navigateToDetail(AppScreen.REAL_TIME)
                                },
                                onOpenMultiObject = {
                                    navigateToDetail(AppScreen.MULTI_OBJECT)
                                },
                                onOpenBarcodeScan = {
                                    navigateToDetail(AppScreen.BARCODE_SCAN)
                                }
                            )
                        }

                        AppScreen.LEARNING_HUB -> {
                            LearningHubScreen(
                                onOpenSearch = {
                                    navigateToDetail(AppScreen.SEARCH)
                                },
                                onOpenMistake = {
                                    navigateToDetail(AppScreen.MISTAKE)
                                },
                                onOpenKnowledge = {
                                    navigateToDetail(AppScreen.KNOWLEDGE)
                                },
                                onOpenDisposalPoint = {
                                    navigateToDetail(AppScreen.DISPOSAL_POINT)
                                },
                                onOpenQuiz = {
                                    navigateToDetail(AppScreen.QUIZ)
                                },
                                onOpenSpecialWaste = {
                                    navigateToDetail(AppScreen.SPECIAL_WASTE)
                                },
                                onOpenCampusRule = {
                                    navigateToDetail(AppScreen.CAMPUS_RULE)
                                }
                            )
                        }

                        AppScreen.MODEL_HUB -> {
                            ModelHubScreen(
                                onOpenModelSetting = {
                                    navigateToDetail(AppScreen.MODEL_SETTING)
                                },
                                onOpenModelEvaluation = {
                                    navigateToDetail(AppScreen.MODEL_EVALUATION)
                                },
                                onOpenOptimizationSamples = {
                                    navigateToDetail(AppScreen.OPTIMIZATION_SAMPLES)
                                },
                                onOpenStatistics = {
                                    navigateToDetail(AppScreen.STATISTICS)
                                },
                                onOpenFeedback = {
                                    navigateToDetail(AppScreen.FEEDBACK)
                                }
                            )
                        }

                        AppScreen.PROFILE_HUB -> {
                            ProfileHubScreen(
                                onOpenEcoScore = {
                                    navigateToDetail(AppScreen.ECO_SCORE)
                                },
                                onOpenReminderSetting = {
                                    navigateToDetail(AppScreen.REMINDER_SETTING)
                                },
                                onOpenHistory = {
                                    navigateToDetail(AppScreen.HISTORY)
                                },
                                onOpenAssistant = {
                                    navigateToDetail(AppScreen.ASSISTANT)
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

                                    coroutineScope.launch {
                                        ecoScoreManager.addScore(1)
                                    }

                                    currentScreen = AppScreen.RESULT
                                },
                                onBackHome = {
                                    goBackToParentHub()
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
                                    goBackToParentHub()
                                },
                                onFeedbackCorrect = {
                                    latestResult?.let { result ->
                                        addFeedback(
                                            result = result,
                                            isCorrect = true
                                        )

                                        coroutineScope.launch {
                                            ecoScoreManager.addScore(1)
                                        }
                                    }
                                },
                                onFeedbackWrong = {
                                    latestResult?.let { result ->
                                        addFeedback(
                                            result = result,
                                            isCorrect = false
                                        )

                                        coroutineScope.launch {
                                            ecoScoreManager.addScore(1)
                                        }
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
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.STATISTICS -> {
                            StatisticsScreen(
                                historyList = historyList,
                                onBackHome = {
                                    goBackToParentHub()
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
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.KNOWLEDGE -> {
                            KnowledgeScreen(
                                onBackHome = {
                                    goBackToParentHub()
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
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.MODEL_EVALUATION -> {
                            ModelEvaluationScreen(
                                context = this@MainActivity,
                                currentModelName = currentModelName,
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.ASSISTANT -> {
                            AssistantScreen(
                                onBackHome = {
                                    goBackToParentHub()
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
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.MULTI_OBJECT -> {
                            MultiObjectDetectionScreen(
                                onClassifyBitmap = { bitmap ->
                                    classifier.classify(bitmap)
                                },
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.BARCODE_SCAN -> {
                            BarcodeScanScreen(
                                lifecycleOwner = this@MainActivity,
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.SEARCH -> {
                            SearchScreen(
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.MISTAKE -> {
                            MistakeScreen(
                                feedbackList = feedbackList,
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.DISPOSAL_POINT -> {
                            DisposalPointScreen(
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.QUIZ -> {
                            QuizScreen(
                                onQuizFinished = {
                                    coroutineScope.launch {
                                        ecoScoreManager.addScore(3)
                                    }
                                },
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.SPECIAL_WASTE -> {
                            SpecialWasteScreen(
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.CAMPUS_RULE -> {
                            CampusRuleScreen(
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.ECO_SCORE -> {
                            EcoScoreScreen(
                                ecoScoreState = ecoScoreState.value,
                                todayText = todayText,
                                onCheckIn = {
                                    coroutineScope.launch {
                                        ecoScoreManager.checkInToday()
                                    }
                                },
                                onResetScore = {
                                    coroutineScope.launch {
                                        ecoScoreManager.resetScore()
                                    }
                                },
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.REMINDER_SETTING -> {
                            ReminderSettingScreen(
                                onEnableReminder = {
                                    runWithNotificationPermission {
                                        ReminderScheduler.scheduleDailyReminder(this@MainActivity)
                                    }
                                },
                                onDisableReminder = {
                                    ReminderScheduler.cancelDailyReminder(this@MainActivity)
                                },
                                onSendTestReminder = {
                                    runWithNotificationPermission {
                                        ReminderScheduler.sendTestReminder(this@MainActivity)
                                    }
                                },
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }

                        AppScreen.OPTIMIZATION_SAMPLES -> {
                            OptimizationSampleScreen(
                                samples = optimizationSamples,
                                onExportSamples = {
                                    exportOptimizationSamplesCsv(optimizationSamples)
                                },
                                onBackHome = {
                                    goBackToParentHub()
                                }
                            )
                        }
                    }
                }
            }

            MaterialTheme {
                if (isHubScreen(currentScreen)) {
                    Scaffold(
                        bottomBar = {
                            AppBottomNavigationBar(
                                currentScreen = currentScreen,
                                onScreenSelected = { screen ->
                                    navigateToHub(screen)
                                }
                            )
                        }
                    ) { innerPadding ->
                        Surface(
                            modifier = Modifier.padding(
                                bottom = innerPadding.calculateBottomPadding()
                            )
                        ) {
                            CurrentScreenContent()
                        }
                    }
                } else {
                    CurrentScreenContent()
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
        val topCandidatesText = result.topCandidates.joinToString(" | ") { candidate ->
            "${candidate.labelCn}(${candidate.label}):${String.format("%.2f", candidate.confidence * 100)}%"
        }

        val record = HistoryEntity(
            timeText = getCurrentTimeText(),
            source = source,
            label = result.label,
            labelCn = result.labelCn,
            wasteCategory = result.wasteCategory,
            confidence = result.confidence,
            suggestion = result.suggestion,
            modelName = currentModelName,
            topCandidatesText = topCandidatesText,
            top2Gap = result.top2Gap,
            isUncertain = result.isUncertain,
            uncertaintyReason = result.uncertaintyReason
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

    private fun exportOptimizationSamplesCsv(
        samples: List<OptimizationSample>
    ) {
        val csvText = OptimizationSampleRepository.buildCsv(samples)

        val sendIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            type = "text/csv"
            putExtra(android.content.Intent.EXTRA_TEXT, csvText)
        }

        val shareIntent = android.content.Intent.createChooser(
            sendIntent,
            "导出模型优化样本"
        )

        startActivity(shareIntent)
    }
}

enum class AppScreen {
    HOME,
    RECOGNITION_HUB,
    LEARNING_HUB,
    MODEL_HUB,
    PROFILE_HUB,
    CAMERA,
    REAL_TIME,
    MULTI_OBJECT,
    BARCODE_SCAN,
    SEARCH,
    MISTAKE,
    DISPOSAL_POINT,
    QUIZ,
    SPECIAL_WASTE,
    CAMPUS_RULE,
    ECO_SCORE,
    REMINDER_SETTING,
    RESULT,
    HISTORY,
    STATISTICS,
    KNOWLEDGE,
    FEEDBACK,
    MODEL_SETTING,
    MODEL_EVALUATION,
    OPTIMIZATION_SAMPLES,
    ASSISTANT
}

private data class BottomNavigationItem(
    val label: String,
    val screen: AppScreen
)

private val bottomNavigationItems = listOf(
    BottomNavigationItem(
        label = "首页",
        screen = AppScreen.HOME
    ),
    BottomNavigationItem(
        label = "识别",
        screen = AppScreen.RECOGNITION_HUB
    ),
    BottomNavigationItem(
        label = "学习",
        screen = AppScreen.LEARNING_HUB
    ),
    BottomNavigationItem(
        label = "模型",
        screen = AppScreen.MODEL_HUB
    ),
    BottomNavigationItem(
        label = "我的",
        screen = AppScreen.PROFILE_HUB
    )
)

@Composable
private fun AppBottomNavigationBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    NavigationBar {
        bottomNavigationItems.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item.screen,
                onClick = {
                    onScreenSelected(item.screen)
                },
                icon = {},
                label = {
                    Text(item.label)
                }
            )
        }
    }
}
