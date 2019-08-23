package com.weyee.sdk.nfc.reader

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcF

/**
 * API 小于 19 NFC读卡器模式.
 * Created by ZP on 2017/9/20.
 */

class JellyBeanCardReader(activity: Context) : CardReader(activity) {


    private val mPendingIntent: PendingIntent = PendingIntent.getActivity(
            mActivity, 0, Intent(mActivity, mActivity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

    override fun enableCardReader() {
        super.enableCardReader()
        val techListsArray = arrayOf(arrayOf(NfcF::class.java.name), arrayOf(IsoDep::class.java.name))
        mDefaultAdapter?.enableForegroundDispatch(mActivity as Activity?, mPendingIntent, null, techListsArray)

    }

    override fun disableCardReader() {
        super.disableCardReader()
        mDefaultAdapter?.disableForegroundDispatch(mActivity as Activity?)
    }
}
