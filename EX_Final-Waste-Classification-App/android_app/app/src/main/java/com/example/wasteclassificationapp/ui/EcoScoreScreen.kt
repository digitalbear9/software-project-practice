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
import com.example.wasteclassificationapp.model.EcoScoreState

@Composable
fun EcoScoreScreen(
    ecoScoreState: EcoScoreState,
    todayText: String,
    onCheckIn: () -> Unit,
    onResetScore: () -> Unit,
    onBackHome: () -> Unit
) {
    val hasCheckedInToday = ecoScoreState.lastCheckInDate == todayText

    val title = when {
        ecoScoreState.score >= 50 -> "校园环保达人"
        ecoScoreState.score >= 30 -> "分类行动者"
        ecoScoreState.score >= 10 -> "环保新手"
        else -> "刚刚起步"
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
            text = "环保积分",
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
                Text("通过每日打卡、完成识别、参与反馈和分类小测验积累环保积分，培养校园垃圾分类习惯。")
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
                    text = "我的环保积分",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("当前积分：${ecoScoreState.score}")
                Text("累计打卡天数：${ecoScoreState.checkInDays}")
                Text("今日日期：$todayText")
                Text("今日状态：${if (hasCheckedInToday) "已打卡" else "未打卡"}")
                Text("当前称号：$title")
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
                    text = "积分规则",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("每日打卡：+2 分")
                Text("完成一次识别：+1 分")
                Text("提交一次反馈：+1 分")
                Text("完成一次分类小测验：+3 分")
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
                    text = "积分操作",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCheckIn,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !hasCheckedInToday
                ) {
                    Text(
                        if (hasCheckedInToday) {
                            "今日已打卡"
                        } else {
                            "今日打卡 +2 分"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onResetScore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("重置积分")
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
