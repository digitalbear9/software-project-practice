package com.example.wasteclassificationapp.model

data class WasteCategoryInfo(
    val label: String,
    val labelCn: String,
    val wasteCategory: String,
    val suggestion: String
)

object WasteCategory {
    private val categoryMap = mapOf(
        "can" to WasteCategoryInfo(
            label = "can",
            labelCn = "易拉罐",
            wasteCategory = "可回收物",
            suggestion = "易拉罐属于可回收物，投放前建议清空残留液体。"
        ),
        "food_box" to WasteCategoryInfo(
            label = "food_box",
            labelCn = "餐盒",
            wasteCategory = "其他垃圾",
            suggestion = "使用后的餐盒若油污较重，通常按其他垃圾处理。"
        ),
        "food_packaging" to WasteCategoryInfo(
            label = "food_packaging",
            labelCn = "食品包装",
            wasteCategory = "其他垃圾",
            suggestion = "食品包装袋或包装纸通常属于其他垃圾，投放前应尽量清理残留物。"
        ),
        "fruit_peel" to WasteCategoryInfo(
            label = "fruit_peel",
            labelCn = "果皮",
            wasteCategory = "厨余垃圾",
            suggestion = "果皮属于厨余垃圾，应投放到厨余垃圾桶中。"
        ),
        "paper" to WasteCategoryInfo(
            label = "paper",
            labelCn = "纸张",
            wasteCategory = "可回收物",
            suggestion = "干净纸张属于可回收物，保持干燥更利于回收。"
        ),
        "paper_cup" to WasteCategoryInfo(
            label = "paper_cup",
            labelCn = "纸杯",
            wasteCategory = "其他垃圾",
            suggestion = "使用后的纸杯通常因内壁覆膜或污染，按其他垃圾处理。"
        ),
        "plastic_bottle" to WasteCategoryInfo(
            label = "plastic_bottle",
            labelCn = "塑料瓶",
            wasteCategory = "可回收物",
            suggestion = "塑料瓶属于可回收物，投放前建议清空瓶内液体。"
        )
    )

    fun getInfo(label: String): WasteCategoryInfo {
        return categoryMap[label] ?: WasteCategoryInfo(
            label = label,
            labelCn = label,
            wasteCategory = "未知类别",
            suggestion = "未找到该类别的分类建议。"
        )
    }
}