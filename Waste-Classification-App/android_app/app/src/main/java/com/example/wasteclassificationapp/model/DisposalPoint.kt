package com.example.wasteclassificationapp.model

data class DisposalPoint(
    val name: String,
    val area: String,
    val supportedCategories: List<String>,
    val openTime: String,
    val description: String,
    val suitableWaste: String
)

object DisposalPointRepository {

    private val points = listOf(
        DisposalPoint(
            name = "教学楼 A 区垃圾投放点",
            area = "教学楼",
            supportedCategories = listOf("可回收物", "其他垃圾"),
            openTime = "全天开放",
            description = "适合教学楼日常学习生活垃圾分类投放。",
            suitableWaste = "纸张、塑料瓶、纸杯、食品包装袋等。"
        ),

        DisposalPoint(
            name = "食堂门口厨余垃圾投放点",
            area = "食堂",
            supportedCategories = listOf("厨余垃圾", "其他垃圾"),
            openTime = "早餐至晚餐时间开放",
            description = "适合食堂用餐后产生的厨余垃圾和其他垃圾投放。",
            suitableWaste = "果皮、剩饭剩菜、餐巾纸、餐盒等。"
        ),

        DisposalPoint(
            name = "宿舍楼下分类投放点",
            area = "宿舍区",
            supportedCategories = listOf("可回收物", "其他垃圾"),
            openTime = "全天开放",
            description = "适合宿舍日常生活垃圾分类投放。",
            suitableWaste = "塑料瓶、纸张、食品包装袋、纸杯等。"
        ),

        DisposalPoint(
            name = "快递站纸箱回收点",
            area = "快递站",
            supportedCategories = listOf("可回收物"),
            openTime = "9:00 - 21:00",
            description = "适合快递包装类可回收物集中投放。",
            suitableWaste = "快递纸箱、包装纸、干净纸袋等。"
        ),

        DisposalPoint(
            name = "操场旁饮料瓶回收点",
            area = "操场",
            supportedCategories = listOf("可回收物", "其他垃圾"),
            openTime = "全天开放",
            description = "适合运动后产生的饮料瓶、纸巾和包装垃圾投放。",
            suitableWaste = "塑料瓶、易拉罐、纸巾、食品包装袋等。"
        ),

        DisposalPoint(
            name = "实验楼有害垃圾临时投放点",
            area = "实验楼",
            supportedCategories = listOf("有害垃圾"),
            openTime = "工作日 8:00 - 18:00",
            description = "适合特殊垃圾或有害垃圾临时投放，具体以学校管理要求为准。",
            suitableWaste = "废电池、废灯管、实验相关特殊废弃物等。"
        ),

        DisposalPoint(
            name = "图书馆入口分类投放点",
            area = "图书馆",
            supportedCategories = listOf("可回收物", "其他垃圾"),
            openTime = "图书馆开放时间",
            description = "适合图书馆学习场景下产生的纸张和饮料包装垃圾投放。",
            suitableWaste = "纸张、塑料瓶、纸杯、包装袋等。"
        )
    )

    fun getAllPoints(): List<DisposalPoint> {
        return points
    }

    fun filterByCategory(category: String): List<DisposalPoint> {
        if (category == "全部") {
            return points
        }

        return points.filter { point ->
            category in point.supportedCategories
        }
    }

    fun getCategories(): List<String> {
        return listOf(
            "全部",
            "可回收物",
            "厨余垃圾",
            "其他垃圾",
            "有害垃圾"
        )
    }
}