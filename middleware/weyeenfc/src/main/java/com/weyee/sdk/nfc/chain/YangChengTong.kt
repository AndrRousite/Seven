package com.weyee.sdk.nfc.chain

import com.weyee.sdk.nfc.Util

/**
 * Created by ZP on 2017/9/21.
 */
class YangChengTong : BaseCardEntity() {

    /**
     * 解析卡片信息.
     *
     */
    fun parseCardInfo(resp: ByteArray): Boolean {
        return try {
            val src = Util.ByteArrayToHexString(resp)
            cardNumber = src.substring(22, 32)
            effectiveDate = src.substring(46, 54)
            expiredDate = src.substring(54, 62)
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }

    }

    /**
     * 解析卡片余额.
     *
     * @param src 原始16进制数据
     */
    fun parseCardBalance(src: ByteArray): Boolean {
        return try {
            balance = Util.hexToInt(src, 0, src.size - 2).toLong()
            true
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }

    }
}
