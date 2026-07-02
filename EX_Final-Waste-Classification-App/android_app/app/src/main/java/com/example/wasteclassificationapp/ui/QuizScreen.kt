package com.example.wasteclassificationapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wasteclassificationapp.model.QuizQuestion
import com.example.wasteclassificationapp.model.QuizRepository

@Composable
fun QuizScreen(
    onQuizFinished: () -> Unit,
    onBackHome: () -> Unit
){
    val questions = QuizRepository.questions

    var currentIndex by remember {
        mutableIntStateOf(0)
    }

    var selectedAnswer by remember {
        mutableStateOf<String?>(null)
    }

    var hasSubmitted by remember {
        mutableStateOf(false)
    }

    var score by remember {
        mutableIntStateOf(0)
    }

    var isFinished by remember {
        mutableStateOf(false)
    }

    var hasAddedQuizScore by remember {
        mutableStateOf(false)
    }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "分类小测验",
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
                Text("通过答题巩固垃圾分类知识，减少纸杯、餐盒、食品包装等常见误投问题。")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isFinished) {
            QuizResultCard(
                score = score,
                total = questions.size,
                onRestart = {
                    currentIndex = 0
                    selectedAnswer = null
                    hasSubmitted = false
                    score = 0
                    isFinished = false
                    hasAddedQuizScore = false
                }
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "答题进度",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("第 ${currentIndex + 1} / ${questions.size} 题")
                    Text("当前得分：$score")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            QuestionCard(
                question = currentQuestion,
                selectedAnswer = selectedAnswer,
                hasSubmitted = hasSubmitted,
                onSelectAnswer = { answer ->
                    if (!hasSubmitted) {
                        selectedAnswer = answer
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasSubmitted) {
                Button(
                    onClick = {
                        if (selectedAnswer != null) {
                            hasSubmitted = true

                            if (selectedAnswer == currentQuestion.correctAnswer) {
                                score += 1
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAnswer != null
                ) {
                    Text("提交答案")
                }
            } else {
                AnswerExplanationCard(
                    question = currentQuestion,
                    selectedAnswer = selectedAnswer
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (currentIndex < questions.lastIndex) {
                            currentIndex += 1
                            selectedAnswer = null
                            hasSubmitted = false
                        } else {
                            isFinished = true
                            if (!hasAddedQuizScore) {
                                onQuizFinished()
                                hasAddedQuizScore = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (currentIndex < questions.lastIndex) {
                            "下一题"
                        } else {
                            "查看得分"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回首页")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuestionCard(
    question: QuizQuestion,
    selectedAnswer: String?,
    hasSubmitted: Boolean,
    onSelectAnswer: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !hasSubmitted) {
                            onSelectAnswer(option)
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == option,
                        onClick = {
                            onSelectAnswer(option)
                        },
                        enabled = !hasSubmitted
                    )

                    Text(option)
                }
            }
        }
    }
}

@Composable
private fun AnswerExplanationCard(
    question: QuizQuestion,
    selectedAnswer: String?
) {
    val isCorrect = selectedAnswer == question.correctAnswer

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (isCorrect) {
                    "回答正确"
                } else {
                    "回答错误"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("你的答案：${selectedAnswer ?: "未选择"}")
            Text("正确答案：${question.correctAnswer}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("解析：")
            Text(question.explanation)
        }
    }
}

@Composable
private fun QuizResultCard(
    score: Int,
    total: Int,
    onRestart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "测验完成",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("本次得分：$score / $total")

            Spacer(modifier = Modifier.height(8.dp))

            val comment = when {
                score == total -> "非常棒！你已经很好地掌握了这些校园垃圾分类知识。"
                score >= total * 0.7 -> "表现不错，少数易错垃圾可以继续复习。"
                else -> "建议回到分类知识和易错垃圾页面继续学习。"
            }

            Text(comment)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("重新测验")
            }
        }
    }
}
