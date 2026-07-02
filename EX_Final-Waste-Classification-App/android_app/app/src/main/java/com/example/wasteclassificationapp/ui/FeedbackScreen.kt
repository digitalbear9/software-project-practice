package com.example.wasteclassificationapp.ui

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.data.FeedbackEntity

@Composable
fun FeedbackScreen(
    feedbackList: List<FeedbackEntity>,
    onClearFeedback: () -> Unit,
    onExportFeedback: () -> Unit,
    onBackHome: () -> Unit
) {
    val correctCount = feedbackList.count { it.isCorrect }
    val wrongCount = feedbackList.size - correctCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "反馈记录",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "反馈总览",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("反馈总数：${feedbackList.size}")
                Text("识别正确：$correctCount")
                Text("识别错误：$wrongCount")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (feedbackList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "暂无反馈记录",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("在识别结果页点击“识别正确”或“识别错误”后，这里会保存反馈记录。")
                }
            }
        } else {
            feedbackList.forEach { record ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (record.isCorrect) "反馈：识别正确" else "反馈：识别错误",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text("识别物品：${record.labelCn}")
                        Text("英文标签：${record.label}")
                        Text("垃圾类别：${record.wasteCategory}")
                        Text("反馈时间：${record.timeText}")
                        Text("使用模型：${record.modelName}")
                        Text("置信度：${String.format("%.2f", record.confidence * 100)}%")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onExportFeedback,
            modifier = Modifier.fillMaxWidth(),
            enabled = feedbackList.isNotEmpty()
        ) {
            Text("导出反馈样本")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onClearFeedback,
            modifier = Modifier.fillMaxWidth(),
            enabled = feedbackList.isNotEmpty()
        ) {
            Text("清空反馈记录")
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
