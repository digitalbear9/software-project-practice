package com.example.wasteclassificationapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraScreen(
    onImageCaptured: (Bitmap) -> Unit,
    onBackHome: () -> Unit
) {
    val context = LocalContext.current

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

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        CameraContent(
            onImageCaptured = onImageCaptured,
            onBackHome = onBackHome
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("需要相机权限才能进行拍照识别")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            ) {
                Text("申请相机权限")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackHome
            ) {
                Text("返回首页")
            }
        }
    }
}

@Composable
private fun CameraContent(
    onImageCaptured: (Bitmap) -> Unit,
    onBackHome: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    var isProcessing by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isProcessing = true
            errorMessage = null

            val bitmap = decodeBitmapFromUriWithCorrectOrientation(
                context = context,
                uri = uri
            )

            if (bitmap != null) {
                isProcessing = false
                onImageCaptured(bitmap)
            } else {
                isProcessing = false
                errorMessage = "相册图片读取失败"
            }
        }
    }

    LaunchedEffect(Unit) {
        val cameraProvider = context.getCameraProvider()

        val rotation = previewView.display?.rotation ?: Surface.ROTATION_0

        val preview = Preview.Builder()
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        imageCapture.targetRotation = rotation

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            errorMessage = "相机启动失败：${e.message}"
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener(
                {
                    cameraProviderFuture.get().unbindAll()
                },
                ContextCompat.getMainExecutor(context)
            )
        }
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
            text = "图片识别",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "支持拍照或从相册选择图片进行垃圾分类识别"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "相机预览",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                AndroidView(
                    factory = {
                        previewView
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "识别方式",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

            Text("请将物品放在画面中央，保持光线充足后再拍照")

            Spacer(modifier = Modifier.height(8.dp))

            errorMessage?.let {
                Text(text = it)

                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    isProcessing = true
                    errorMessage = null

                    imageCapture.targetRotation =
                        previewView.display?.rotation ?: Surface.ROTATION_0

                    takePhoto(
                        context = context,
                        imageCapture = imageCapture,
                        onSuccess = { bitmap ->
                            isProcessing = false
                            onImageCaptured(bitmap)
                        },
                        onError = { message ->
                            isProcessing = false
                            errorMessage = message
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text(
                    if (isProcessing) {
                        "识别中..."
                    } else {
                        "拍照并识别"
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text("从相册选择图片识别")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBackHome,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text("返回首页")
            }
        }
    }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    onSuccess: (Bitmap) -> Unit,
    onError: (String) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        "capture_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(
                outputFileResults: ImageCapture.OutputFileResults
            ) {
                val bitmap = decodeBitmapWithCorrectOrientation(photoFile)

                if (bitmap != null) {
                    onSuccess(bitmap)
                } else {
                    onError("图片读取失败")
                }
            }

            override fun onError(exception: ImageCaptureException) {
                onError("拍照失败：${exception.message}")
            }
        }
    )
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider {
    return suspendCoroutine { continuation ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            {
                continuation.resume(cameraProviderFuture.get())
            },
            ContextCompat.getMainExecutor(this)
        )
    }
}

private fun decodeBitmapWithCorrectOrientation(photoFile: File): Bitmap? {
    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath) ?: return null

    val exif = ExifInterface(photoFile.absolutePath)

    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )

    val rotationDegrees = orientationToDegrees(orientation)

    return if (rotationDegrees == 0) {
        bitmap
    } else {
        rotateBitmap(bitmap, rotationDegrees)
    }
}

private fun decodeBitmapFromUriWithCorrectOrientation(
    context: Context,
    uri: Uri
): Bitmap? {
    val orientation = try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ExifInterface(inputStream).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL
    } catch (e: Exception) {
        ExifInterface.ORIENTATION_NORMAL
    }

    val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
    } ?: return null

    val rotationDegrees = orientationToDegrees(orientation)

    return if (rotationDegrees == 0) {
        bitmap
    } else {
        rotateBitmap(bitmap, rotationDegrees)
    }
}

private fun orientationToDegrees(orientation: Int): Int {
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees.toFloat())
    }

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
