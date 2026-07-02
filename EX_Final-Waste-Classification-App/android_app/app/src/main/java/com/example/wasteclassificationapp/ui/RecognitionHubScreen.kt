package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecognitionHubScreen(
    onStartRecognize: () -> Unit,
    onOpenRealTime: () -> Unit,
    onOpenMultiObject: () -> Unit,
    onOpenBarcodeScan: () -> Unit
) {
    HubPageContainer {
        PageHeader(
            title = "识别中心",
            subtitle = "拍照、实时画面、多目标图片和包装条码识别集中在这里。"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onStartRecognize,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("开始识别")
        }

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "实时识别",
            description = "打开摄像头连续分析画面，适合快速判断手边物品。",
            buttonText = "进入实时识别",
            onClick = onOpenRealTime
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "多目标识别",
            description = "从图片中检测多个目标，再逐个进行垃圾分类。",
            buttonText = "选择图片分析",
            onClick = onOpenMultiObject
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "条码识别",
            description = "扫描包装条码或二维码，查询本地包装垃圾投放建议。",
            buttonText = "扫描条码",
            onClick = onOpenBarcodeScan
        )
    }
}
