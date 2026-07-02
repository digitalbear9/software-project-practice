package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MainHomeScreen(
    ecoScore: Int,
    historyCount: Int,
    feedbackCount: Int,
    optimizationSampleCount: Int,
    onStartRecognize: () -> Unit,
    onOpenRealTime: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenDisposalPoint: () -> Unit,
    onOpenModelEvaluation: () -> Unit
) {
    HubPageContainer {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEDE7F6),
                contentColor = Color(0xFF4A148C)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "识界",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("今日概览")

        Spacer(modifier = Modifier.height(12.dp))

        MetricGridCard(
            items = listOf(
                "环保积分" to ecoScore.toString(),
                "识别历史" to historyCount.toString(),
                "反馈数" to feedbackCount.toString(),
                "待优化样本" to optimizationSampleCount.toString()
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("推荐功能")

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "垃圾搜索",
            description = "输入垃圾名称，快速查看分类类别和投放建议。",
            buttonText = "去搜索",
            onClick = onOpenSearch
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "校园投放点",
            description = "按垃圾类别查看校园内合适的投放位置。",
            buttonText = "查看投放点",
            onClick = onOpenDisposalPoint
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "模型评估报告",
            description = "查看模型准确率、推理耗时和移动端部署指标。",
            buttonText = "查看报告",
            onClick = onOpenModelEvaluation
        )
    }
}
