package com.weyee.sdk.nfc

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.util.Log
import com.weyee.sdk.nfc.callback.CallBackListener
import com.weyee.sdk.nfc.reader.CardReader
import com.weyee.sdk.nfc.reader.CardReaderFactory
import com.weyee.sdk.nfc.reader.INfcManager
import java.io.IOException

/**
 * Nfc 读卡器管理类
 * @author wuqi by 2019-08-22.
 */
class NfcCardReaderManager(builder: Builder) : INfcManager {
    private var activity: Context? = null
    private var cardReader: CardReader? = null
    private var enableSound: Boolean = false
    private var delay: Int = 0
    private var callback: CallBackListener? = null

    init {
        this.activity = builder.activity
        this.cardReader = builder.cardReader
        this.enableSound = builder.enableSound
        this.delay = builder.delay
        this.callback = builder.callback

        if (cardReader == null) {
            // 外部没有传入NFC处理，则使用工厂自动创建
            cardReader = activity?.let { CardReaderFactory.productCardReader(it) }
        }

        cardReader?.let {
            it.enablePlatformSound(enableSound)
            it.setReaderPresenceCheckDelay(delay)
            callback?.let { it1 -> it.setCallBackListener(it1) }
        }
    }

    override fun isConnect(): Boolean {
        return cardReader?.isConnected ?: false
    }

    override fun onCreate(intent: Intent) {
        dispatchIntent(intent)
    }

    override fun onStart() {
    }

    override fun onResume() {
        cardReader?.enableCardReader()
    }

    override fun onPause() {
        cardReader?.disableCardReader()
    }

    override fun onStop() {

    }

    override fun onDestroy() {
        cardReader?.let {
            it.stopCheckThread()
            cardReader = null
            activity = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        dispatchIntent(intent)
    }

    @Throws(IOException::class)
    override fun sendData(data: ByteArray): String {
        cardReader?.let {
            return Util.ByteArrayToHexString(it.tranceive(data))
        } ?: throw IOException("card reader not null")
    }

    @Throws(IOException::class)
    override fun sendData(hexData: String): String {
        return sendData(Util.HexStringToByteArray(hexData))
    }

    @Throws(IOException::class)
    override fun tranceive(data: ByteArray): ByteArray {
        cardReader?.let {
            return it.tranceive(data)
        } ?: throw IOException("card reader not null")
    }

    @Throws(IOException::class)
    override fun tranceive(hexData: String): ByteArray {
        return tranceive(Util.HexStringToByteArray(hexData))
    }

    private fun dispatchIntent(intent: Intent?) {
        if (intent != null) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                cardReader?.dispatchTag(tag)
            } else {
                Log.d("NfcCardReaderManager", "dispatchIntent: tag is null")
            }
        }
    }


    class Builder {
        internal var activity: Context? = null
        internal var cardReader: CardReader? = null
        internal var enableSound: Boolean = false
        internal var delay: Int = 0
        internal var callback: CallBackListener? = null

        constructor(activity: Context) {
            this.activity = activity
        }

        constructor(copy: NfcCardReaderManager) {
            this.activity = copy.activity
            this.cardReader = copy.cardReader
            this.enableSound = copy.enableSound
            this.delay = copy.delay
            this.callback = copy.callback
        }

        fun cardReader(cardReader: CardReader?): Builder {
            this.cardReader = cardReader
            return this
        }

        fun enableSound(enableSound: Boolean): Builder {
            this.enableSound = enableSound
            return this;
        }

        fun delay(delay: Int): Builder {
            this.delay = delay
            return this
        }

        fun callback(callback: CallBackListener): Builder {
            this.callback = callback
            return this
        }

        fun build(): NfcCardReaderManager {
            return NfcCardReaderManager(this)
        }
    }
}