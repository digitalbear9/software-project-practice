package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.ml.RecognitionResult
import com.example.wasteclassificationapp.model.DisposalStepRepository

@Composable
fun ResultScreen(
    result: RecognitionResult?,
    onRetry: () -> Unit,
    onBackHome: () -> Unit,
    onFeedbackCorrect: () -> Unit,
    onFeedbackWrong: () -> Unit
) {
    var feedbackMessage by remember(result) {
        mutableStateOf<String?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "识别结果",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (result != null) {
            val confidencePercent = result.confidence * 100

            val confidenceLevel = when {
                result.confidence >= 0.85f -> "高"
                result.confidence >= 0.70f -> "中"
                else -> "低"
            }

            val disposalStep = DisposalStepRepository.getStep(result.label)

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("识别结果：${result.labelCn}")
                    Text("英文标签：${result.label}")
                    Text("垃圾类别：${result.wasteCategory}")
                    Text("置信度：${String.format("%.2f", confidencePercent)}%")
                    Text("可信程度：$confidenceLevel")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("分类建议：")
                    Text(result.suggestion)

                    disposalStep?.let { step ->
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("投放前处理步骤：")
                        Text(step.title)

                        Spacer(modifier = Modifier.height(8.dp))

                        step.steps.forEachIndexed { index, item ->
                            Text("${index + 1}. $item")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("注意事项：${step.warning}")
                    }

                    if (result.confidence < 0.70f) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("低置信度提示：")
                        Text(
                            text = "本次识别置信度较低，结果可能不够准确。建议重新拍摄真实物品，保持光线充足，并让物品位于画面中央；也可以从相册选择更清晰的图片进行识别。"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("本次识别是否正确？")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onFeedbackCorrect()
                    feedbackMessage = "已记录反馈：识别正确"
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = feedbackMessage == null
            ) {
                Text("识别正确")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    onFeedbackWrong()
                    feedbackMessage = "已记录反馈：识别错误"
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = feedbackMessage == null
            ) {
                Text("识别错误")
            }

            feedbackMessage?.let { message ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(message)
            }
        } else {
            Text("暂无识别结果")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("重新识别")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }
    }
}