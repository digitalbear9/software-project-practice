package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ModelSettingScreen(
    currentModelName: String,
    onUseFloat32Model: () -> Unit,
    onUseDynamicRangeModel: () -> Unit,
    onBackHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "模型设置",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("当前使用模型：$currentModelName")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onUseFloat32Model,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("使用 Float32 模型")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onUseDynamicRangeModel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("使用 Dynamic Range 量化模型")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("说明：")
        Text("Float32 模型精度稳定；Dynamic Range 量化模型文件更小，适合移动端部署对比。")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }
    }
}