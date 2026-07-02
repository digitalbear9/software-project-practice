package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileHubScreen(
    onOpenEcoScore: () -> Unit,
    onOpenReminderSetting: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenAssistant: () -> Unit
) {
    HubPageContainer {
        PageHeader(
            title = "我的",
            subtitle = "管理个人环保积分、提醒、历史记录和本地助手。"
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureCard(
            title = "环保积分",
            description = "通过打卡、识别、反馈和测验积累校园环保积分。",
            buttonText = "查看积分",
            onClick = onOpenEcoScore
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "每日提醒",
            description = "开启垃圾分类小提示，帮助保持日常投放习惯。",
            buttonText = "设置提醒",
            onClick = onOpenReminderSetting
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "识别历史",
            description = "回看识别时间、类别、置信度、建议和使用模型。",
            buttonText = "查看历史",
            onClick = onOpenHistory
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "本地指令助手",
            description = "使用本地规则回答垃圾分类和 APP 使用相关问题。",
            buttonText = "打开助手",
            onClick = onOpenAssistant
        )
    }
}
