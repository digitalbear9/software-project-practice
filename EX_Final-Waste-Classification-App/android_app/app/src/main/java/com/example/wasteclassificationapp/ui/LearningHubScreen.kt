package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LearningHubScreen(
    onOpenSearch: () -> Unit,
    onOpenMistake: () -> Unit,
    onOpenKnowledge: () -> Unit,
    onOpenDisposalPoint: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenSpecialWaste: () -> Unit,
    onOpenCampusRule: () -> Unit
) {
    HubPageContainer {
        PageHeader(
            title = "学习中心",
            subtitle = "用查询、知识卡片和测验建立稳定的垃圾分类判断能力。"
        )

        Spacer(modifier = Modifier.height(20.dp))

        FeatureCard(
            title = "垃圾搜索",
            description = "输入垃圾名称，查看类别、投放建议和常见误区。",
            buttonText = "搜索垃圾",
            onClick = onOpenSearch
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "易错垃圾",
            description = "复习校园场景中容易误投的垃圾和正确处理方式。",
            buttonText = "查看易错项",
            onClick = onOpenMistake
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "分类知识",
            description = "通过知识卡片了解常见垃圾类别和投放原则。",
            buttonText = "学习知识",
            onClick = onOpenKnowledge
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "校园投放点",
            description = "按类别查找校园内适合投放的位置。",
            buttonText = "查看投放点",
            onClick = onOpenDisposalPoint
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "分类小测验",
            description = "用答题巩固纸杯、餐盒、果皮等常见物品分类。",
            buttonText = "开始测验",
            onClick = onOpenQuiz
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "特殊垃圾专区",
            description = "查看废电池、过期药品等模型未覆盖物品的处理建议。",
            buttonText = "查看专区",
            onClick = onOpenSpecialWaste
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureCard(
            title = "校园分类规则",
            description = "按宿舍、食堂、教学楼等校园场景查看分类规则。",
            buttonText = "查看规则",
            onClick = onOpenCampusRule
        )
    }
}
