package com.example.wasteclassificationapp.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.data.FeedbackEntity
import com.example.wasteclassificationapp.model.MistakeItem
import com.example.wasteclassificationapp.model.MistakeRepository

@Composable
fun MistakeScreen(
    feedbackList: List<FeedbackEntity>,
    onBackHome: () -> Unit
) {
    val wrongFeedbackList = feedbackList.filter { !it.isCorrect }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "易错垃圾库",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("本页面整理了校园垃圾分类中常见的易错物品，并结合你的错误反馈记录进行提醒。")

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "我的错题统计",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("反馈总数：${feedbackList.size} 次")
                Text("识别错误反馈：${wrongFeedbackList.size} 次")

                if (wrongFeedbackList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("最近错误反馈：")

                    wrongFeedbackList.take(3).forEach { record ->
                        Text("- ${record.labelCn}（${record.wasteCategory}，${record.modelName}）")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "常见易错垃圾",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        MistakeRepository.items.forEach { item ->
            MistakeCard(item)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }
    }
}

@Composable
private fun MistakeCard(
    item: MistakeItem
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("常见误区：")
            Text(item.wrongIdea)

            Spacer(modifier = Modifier.height(8.dp))

            Text("正确类别：")
            Text(item.correctCategory)

            Spacer(modifier = Modifier.height(8.dp))

            Text("原因说明：")
            Text(item.reason)

            Spacer(modifier = Modifier.height(8.dp))

            Text("投放建议：")
            Text(item.suggestion)
        }
    }
}