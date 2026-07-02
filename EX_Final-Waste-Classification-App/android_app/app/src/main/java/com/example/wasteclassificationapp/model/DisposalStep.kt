package com.example.wasteclassificationapp.model

data class DisposalStep(
    val label: String,
    val title: String,
    val steps: List<String>,
    val warning: String
)

object DisposalStepRepository {

    private val stepMap = mapOf(
        "plastic_bottle" to DisposalStep(
            label = "plastic_bottle",
            title = "塑料瓶投放前处理",
            steps = listOf(
                "倒空瓶内剩余液体",
                "简单压扁瓶身，减少占用空间",
                "投入可回收物收集容器"
            ),
            warning = "如果瓶内有明显污渍，建议简单冲洗后再投放。"
        ),

        "can" to DisposalStep(
            label = "can",
            title = "易拉罐投放前处理",
            steps = listOf(
                "清空罐内液体",
                "可简单压扁，减少占用空间",
                "投入可回收物收集容器"
            ),
            warning = "避免罐内残留饮料污染其他可回收物。"
        ),

        "paper" to DisposalStep(
            label = "paper",
            title = "纸张投放前处理",
            steps = listOf(
                "保持纸张干燥",
                "去除明显食物残渣或油污部分",
                "投入可回收物收集容器"
            ),
            warning = "被油污严重污染的纸张不建议按普通纸张回收。"
        ),

        "paper_cup" to DisposalStep(
            label = "paper_cup",
            title = "纸杯投放前处理",
            steps = listOf(
                "倒掉杯内剩余液体",
                "尽量沥干",
                "投入其他垃圾收集容器"
            ),
            warning = "使用后的纸杯常有覆膜或饮料污染，通常不按普通纸张回收。"
        ),

        "food_box" to DisposalStep(
            label = "food_box",
            title = "餐盒投放前处理",
            steps = listOf(
                "倒掉剩余食物",
                "尽量沥干水分",
                "投入其他垃圾收集容器"
            ),
            warning = "不要将剩饭剩菜和餐盒混在一起投放。"
        ),

        "food_packaging" to DisposalStep(
            label = "food_packaging",
            title = "食品包装投放前处理",
            steps = listOf(
                "清理明显食物残渣",
                "压平包装袋，减少占用空间",
                "投入其他垃圾收集容器"
            ),
            warning = "被油污污染的食品包装一般不作为可回收物处理。"
        ),

        "fruit_peel" to DisposalStep(
            label = "fruit_peel",
            title = "果皮投放前处理",
            steps = listOf(
                "去除塑料袋、纸巾等非厨余物",
                "尽量沥干水分",
                "投入厨余垃圾收集容器"
            ),
            warning = "不要连同塑料袋一起投入厨余垃圾桶。"
        )
    )

    fun getStep(label: String): DisposalStep? {
        return stepMap[label]
    }
}