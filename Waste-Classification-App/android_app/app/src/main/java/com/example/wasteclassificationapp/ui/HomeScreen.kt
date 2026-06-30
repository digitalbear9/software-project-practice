package com.example.wasteclassificationapp.ui

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onStartRecognize: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenKnowledge: () -> Unit,
    onOpenModelSetting: () -> Unit,
    onOpenAssistant: () -> Unit
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
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "基于 Android + TFLite 的校园垃圾分类识别 APP",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "V3 功能",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("支持拍照识别、相册识别、识别历史、分类知识、错误反馈、模型切换和本地指令助手。")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onStartRecognize,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("开始识别")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("识别历史")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenKnowledge,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("分类知识")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenModelSetting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("模型设置")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenAssistant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("本地指令助手")
        }
    }
}