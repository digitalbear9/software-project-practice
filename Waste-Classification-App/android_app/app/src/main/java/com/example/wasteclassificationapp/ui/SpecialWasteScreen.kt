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
import com.example.wasteclassificationapp.model.SpecialWasteItem
import com.example.wasteclassificationapp.model.SpecialWasteRepository

@Composable
fun SpecialWasteScreen(
    onBackHome: () -> Unit
) {
    val items = SpecialWasteRepository.items

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "特殊垃圾专区",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "本页面用于补充模型暂未覆盖的特殊垃圾，例如废电池、废灯管、过期药品、电子废弃物等。"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "特殊垃圾列表",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { item ->
            SpecialWasteCard(item)
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
private fun SpecialWasteCard(
    item: SpecialWasteItem
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

            Text("建议类别：${item.category}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("为什么要特殊处理：")
            Text(item.reason)

            Spacer(modifier = Modifier.height(8.dp))

            Text("投放建议：")
            Text(item.suggestion)

            Spacer(modifier = Modifier.height(8.dp))

            Text("注意事项：")
            Text(item.notice)
        }
    }
}