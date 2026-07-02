package com.example.wasteclassificationapp.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.ml.RecognitionResult
import com.example.wasteclassificationapp.model.DetectedWasteObject
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

@Composable
fun MultiObjectDetectionScreen(
    onClassifyBitmap: (Bitmap) -> RecognitionResult,
    onBackHome: () -> Unit
) {
    val context = LocalContext.current

    var selectedBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var detectedObjects by remember {
        mutableStateOf<List<DetectedWasteObject>>(emptyList())
    }

    var message by remember {
        mutableStateOf("请选择一张包含多个垃圾物品的图片进行分析。")
    }

    var isAnalyzing by remember {
        mutableStateOf(false)
    }

    val objectDetector = remember {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .build()

        ObjectDetection.getClient(options)
    }

    DisposableEffect(Unit) {
        onDispose {
            objectDetector.close()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }

        val bitmap = loadBitmapFromUri(
            context = context,
            uri = uri
        )

        if (bitmap == null) {
            message = "图片读取失败，请重新选择。"
            return@rememberLauncherForActivityResult
        }

        selectedBitmap = bitmap
        detectedObjects = emptyList()
        isAnalyzing = true
        message = "正在进行目标检测和逐个分类，请稍候……"

        analyzeMultiObjectImage(
            bitmap = bitmap,
            objectDetector = objectDetector,
            onClassifyBitmap = onClassifyBitmap,
            onSuccess = { results ->
                detectedObjects = results
                isAnalyzing = false

                message = if (results.isEmpty()) {
                    "未检测到明显物体。建议选择物体更清晰、背景更简单的图片。"
                } else {
                    "共检测到 ${results.size} 个目标，并已完成逐个分类。"
                }
            },
            onFailure = { errorMessage ->
                isAnalyzing = false
                message = errorMessage
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "多目标识别",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("选择包含多个垃圾物品的图片，系统会先检测目标区域，再逐个分类")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                imagePickerLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isAnalyzing
        ) {
            Text(
                if (isAnalyzing) {
                    "正在分析……"
                } else {
                    "选择图片并分析"
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedBitmap?.let { bitmap ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "已选择图片",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "待分析图片",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "分析状态",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(message)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (detectedObjects.isNotEmpty()) {
            Text(
                text = "识别结果列表",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            detectedObjects.forEach { detectedObject ->
                DetectedWasteObjectCard(detectedObject)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                selectedBitmap = null
                detectedObjects = emptyList()
                message = "请选择一张包含多个垃圾物品的图片进行分析。"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("清空结果")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetectedWasteObjectCard(
    detectedObject: DetectedWasteObject
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "目标 ${detectedObject.index}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("检测框：${detectedObject.boundingBoxText}")
            Text("识别结果：${detectedObject.labelCn}（${detectedObject.label}）")
            Text("垃圾类别：${detectedObject.wasteCategory}")
            Text("置信度：${String.format("%.2f", detectedObject.confidence * 100)}%")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Top-3 候选：")
            Text(detectedObject.topCandidatesText)

            Spacer(modifier = Modifier.height(8.dp))

            Text("投放建议：")
            Text(detectedObject.suggestion)

            if (detectedObject.isUncertain) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("不确定提示：")
                Text(
                    if (detectedObject.uncertaintyReason.isNotBlank()) {
                        detectedObject.uncertaintyReason
                    } else {
                        "当前目标识别结果可能不够稳定，建议重新拍摄或人工确认。"
                    }
                )
            }
        }
    }
}

private fun analyzeMultiObjectImage(
    bitmap: Bitmap,
    objectDetector: ObjectDetector,
    onClassifyBitmap: (Bitmap) -> RecognitionResult,
    onSuccess: (List<DetectedWasteObject>) -> Unit,
    onFailure: (String) -> Unit
) {
    val image = InputImage.fromBitmap(bitmap, 0)

    objectDetector.process(image)
        .addOnSuccessListener { mlKitObjects ->
            val results = mlKitObjects.mapIndexedNotNull { index, detectedObject ->
                val cropBitmap = cropBitmapSafely(
                    source = bitmap,
                    boundingBox = detectedObject.boundingBox
                ) ?: return@mapIndexedNotNull null

                val recognitionResult = onClassifyBitmap(cropBitmap)

                val topCandidatesText = recognitionResult.topCandidates.joinToString(" | ") { candidate ->
                    "${candidate.labelCn}:${String.format("%.2f", candidate.confidence * 100)}%"
                }

                DetectedWasteObject(
                    index = index + 1,
                    boundingBoxText = buildBoundingBoxText(detectedObject.boundingBox),
                    label = recognitionResult.label,
                    labelCn = recognitionResult.labelCn,
                    wasteCategory = recognitionResult.wasteCategory,
                    confidence = recognitionResult.confidence,
                    suggestion = recognitionResult.suggestion,
                    topCandidatesText = topCandidatesText,
                    isUncertain = recognitionResult.isUncertain,
                    uncertaintyReason = recognitionResult.uncertaintyReason
                )
            }

            onSuccess(results)
        }
        .addOnFailureListener { exception ->
            onFailure("多目标检测失败：${exception.message ?: "未知错误"}")
        }
}

private fun cropBitmapSafely(
    source: Bitmap,
    boundingBox: Rect
): Bitmap? {
    if (source.width <= 0 || source.height <= 0) {
        return null
    }

    val left = boundingBox.left.coerceIn(0, source.width - 1)
    val top = boundingBox.top.coerceIn(0, source.height - 1)
    val right = boundingBox.right.coerceIn(left + 1, source.width)
    val bottom = boundingBox.bottom.coerceIn(top + 1, source.height)

    val width = right - left
    val height = bottom - top

    if (width < 20 || height < 20) {
        return null
    }

    return try {
        Bitmap.createBitmap(
            source,
            left,
            top,
            width,
            height
        )
    } catch (e: Exception) {
        null
    }
}

private fun buildBoundingBoxText(
    boundingBox: Rect
): String {
    return "x=${boundingBox.left}, y=${boundingBox.top}, w=${boundingBox.width()}, h=${boundingBox.height()}"
}

private fun loadBitmapFromUri(
    context: android.content.Context,
    uri: Uri
): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(
                context.contentResolver,
                uri
            )

            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                uri
            )
        }
    } catch (e: Exception) {
        null
    }
}
