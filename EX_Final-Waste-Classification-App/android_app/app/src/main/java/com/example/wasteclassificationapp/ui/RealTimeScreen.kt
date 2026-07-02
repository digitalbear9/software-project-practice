package com.example.wasteclassificationapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.wasteclassificationapp.ml.RecognitionResult
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

@Composable
fun RealTimeScreen(
    lifecycleOwner: LifecycleOwner,
    onAnalyzeBitmap: (Bitmap) -> RecognitionResult,
    onBackHome: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    val lastAnalyzeTime = remember {
        AtomicLong(0L)
    }

    var latestResult by remember {
        mutableStateOf<RecognitionResult?>(null)
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Exception) {
            }

            cameraExecutor.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "实时识别",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "摄像头会定时分析画面，结果仅用于实时展示，不会自动保存到历史记录。"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasCameraPermission) {
            Text("需要相机权限才能使用实时识别功能。")

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("申请相机权限")
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "摄像头预览",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(360.dp),
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)

                            startRealTimeCamera(
                                context = ctx,
                                lifecycleOwner = lifecycleOwner,
                                previewView = previewView,
                                cameraExecutor = cameraExecutor,
                                lastAnalyzeTime = lastAnalyzeTime,
                                onAnalyzeBitmap = onAnalyzeBitmap,
                                onResult = { result ->
                                    latestResult = result
                                }
                            )

                            previewView
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "当前识别结果",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (latestResult == null) {
                        Text("请将垃圾物品放入画面中央")
                    } else {
                        val result = latestResult!!
                        Text("识别结果：${result.labelCn}")
                        Text("英文标签：${result.label}")
                        Text("垃圾类别：${result.wasteCategory}")
                        Text("置信度：${String.format("%.2f", result.confidence * 100)}%")
                        Text("分类建议：${result.suggestion}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun startRealTimeCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraExecutor: java.util.concurrent.ExecutorService,
    lastAnalyzeTime: AtomicLong,
    onAnalyzeBitmap: (Bitmap) -> RecognitionResult,
    onResult: (RecognitionResult) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener(
        {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val mainExecutor = ContextCompat.getMainExecutor(context)

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                try {
                    val now = System.currentTimeMillis()
                    val last = lastAnalyzeTime.get()

                    if (now - last >= 1000L && lastAnalyzeTime.compareAndSet(last, now)) {
                        val bitmap = imageProxyToBitmap(imageProxy)

                        if (bitmap != null) {
                            val result = onAnalyzeBitmap(bitmap)

                            mainExecutor.execute {
                                onResult(result)
                            }
                        }
                    }
                } catch (_: Exception) {
                } finally {
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (_: Exception) {
            }
        },
        ContextCompat.getMainExecutor(context)
    )
}

private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    val nv21 = yuv420ToNv21(imageProxy)

    val yuvImage = YuvImage(
        nv21,
        ImageFormat.NV21,
        imageProxy.width,
        imageProxy.height,
        null
    )

    val outputStream = ByteArrayOutputStream()

    yuvImage.compressToJpeg(
        Rect(0, 0, imageProxy.width, imageProxy.height),
        90,
        outputStream
    )

    val jpegBytes = outputStream.toByteArray()

    val bitmap = BitmapFactory.decodeByteArray(
        jpegBytes,
        0,
        jpegBytes.size
    ) ?: return null

    return rotateBitmap(
        bitmap = bitmap,
        rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
    )
}

private fun yuv420ToNv21(imageProxy: ImageProxy): ByteArray {
    val width = imageProxy.width
    val height = imageProxy.height
    val ySize = width * height
    val uvSize = width * height / 4

    val nv21 = ByteArray(ySize + uvSize * 2)

    val yPlane = imageProxy.planes[0]
    val uPlane = imageProxy.planes[1]
    val vPlane = imageProxy.planes[2]

    val yBuffer = yPlane.buffer
    val uBuffer = uPlane.buffer
    val vBuffer = vPlane.buffer

    val yRowStride = yPlane.rowStride
    val yPixelStride = yPlane.pixelStride

    var outputOffset = 0

    for (row in 0 until height) {
        for (col in 0 until width) {
            val yIndex = row * yRowStride + col * yPixelStride
            nv21[outputOffset++] = yBuffer.get(yIndex)
        }
    }

    val chromaHeight = height / 2
    val chromaWidth = width / 2

    val uRowStride = uPlane.rowStride
    val uPixelStride = uPlane.pixelStride
    val vRowStride = vPlane.rowStride
    val vPixelStride = vPlane.pixelStride

    for (row in 0 until chromaHeight) {
        for (col in 0 until chromaWidth) {
            val vIndex = row * vRowStride + col * vPixelStride
            val uIndex = row * uRowStride + col * uPixelStride

            nv21[outputOffset++] = vBuffer.get(vIndex)
            nv21[outputOffset++] = uBuffer.get(uIndex)
        }
    }

    return nv21
}

private fun rotateBitmap(
    bitmap: Bitmap,
    rotationDegrees: Float
): Bitmap {
    if (rotationDegrees == 0f) {
        return bitmap
    }

    val matrix = Matrix()
    matrix.postRotate(rotationDegrees)

    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}
