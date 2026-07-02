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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.model.CampusRule
import com.example.wasteclassificationapp.model.CampusRuleRepository

@Composable
fun CampusRuleScreen(
    onBackHome: () -> Unit
) {
    val rules = CampusRuleRepository.rules

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "校园分类规则",
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
                Text("本页面按照校园常见场景整理垃圾分类规则，帮助用户在宿舍、食堂、教学楼等场景中快速判断如何投放。")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        rules.forEach { rule ->
            CampusRuleCard(rule)
            Spacer(modifier = Modifier.height(12.dp))
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

@Composable
private fun CampusRuleCard(
    rule: CampusRule
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = rule.scene,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("常见垃圾：")
            Text(rule.commonWaste)

            Spacer(modifier = Modifier.height(8.dp))

            Text("分类规则：")
            Text(rule.rule)

            Spacer(modifier = Modifier.height(8.dp))

            Text("投放建议：")
            Text(rule.suggestion)

            Spacer(modifier = Modifier.height(8.dp))

            Text("注意事项：")
            Text(rule.notice)
        }
    }
}
