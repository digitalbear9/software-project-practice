package com.example.wasteclassificationapp.model

data class BarcodeProduct(
    val code: String,
    val name: String,
    val category: String,
    val suggestion: String,
    val notice: String
)

object BarcodeProductRepository {

    private val products = listOf(
        BarcodeProduct(
            code = "WASTE_PLASTIC_BOTTLE",
            name = "示例饮料瓶",
            category = "可回收物",
            suggestion = "投放前请清空瓶内液体，简单压扁后投入可回收物收集容器。",
            notice = "如果瓶内有明显污渍，建议简单冲洗后再投放。"
        ),

        BarcodeProduct(
            code = "WASTE_PAPER_CUP",
            name = "示例纸杯",
            category = "其他垃圾",
            suggestion = "倒掉杯内液体，尽量沥干后投入其他垃圾收集容器。",
            notice = "使用后的纸杯常有覆膜或饮料污染，通常不按普通纸张回收。"
        ),

        BarcodeProduct(
            code = "WASTE_FOOD_BOX",
            name = "示例外卖餐盒",
            category = "其他垃圾",
            suggestion = "投放前请倒掉剩余食物，尽量沥干后投入其他垃圾收集容器。",
            notice = "不要把餐盒和剩饭剩菜混在一起投入厨余垃圾桶。"
        ),

        BarcodeProduct(
            code = "WASTE_FOOD_PACKAGING",
            name = "示例食品包装袋",
            category = "其他垃圾",
            suggestion = "清理明显食物残渣后，投入其他垃圾收集容器。",
            notice = "食品包装袋常有油污或复合材料，不建议直接按普通可回收物处理。"
        ),

        BarcodeProduct(
            code = "WASTE_CAN",
            name = "示例易拉罐",
            category = "可回收物",
            suggestion = "清空罐内液体，可简单压扁后投入可回收物收集容器。",
            notice = "避免罐内残留饮料污染其他可回收物。"
        ),

        BarcodeProduct(
            code = "WASTE_PAPER",
            name = "示例纸张",
            category = "可回收物",
            suggestion = "保持纸张干燥，投入可回收物收集容器。",
            notice = "被油污或食物残渣严重污染的纸张不建议按普通纸张回收。"
        ),

        BarcodeProduct(
            code = "WASTE_FRUIT_PEEL",
            name = "示例果皮",
            category = "厨余垃圾",
            suggestion = "去除塑料袋、纸巾等非厨余物，沥干后投入厨余垃圾收集容器。",
            notice = "不要连同塑料袋一起投入厨余垃圾桶。"
        ),

        BarcodeProduct(
            code = "690000000001",
            name = "示例饮料瓶条形码",
            category = "可回收物",
            suggestion = "清空液体，压扁瓶身后投入可回收物收集容器。",
            notice = "该编号为课程演示用本地条码数据。"
        ),

        BarcodeProduct(
            code = "690000000002",
            name = "示例零食包装袋条形码",
            category = "其他垃圾",
            suggestion = "清理明显残渣后投入其他垃圾收集容器。",
            notice = "该编号为课程演示用本地条码数据。"
        )
    )

    fun findByCode(code: String): BarcodeProduct? {
        return products.find { it.code == code }
    }
}