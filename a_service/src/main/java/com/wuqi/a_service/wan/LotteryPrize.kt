package com.wuqi.a_service.wan

import com.google.gson.annotations.SerializedName

/**
 *
 * @author wuqi by 2019/5/20.
 */

data class LotteryPrize(
    @SerializedName(value = "prize_money", alternate = ["prize_amount"])
    val prize_money: String,
    val prize_name: String,
    val prize_num: String,
    val prize_require: String
)