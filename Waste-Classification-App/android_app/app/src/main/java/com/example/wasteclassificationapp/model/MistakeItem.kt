package com.example.wasteclassificationapp.model

data class MistakeItem(
    val name: String,
    val wrongIdea: String,
    val correctCategory: String,
    val reason: String,
    val suggestion: String
)

object MistakeRepository {

    val items = listOf(
        MistakeItem(
            name = "纸杯",
            wrongIdea = "很多人以为纸杯是纸做的，所以属于可回收纸张。",
            correctCategory = "其他垃圾",
            reason = "使用后的纸杯内壁常有覆膜，也容易被饮料污染，通常不按普通纸张回收。",
            suggestion = "倒掉杯内液体，尽量沥干后投入其他垃圾收集容器。"
        ),

        MistakeItem(
            name = "外卖餐盒",
            wrongIdea = "很多人以为餐盒是塑料制品，所以一定属于可回收物。",
            correctCategory = "其他垃圾",
            reason = "使用后的餐盒通常有油污和食物残渣，容易污染其他可回收物。",
            suggestion = "先倒掉剩余食物，尽量沥干后投入其他垃圾收集容器。"
        ),

        MistakeItem(
            name = "食品包装袋",
            wrongIdea = "很多人以为食品包装袋是塑料，所以都可以回收。",
            correctCategory = "其他垃圾",
            reason = "食品包装袋常带有油污、调料残留或多层复合材料，不适合作为普通可回收物处理。",
            suggestion = "清理明显食物残渣后，投入其他垃圾收集容器。"
        ),

        MistakeItem(
            name = "污染纸张",
            wrongIdea = "很多人认为只要是纸，就都属于可回收物。",
            correctCategory = "视污染程度而定，严重污染时按其他垃圾处理",
            reason = "干净纸张通常可回收，但被油污、汤汁、食物残渣严重污染后，会影响回收处理。",
            suggestion = "干净纸张投入可回收物；污染严重的纸张投入其他垃圾。"
        ),

        MistakeItem(
            name = "果皮连塑料袋",
            wrongIdea = "很多人会把果皮连同塑料袋一起投入厨余垃圾桶。",
            correctCategory = "果皮属于厨余垃圾，塑料袋属于其他垃圾",
            reason = "塑料袋不属于厨余垃圾，会影响厨余垃圾后续处理。",
            suggestion = "投放前应将果皮和塑料袋分开，果皮投入厨余垃圾，塑料袋投入其他垃圾。"
        ),

        MistakeItem(
            name = "快递纸箱上的胶带",
            wrongIdea = "很多人把纸箱和胶带整体一起当作可回收物投放。",
            correctCategory = "纸箱属于可回收物，胶带一般按其他垃圾处理",
            reason = "纸箱可以回收，但大量胶带、塑料填充物会影响回收质量。",
            suggestion = "投放前尽量去除胶带和塑料填充物，将纸箱压平后投入可回收物。"
        ),

        MistakeItem(
            name = "废电池",
            wrongIdea = "很多人会把废电池当作普通其他垃圾丢弃。",
            correctCategory = "有害垃圾",
            reason = "废电池可能含有对环境有害的成分，应单独投放。",
            suggestion = "投入有害垃圾收集容器或学校指定的有害垃圾回收点。"
        ),

        MistakeItem(
            name = "奶茶杯组合垃圾",
            wrongIdea = "很多人会把奶茶杯、吸管、杯盖、剩余饮料一起投放。",
            correctCategory = "通常应拆分处理",
            reason = "奶茶杯可能有残余液体，吸管和杯盖材质不同，混投容易增加处理难度。",
            suggestion = "先倒掉剩余液体，再将杯身、吸管、杯盖按当地要求分别投放；不确定时按其他垃圾处理。"
        )
    )
}