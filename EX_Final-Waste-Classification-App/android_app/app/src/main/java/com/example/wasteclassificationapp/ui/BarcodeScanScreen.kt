package com.example.wasteclassificationapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.wasteclassificationapp.model.BarcodeProduct
import com.example.wasteclassificationapp.model.BarcodeProductRepository
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@Composable
fun BarcodeScanScreen(
    lifecycleOwner: LifecycleOwner,
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

    var scannedCode by remember {
        mutableStateOf<String?>(null)
    }

    var matchedProduct by remember {
        mutableStateOf<BarcodeProduct?>(null)
    }

    var scanMessage by remember {
        mutableStateOf("请将包装上的条形码或二维码放入画面中央。")
    }

    val scanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128
            )
            .build()

        BarcodeScanning.getClient(options)
    }

    DisposableEffect(Unit) {
        onDispose {
            scanner.close()
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
            text = "条码识别",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("扫描包装上的条形码或二维码，查询本地包装垃圾投放建议")

        Spacer(modifier = Modifier.height(12.dp))

        if (hasCameraPermission) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "相机预览",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        factory = { viewContext ->
                            val previewView = PreviewView(viewContext)

                            val cameraProviderFuture = ProcessCameraProvider.getInstance(viewContext)

                            cameraProviderFuture.addListener(
                                {
                                    val cameraProvider = cameraProviderFuture.get()

                                    val preview = Preview.Builder()
                                        .build()
                                        .also { previewUseCase ->
                                            previewUseCase.setSurfaceProvider(
                                                previewView.surfaceProvider
                                            )
                                        }

                                    val imageAnalysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(
                                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                        )
                                        .build()

                                    imageAnalysis.setAnalyzer(
                                        ContextCompat.getMainExecutor(viewContext)
                                    ) { imageProxy ->
                                        processBarcodeImageProxy(
                                            imageProxy = imageProxy,
                                            scanner = scanner,
                                            onBarcodeDetected = { code ->
                                                scannedCode = code
                                                val product = BarcodeProductRepository.findByCode(code)
                                                matchedProduct = product

                                                scanMessage = if (product != null) {
                                                    "识别成功，已匹配本地包装数据库。"
                                                } else {
                                                    "识别成功，但该条码暂未收录。"
                                                }
                                            }
                                        )
                                    }

                                    try {
                                        cameraProvider.unbindAll()

                                        cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            CameraSelector.DEFAULT_BACK_CAMERA,
                                            preview,
                                            imageAnalysis
                                        )
                                    } catch (e: Exception) {
                                        scanMessage = "相机启动失败：${e.message ?: "未知错误"}"
                                    }
                                },
                                ContextCompat.getMainExecutor(viewContext)
                            )

                            previewView
                        }
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "相机权限",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("需要相机权限才能进行条码识别。请允许应用访问摄像头后继续扫描。")

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("申请相机权限")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BarcodeResultCard(
            scannedCode = scannedCode,
            product = matchedProduct,
            message = scanMessage
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                scannedCode = null
                matchedProduct = null
                scanMessage = "请将包装上的条形码或二维码放入画面中央。"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("重新扫描")
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

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processBarcodeImageProxy(
    imageProxy: androidx.camera.core.ImageProxy,
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onBarcodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image

    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(
        mediaImage,
        imageProxy.imageInfo.rotationDegrees
    )

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            val firstBarcode = barcodes.firstOrNull()
            val rawValue = firstBarcode?.rawValue

            if (!rawValue.isNullOrBlank()) {
                onBarcodeDetected(rawValue)
            }
        }
        .addOnFailureListener {
            // 本项目中失败时不弹出错误，避免实时扫描时频繁提示。
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}

@Composable
private fun BarcodeResultCard(
    scannedCode: String?,
    product: BarcodeProduct?,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "扫描结果",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(message)

            Spacer(modifier = Modifier.height(8.dp))

            if (scannedCode == null) {
                Text("当前未识别到条码或二维码。")
            } else {
                Text("识别码值：$scannedCode")

                Spacer(modifier = Modifier.height(8.dp))

                if (product != null) {
                    Text("匹配物品：${product.name}")
                    Text("垃圾类别：${product.category}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("投放建议：")
                    Text(product.suggestion)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("注意事项：")
                    Text(product.notice)
                } else {
                    Text("该条码暂未收录。")
                    Text("建议使用“垃圾搜索”或“拍照识别”继续判断。")
                }
            }
        }
    }
}
