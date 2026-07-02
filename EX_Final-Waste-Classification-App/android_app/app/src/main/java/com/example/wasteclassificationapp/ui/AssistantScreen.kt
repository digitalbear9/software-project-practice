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

@Composable
fun AssistantScreen(
    onBackHome: () -> Unit
) {
    var question by remember {
        mutableStateOf("")
    }

    var answer by remember {
        mutableStateOf("你可以输入垃圾分类相关问题，例如：塑料瓶怎么投放、纸杯是什么垃圾、拍照不准怎么办。")
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
            text = "本地指令助手",
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
                    text = "本助手采用本地规则匹配方式运行，不需要联网，主要用于回答垃圾分类和 APP 使用相关问题。",
                    style = MaterialTheme.typography.bodyMedium
                )
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
                    text = "输入问题",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = question,
                    onValueChange = {
                        question = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("请输入问题")
                    },
                    placeholder = {
                        Text("例如：塑料瓶怎么投放？")
                    },
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        answer = getAssistantAnswer(question)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("获取回答")
                }
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
                    text = "助手回答",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(answer)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "常用问题",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        question = "塑料瓶怎么投放？"
                        answer = getAssistantAnswer(question)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("塑料瓶怎么投放？")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        question = "纸杯是什么垃圾？"
                        answer = getAssistantAnswer(question)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("纸杯是什么垃圾？")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        question = "拍照识别不准怎么办？"
                        answer = getAssistantAnswer(question)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("拍照识别不准怎么办？")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        question = "置信度低是什么意思？"
                        answer = getAssistantAnswer(question)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("置信度低是什么意思？")
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

private fun getAssistantAnswer(question: String): String {
    val q = question.trim().lowercase()

    if (q.isBlank()) {
        return "请输入一个问题，例如：塑料瓶怎么投放、纸杯是什么垃圾、拍照识别不准怎么办。"
    }

    return when {
        "塑料瓶" in q || "plastic_bottle" in q -> {
            "塑料瓶通常属于可回收物。投放前建议清空瓶内液体，尽量压扁后投入可回收物收集容器。"
        }

        "纸张" in q || "纸" in q && "纸杯" !in q -> {
            "干净的纸张通常属于可回收物。投放时应尽量保持干燥，避免被油污、食物残渣污染。"
        }

        "易拉罐" in q || "罐" in q || "can" in q -> {
            "易拉罐通常属于可回收物。投放前建议清空内容物，简单压扁后投入可回收物收集容器。"
        }

        "纸杯" in q || "paper_cup" in q -> {
            "使用后的纸杯通常按其他垃圾处理。因为纸杯内壁常有覆膜，且容易被饮料污染，不建议直接按普通纸张回收。"
        }

        "餐盒" in q || "food_box" in q -> {
            "一次性餐盒通常按其他垃圾处理。投放前建议倒掉剩余食物，尽量沥干水分。"
        }

        "食品包装" in q || "包装袋" in q || "food_packaging" in q -> {
            "食品包装袋通常属于其他垃圾。若包装上有明显食物残渣，应尽量清理后再投放。"
        }

        "果皮" in q || "fruit_peel" in q -> {
            "果皮通常属于厨余垃圾。投放时建议沥干水分，不要连同塑料袋一起投入厨余垃圾桶。"
        }

        "可回收" in q -> {
            "可回收物是适宜回收利用的生活废弃物，例如塑料瓶、纸张、易拉罐等。投放前建议清空内容物并保持干燥。"
        }

        "厨余" in q -> {
            "厨余垃圾是容易腐烂的生活废弃物，例如果皮、剩饭剩菜等。投放时应尽量沥干水分。"
        }

        "其他垃圾" in q -> {
            "其他垃圾是除可回收物、厨余垃圾和有害垃圾以外的生活废弃物，例如污染纸杯、餐盒、食品包装袋等。"
        }

        "有害垃圾" in q -> {
            "有害垃圾包括废电池、废灯管、过期药品等。当前 APP 暂未识别有害垃圾类别，但实际投放时应投入有害垃圾收集容器。"
        }

        "拍照" in q || "不准" in q || "识别错误" in q -> {
            "拍照识别不准通常与光线、背景、角度、反光和图片清晰度有关。建议拍摄真实物品，保持光线充足，让物品位于画面中央，并尽量避免拍电脑屏幕上的图片。"
        }

        "置信度" in q || "可信" in q -> {
            "置信度表示模型对当前识别结果的把握程度。置信度较低时，结果可能不够稳定，建议重新拍摄或从相册选择更清晰的图片。"
        }

        "模型" in q || "量化" in q || "dynamic" in q || "float32" in q -> {
            "当前 APP 支持 Float32 模型和 Dynamic Range 量化模型切换。Float32 模型精度较稳定，Dynamic Range 量化模型文件更小，适合移动端部署对比。"
        }

        "历史" in q -> {
            "识别历史用于记录每次识别的时间、类别、置信度、分类建议和使用模型，方便用户回看之前的识别结果。"
        }

        "反馈" in q -> {
            "错误反馈功能用于记录用户对识别结果的判断，例如识别正确或识别错误。后续可以根据反馈样本改进模型。"
        }

        "实时识别" in q -> {
            "实时识别是指通过摄像头连续获取画面并定时进行识别。为了避免卡顿，实时识别通常不会每一帧都保存为历史记录。"
        }

        else -> {
            "暂时无法回答该问题。你可以尝试询问：塑料瓶怎么投放、纸杯是什么垃圾、果皮是什么垃圾、拍照识别不准怎么办、置信度低是什么意思。"
        }
    }
}
