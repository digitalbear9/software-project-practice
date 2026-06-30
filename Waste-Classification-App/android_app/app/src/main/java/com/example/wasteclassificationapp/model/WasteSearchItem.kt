package com.example.wasteclassificationapp.model

data class WasteSearchItem(
    val name: String,
    val category: String,
    val suggestion: String,
    val commonMistake: String,
    val keywords: List<String>
)

object WasteSearchRepository {

    private val items = listOf(
        WasteSearchItem(
            name = "塑料瓶",
            category = "可回收物",
            suggestion = "投放前建议清空瓶内液体，简单压扁后投入可回收物收集容器。",
            commonMistake = "不要把装有剩余饮料的塑料瓶直接投入可回收物桶，液体可能污染其他可回收物。",
            keywords = listOf(
                "塑料瓶",
                "饮料瓶",
                "矿泉水瓶",
                "水瓶",
                "可乐瓶",
                "plastic_bottle"
            )
        ),

        WasteSearchItem(
            name = "易拉罐",
            category = "可回收物",
            suggestion = "投放前建议清空罐内液体，可简单压扁后投入可回收物收集容器。",
            commonMistake = "不要把未喝完的饮料罐直接投放，残留液体会造成污染。",
            keywords = listOf(
                "易拉罐",
                "铝罐",
                "饮料罐",
                "罐",
                "can"
            )
        ),

        WasteSearchItem(
            name = "纸张",
            category = "可回收物",
            suggestion = "干净、干燥的纸张可以投入可回收物收集容器。",
            commonMistake = "被油污、食物残渣严重污染的纸张不建议按普通纸张回收。",
            keywords = listOf(
                "纸张",
                "纸",
                "打印纸",
                "试卷",
                "草稿纸",
                "作业纸",
                "paper"
            )
        ),

        WasteSearchItem(
            name = "纸杯",
            category = "其他垃圾",
            suggestion = "使用后的纸杯建议倒掉杯内液体，尽量沥干后投入其他垃圾收集容器。",
            commonMistake = "很多人以为纸杯属于可回收纸张，但使用后的纸杯常有覆膜或饮料污染，通常按其他垃圾处理。",
            keywords = listOf(
                "纸杯",
                "奶茶杯",
                "咖啡杯",
                "饮料杯",
                "paper_cup"
            )
        ),

        WasteSearchItem(
            name = "餐盒",
            category = "其他垃圾",
            suggestion = "投放前建议倒掉剩余食物，尽量沥干水分后投入其他垃圾收集容器。",
            commonMistake = "不要把剩饭剩菜和餐盒混在一起投放，厨余垃圾和餐盒应分开处理。",
            keywords = listOf(
                "餐盒",
                "外卖盒",
                "饭盒",
                "一次性餐盒",
                "food_box"
            )
        ),

        WasteSearchItem(
            name = "食品包装袋",
            category = "其他垃圾",
            suggestion = "投放前建议清理明显食物残渣，压平后投入其他垃圾收集容器。",
            commonMistake = "很多食品包装袋有油污或残渣，不适合作为普通可回收塑料处理。",
            keywords = listOf(
                "食品包装",
                "食品包装袋",
                "包装袋",
                "零食袋",
                "薯片袋",
                "饼干袋",
                "food_packaging"
            )
        ),

        WasteSearchItem(
            name = "果皮",
            category = "厨余垃圾",
            suggestion = "投放前建议去除塑料袋、纸巾等非厨余物，尽量沥干后投入厨余垃圾收集容器。",
            commonMistake = "不要把果皮连同塑料袋一起投入厨余垃圾桶，塑料袋不属于厨余垃圾。",
            keywords = listOf(
                "果皮",
                "香蕉皮",
                "橘子皮",
                "苹果皮",
                "水果皮",
                "fruit_peel"
            )
        ),

        WasteSearchItem(
            name = "废电池",
            category = "有害垃圾",
            suggestion = "废电池应投入有害垃圾收集容器或学校指定的有害垃圾回收点。",
            commonMistake = "不要把废电池随普通垃圾丢弃，也不要投入普通可回收物桶。",
            keywords = listOf(
                "电池",
                "废电池",
                "纽扣电池",
                "充电电池"
            )
        ),

        WasteSearchItem(
            name = "快递纸箱",
            category = "可回收物",
            suggestion = "投放前建议去除胶带，压平后投入可回收物收集容器或纸箱回收点。",
            commonMistake = "纸箱内如果混有塑料袋、泡沫、胶带等，应尽量分开处理。",
            keywords = listOf(
                "纸箱",
                "快递盒",
                "快递纸箱",
                "包装纸箱"
            )
        ),

        WasteSearchItem(
            name = "塑料袋",
            category = "其他垃圾",
            suggestion = "普通污染塑料袋通常按其他垃圾处理，投放前应避免混入厨余垃圾。",
            commonMistake = "不要把装有厨余垃圾的塑料袋一起投入厨余垃圾桶。",
            keywords = listOf(
                "塑料袋",
                "购物袋",
                "垃圾袋",
                "袋子"
            )
        )
    )

    fun search(query: String): List<WasteSearchItem> {
        val q = query.trim().lowercase()

        if (q.isBlank()) {
            return emptyList()
        }

        return items.filter { item ->
            item.name.lowercase().contains(q) ||
                    item.category.lowercase().contains(q) ||
                    item.keywords.any { keyword ->
                        keyword.lowercase().contains(q) || q.contains(keyword.lowercase())
                    }
        }
    }

    fun getHotItems(): List<WasteSearchItem> {
        return items.take(7)
    }
}