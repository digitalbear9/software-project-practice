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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.model.WasteSearchItem
import com.example.wasteclassificationapp.model.WasteSearchRepository

@Composable
fun SearchScreen(
    onBackHome: () -> Unit
) {
    var query by remember {
        mutableStateOf("")
    }

    var results by remember {
        mutableStateOf<List<WasteSearchItem>>(emptyList())
    }

    var hasSearched by remember {
        mutableStateOf(false)
    }

    val hotItems = WasteSearchRepository.getHotItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "垃圾名称搜索",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("当拍照不方便或识别结果不确定时，可以直接输入垃圾名称进行查询。")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("请输入垃圾名称")
            },
            placeholder = {
                Text("例如：纸杯、塑料瓶、果皮")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                results = WasteSearchRepository.search(query)
                hasSearched = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("搜索")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!hasSearched) {
            Text(
                text = "常见搜索",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            hotItems.forEach { item ->
                OutlinedButton(
                    onClick = {
                        query = item.name
                        results = WasteSearchRepository.search(item.name)
                        hasSearched = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(item.name)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            if (results.isEmpty()) {
                Text("暂无搜索结果。可以尝试输入：纸杯、塑料瓶、果皮、餐盒、废电池。")
            } else {
                Text(
                    text = "搜索结果",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                results.forEach { item ->
                    SearchResultCard(item)
                    Spacer(modifier = Modifier.height(12.dp))
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
    }
}

@Composable
private fun SearchResultCard(
    item: WasteSearchItem
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

            Text("垃圾类别：${item.category}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("投放建议：")
            Text(item.suggestion)

            Spacer(modifier = Modifier.height(8.dp))

            Text("常见误区：")
            Text(item.commonMistake)
        }
    }
}