package com.weyee.sdk.nfc.reader

import android.content.Intent
import java.io.IOException


/**
 * 提供外部调用的NFC相关功能
 */
interface INfcManager {
    fun isConnect(): Boolean

    fun onCreate(intent: Intent)

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onDestroy()

    fun onNewIntent(intent: Intent)

    @Throws(IOException::class)
    fun sendData(data: ByteArray): String

    @Throws(IOException::class)
    fun sendData(hexData: String): String

    @Throws(IOException::class)
    fun tranceive(data: ByteArray): ByteArray

    @Throws(IOException::class)
    fun tranceive(hexData: String): ByteArray
}