package com.example.wasteclassificationapp

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.ml.ImageClassifier
import com.example.wasteclassificationapp.ml.RecognitionResult

class MainActivity : ComponentActivity() {

    private lateinit var classifier: ImageClassifier

    private var latestResult by mutableStateOf<RecognitionResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        classifier = ImageClassifier(this)

        setContent {
            WasteClassificationApp(
                latestResult = latestResult,
                onImageSelected = { uri ->
                    classifyImage(uri)
                }
            )
        }
    }

    private fun classifyImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap != null) {
            latestResult = classifier.classify(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
    }
}

@Composable
fun WasteClassificationApp(
    latestResult: RecognitionResult?,
    onImageSelected: (Uri) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "校园智能识别助手",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "基于 Android + TFLite 的校园垃圾物品分类 APP",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Text("选择图片识别")
                }

                Spacer(modifier = Modifier.height(32.dp))

                latestResult?.let { result ->
                    ResultCard(result)
                }
            }
        }
    }
}

@Composable
fun ResultCard(result: RecognitionResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("识别结果：${result.labelCn}")
            Text("英文标签：${result.label}")
            Text("分类建议：${result.wasteCategory}")
            Text("置信度：${String.format("%.2f", result.confidence * 100)}%")
            Spacer(modifier = Modifier.height(8.dp))
            Text("提示：${result.suggestion}")
        }
    }
}