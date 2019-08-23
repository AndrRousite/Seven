package com.weyee.sdk.nfc.callback

/**
 *
 * @author wuqi by 2019-08-22.
 */
interface CallBackListener {
    /**
     * 手机不支持NFC.
     */
    fun onNfcNotSupport()

    /**
     * 手机支持NFC,但未开启.
     */
    fun onNfcNotEnable()

    /**
     * CPU卡是否被NFC检测到.
     *
     * @param isConnected true 已连接 false 未连接
     */
    fun onNfcConnected(isConnected: Boolean)
}