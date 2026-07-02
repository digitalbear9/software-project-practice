package com.example.wasteclassificationapp.model

data class KnowledgeCard(
    val title: String,
    val description: String,
    val examples: String,
    val suggestion: String
)

object KnowledgeRepository {
    val cards = listOf(
        KnowledgeCard(
            title = "可回收物",
            description = "适宜回收利用的生活废弃物。",
            examples = "常见物品：纸张、塑料瓶、易拉罐、纸箱等。",
            suggestion = "投放前建议清空内容物，保持干燥，避免污染。"
        ),
        KnowledgeCard(
            title = "厨余垃圾",
            description = "易腐烂的生活废弃物。",
            examples = "常见物品：果皮、剩饭剩菜、菜叶等。",
            suggestion = "投放前尽量沥干水分，避免混入塑料袋、纸巾等其他垃圾。"
        ),
        KnowledgeCard(
            title = "有害垃圾",
            description = "对人体健康或自然环境可能造成危害的垃圾。",
            examples = "常见物品：废电池、废灯管、过期药品等。",
            suggestion = "应投放到有害垃圾收集容器，不要随意丢弃。"
        ),
        KnowledgeCard(
            title = "其他垃圾",
            description = "除可回收物、厨余垃圾和有害垃圾以外的其他生活废弃物。",
            examples = "常见物品：污染纸杯、餐盒、食品包装袋等。",
            suggestion = "尽量沥干水分后投放，避免混入可回收物。"
        )
    )
}