package com.example.wasteclassificationapp.model

data class CampusRule(
    val scene: String,
    val commonWaste: String,
    val rule: String,
    val suggestion: String,
    val notice: String
)

object CampusRuleRepository {

    val rules = listOf(
        CampusRule(
            scene = "宿舍",
            commonWaste = "塑料瓶、纸杯、纸巾、食品包装袋、快递纸箱、旧衣物",
            rule = "塑料瓶、干净纸张和快递纸箱优先投入可回收物；纸杯、纸巾、食品包装袋通常投入其他垃圾；旧衣物可投放至旧衣回收点。",
            suggestion = "宿舍内可以准备两个小袋子，一个收集可回收物，一个收集其他垃圾，定期带到楼下分类投放点。",
            notice = "食品包装袋和纸杯如果有明显残留，应尽量清理或沥干后再投放。"
        ),

        CampusRule(
            scene = "食堂",
            commonWaste = "剩饭剩菜、果皮、餐盒、纸杯、餐巾纸、饮料瓶",
            rule = "剩饭剩菜和果皮投入厨余垃圾；餐盒、纸杯、餐巾纸通常投入其他垃圾；饮料瓶清空后投入可回收物。",
            suggestion = "用餐后先将剩饭剩菜倒入厨余垃圾桶，再将餐盒、纸巾等投入其他垃圾桶。",
            notice = "不要把餐盒连同剩饭剩菜一起投入厨余垃圾桶。"
        ),

        CampusRule(
            scene = "教学楼",
            commonWaste = "打印纸、草稿纸、饮料瓶、纸杯、食品包装袋",
            rule = "干净纸张、饮料瓶属于可回收物；纸杯和食品包装袋通常属于其他垃圾。",
            suggestion = "学习产生的废纸应保持干燥后集中投入可回收物桶。",
            notice = "被油污或食物残渣污染的纸张不建议作为普通纸张回收。"
        ),

        CampusRule(
            scene = "快递站",
            commonWaste = "快递纸箱、胶带、塑料袋、泡沫包装、包装纸",
            rule = "快递纸箱、干净包装纸属于可回收物；胶带、污染塑料袋、泡沫包装一般按其他垃圾或学校指定规则处理。",
            suggestion = "取快递后尽量拆除胶带，将纸箱压平后投入纸箱回收点。",
            notice = "纸箱内的塑料袋、泡沫、胶带应尽量分开处理。"
        ),

        CampusRule(
            scene = "图书馆",
            commonWaste = "纸张、便利贴、饮料瓶、纸杯、食品包装袋",
            rule = "干净纸张和饮料瓶投入可回收物；纸杯、食品包装袋、污染纸张投入其他垃圾。",
            suggestion = "图书馆学习产生的废纸可以集中保存，离开时投入可回收物桶。",
            notice = "不要将带有饮料残留的纸杯与干净纸张混放。"
        ),

        CampusRule(
            scene = "操场",
            commonWaste = "饮料瓶、易拉罐、纸巾、食品包装袋",
            rule = "饮料瓶、易拉罐清空后投入可回收物；纸巾和食品包装袋通常投入其他垃圾。",
            suggestion = "运动后产生的饮料瓶应先清空液体，再投入可回收物桶。",
            notice = "不要把未喝完的饮料瓶直接投入可回收物桶。"
        ),

        CampusRule(
            scene = "实验楼",
            commonWaste = "纸张、塑料瓶、废电池、废灯管、实验相关废弃物",
            rule = "普通纸张和塑料瓶按常规分类投放；废电池、废灯管等特殊垃圾应投放至指定回收点；实验相关废弃物按实验室管理要求处理。",
            suggestion = "实验楼内产生的特殊垃圾不要随意丢弃，应询问老师或管理员后按规定投放。",
            notice = "实验相关废弃物不能简单按普通生活垃圾处理。"
        )
    )
}