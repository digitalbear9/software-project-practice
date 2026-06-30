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
import com.example.wasteclassificationapp.ui.CameraScreen
import com.example.wasteclassificationapp.ui.HomeScreen
import com.example.wasteclassificationapp.ui.ResultScreen

class MainActivity : ComponentActivity() {

    private lateinit var classifier: ImageClassifier

    private var currentScreen by mutableStateOf(AppScreen.HOME)
    private var latestResult by mutableStateOf<RecognitionResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifier = ImageClassifier(this)

        setContent {
            MaterialTheme {
                Surface {
                    when (currentScreen) {
                        AppScreen.HOME -> {
                            HomeScreen(
                                onStartRecognition = {
                                    currentScreen = AppScreen.CAMERA
                                }
                            )
                        }

                        AppScreen.CAMERA -> {
                            CameraScreen(
                                onImageCaptured = { bitmap ->
                                    latestResult = classifier.classify(bitmap)
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
    }
}

enum class AppScreen {
    HOME,
    CAMERA,
    RESULT
}