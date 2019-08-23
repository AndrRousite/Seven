package com.weyee.sdk.nfc.reader

import android.content.Context
import android.os.Build

/**
 * 读卡器模式工厂类，根据系统版本生产相应CardReader.
 * @author wuqi by 2019-08-23.
 */
object CardReaderFactory {
    fun productCardReader(activity: Context): CardReader {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            KikKatCardReader(activity)
        } else {
            JellyBeanCardReader(activity)
        }
    }
}