package com.example.wasteclassificationapp.model

data class SpecialWasteItem(
    val name: String,
    val category: String,
    val reason: String,
    val suggestion: String,
    val notice: String
)

object SpecialWasteRepository {

    val items = listOf(
        SpecialWasteItem(
            name = "废电池",
            category = "有害垃圾 / 专门回收",
            reason = "废电池可能含有对环境有害的成分，不适合随普通生活垃圾混投。",
            suggestion = "建议投入学校或社区设置的有害垃圾收集容器，或交给指定回收点。",
            notice = "不要随意拆开、挤压或投入普通垃圾桶。"
        ),

        SpecialWasteItem(
            name = "废灯管",
            category = "有害垃圾 / 专门回收",
            reason = "部分灯管可能含有有害成分，破损后可能造成环境污染或安全风险。",
            suggestion = "建议完整保存后投放到有害垃圾收集点或学校指定回收点。",
            notice = "如果已经破损，应避免直接接触碎片，并按学校或社区要求处理。"
        ),

        SpecialWasteItem(
            name = "过期药品",
            category = "有害垃圾 / 药品回收",
            reason = "过期药品随意丢弃可能带来环境污染和误用风险。",
            suggestion = "建议交给药店、医院或学校指定的药品回收点；若学校没有回收点，应按当地要求处理。",
            notice = "不要随意送给他人使用，也不要混入厨余垃圾或可回收物。"
        ),

        SpecialWasteItem(
            name = "旧充电线",
            category = "电子废弃物 / 可回收物",
            reason = "旧充电线中含有金属和塑料材料，适合进入电子废弃物或可回收物回收渠道。",
            suggestion = "建议投放到电子废弃物回收点，或校园可回收物集中回收点。",
            notice = "不要和厨余垃圾混投。"
        ),

        SpecialWasteItem(
            name = "旧手机",
            category = "电子废弃物 / 专门回收",
            reason = "旧手机包含电池、金属、屏幕和电子元件，应优先进入电子废弃物回收渠道。",
            suggestion = "建议交给正规电子产品回收点，或学校组织的电子废弃物回收活动。",
            notice = "投放前建议清除个人数据，保护隐私。"
        ),

        SpecialWasteItem(
            name = "快递纸箱",
            category = "可回收物",
            reason = "快递纸箱主要由纸质材料构成，干净完整时适合回收利用。",
            suggestion = "建议拆除过多胶带，压平后投入可回收物收集点或快递站纸箱回收点。",
            notice = "如果纸箱被油污或液体严重污染，应按实际情况处理。"
        ),

        SpecialWasteItem(
            name = "泡沫包装",
            category = "其他垃圾 / 部分地区可回收",
            reason = "泡沫包装体积大、材质特殊，不同地区处理方式可能不同。",
            suggestion = "校园场景下可先按其他垃圾处理；如果学校有专门泡沫回收点，可按回收点要求投放。",
            notice = "不要和厨余垃圾混投。"
        ),

        SpecialWasteItem(
            name = "旧衣物",
            category = "可回收物 / 旧衣回收",
            reason = "较干净的旧衣物可以通过旧衣回收箱或公益回收渠道再利用。",
            suggestion = "建议清洗晾干后投入旧衣回收箱，或交给学校公益回收活动。",
            notice = "潮湿、发霉或严重污染的衣物不建议直接投入旧衣回收箱。"
        )
    )
}