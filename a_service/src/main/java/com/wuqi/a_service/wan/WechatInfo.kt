package com.wuqi.a_service.wan

/**
 *
 * @author wuqi by 2019-06-06.
 */
data class WechatInfo(
    val list: List<WechatItem>,
    val pno: Int,
    val ps: Int,
    val totalPage: Int
)

data class WechatItem(
    val firstImg: String,
    val id: String,
    val mark: String,
    val source: String,
    val title: String,
    val url: String
)
