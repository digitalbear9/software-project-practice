package com.example.wasteclassificationapp.model

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)

object QuizRepository {

    val questions = listOf(
        QuizQuestion(
            question = "使用后的纸杯通常属于什么垃圾？",
            options = listOf("可回收物", "厨余垃圾", "其他垃圾", "有害垃圾"),
            correctAnswer = "其他垃圾",
            explanation = "使用后的纸杯内壁常有覆膜，也容易被饮料污染，通常按其他垃圾处理。"
        ),

        QuizQuestion(
            question = "塑料瓶投放前建议怎么处理？",
            options = listOf("直接丢弃", "清空液体并压扁", "连同剩余饮料一起投放", "投入厨余垃圾桶"),
            correctAnswer = "清空液体并压扁",
            explanation = "塑料瓶通常属于可回收物，投放前建议清空瓶内液体并简单压扁。"
        ),

        QuizQuestion(
            question = "果皮通常属于什么垃圾？",
            options = listOf("可回收物", "厨余垃圾", "其他垃圾", "有害垃圾"),
            correctAnswer = "厨余垃圾",
            explanation = "果皮容易腐烂，通常属于厨余垃圾。投放时不要连同塑料袋一起投放。"
        ),

        QuizQuestion(
            question = "外卖餐盒投放前应该怎么做？",
            options = listOf("直接投入可回收物桶", "倒掉剩余食物并沥干", "连同剩饭一起投入其他垃圾桶", "投入有害垃圾桶"),
            correctAnswer = "倒掉剩余食物并沥干",
            explanation = "外卖餐盒通常有油污和食物残渣，投放前应先倒掉剩余食物并尽量沥干。"
        ),

        QuizQuestion(
            question = "干净的纸张通常属于什么垃圾？",
            options = listOf("可回收物", "厨余垃圾", "其他垃圾", "有害垃圾"),
            correctAnswer = "可回收物",
            explanation = "干净、干燥的纸张通常可以回收；但被油污严重污染的纸张不建议按普通纸张回收。"
        ),

        QuizQuestion(
            question = "废电池应该投放到哪里？",
            options = listOf("厨余垃圾桶", "普通其他垃圾桶", "有害垃圾收集容器", "纸张回收箱"),
            correctAnswer = "有害垃圾收集容器",
            explanation = "废电池可能含有对环境有害的成分，应投入有害垃圾收集容器或学校指定回收点。"
        ),

        QuizQuestion(
            question = "食品包装袋通常应如何投放？",
            options = listOf("直接按厨余垃圾处理", "清理明显残渣后按其他垃圾处理", "一定属于可回收物", "一定属于有害垃圾"),
            correctAnswer = "清理明显残渣后按其他垃圾处理",
            explanation = "食品包装袋常带有油污、调料残留或复合材料，通常按其他垃圾处理。"
        ),

        QuizQuestion(
            question = "果皮能不能连同塑料袋一起投入厨余垃圾桶？",
            options = listOf("可以", "不可以，应分开投放", "必须一起投放", "都属于可回收物"),
            correctAnswer = "不可以，应分开投放",
            explanation = "果皮属于厨余垃圾，但塑料袋不属于厨余垃圾，混入会影响后续处理。"
        ),

        QuizQuestion(
            question = "快递纸箱投放前建议怎么处理？",
            options = listOf("压平纸箱，尽量去除胶带", "直接投入厨余垃圾桶", "必须剪碎后投入有害垃圾桶", "连同食物残渣一起投放"),
            correctAnswer = "压平纸箱，尽量去除胶带",
            explanation = "快递纸箱属于可回收物，压平后投放可以减少占用空间。"
        ),

        QuizQuestion(
            question = "易拉罐通常属于什么垃圾？",
            options = listOf("可回收物", "厨余垃圾", "其他垃圾", "有害垃圾"),
            correctAnswer = "可回收物",
            explanation = "易拉罐通常属于可回收物，投放前建议清空罐内液体。"
        )
    )
}