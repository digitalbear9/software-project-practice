package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    onOpenRealTime: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenMistake: () -> Unit,
    onOpenDisposalPoint: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenSpecialWaste: () -> Unit,
    onOpenCampusRule: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenKnowledge: () -> Unit,
    onOpenFeedback: () -> Unit,
    onOpenModelSetting: () -> Unit,
    onOpenAssistant: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
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

                Text("支持拍照识别、实时识别、垃圾搜索、易错垃圾、校园投放点、分类小测验、特殊垃圾专区、校园分类规则、识别历史、识别统计、分类知识、反馈记录、模型切换和本地指令助手。")
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
            onClick = onOpenRealTime,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("实时识别")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenSearch,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("垃圾搜索")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenMistake,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("易错垃圾")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenDisposalPoint,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("校园投放点")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("分类小测验")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenSpecialWaste,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("特殊垃圾专区")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenCampusRule,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("校园分类规则")
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
            onClick = onOpenStatistics,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("识别统计")
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
            onClick = onOpenFeedback,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("反馈记录")
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