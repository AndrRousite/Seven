package com.weyee.sdk.nfc.reader

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import com.weyee.sdk.nfc.Util
import com.weyee.sdk.nfc.callback.CallBackListener
import java.io.IOException
import java.util.*

/**
 * NFC读卡器
 * @author wuqi by 2019-08-22.
 */
open class CardReader(activity: Context) {
    protected var mActivity: Context = activity
    protected var handler: Handler? = null
    protected var handlerThread: HandlerThread? = null
    protected var mDefaultAdapter: NfcAdapter? = null
    protected var mIsoDep: IsoDep? = null
    protected var callback: CallBackListener? = null

    internal var isConnected: Boolean = false
        get() = field && mIsoDep?.isConnected ?: false

    init {
        mDefaultAdapter = NfcAdapter.getDefaultAdapter(activity)
    }

    open fun enableCardReader() {
        mActivity.let {
            if (!Util.isSupportNfc(mActivity)) {
                callback?.onNfcNotSupport()
                return
            }
            if (!Util.isEnableNfc(mActivity)) {
                callback?.onNfcNotEnable()
                Util.intentToNfcSetting(mActivity) // 前往设置页面
                return
            }
        }
    }

    open fun disableCardReader() {
        // TODO
    }

    /**
     * 分发搜索到的NFC
     */
    @Synchronized
    open fun dispatchTag(tag: Tag) {
        if (Arrays.toString(tag.techList).contains(IsoDep::class.java.name)) {
            connectCard(tag)
        }
    }

    /**
     * 连接NFC设备
     */
    @Synchronized
    private fun connectCard(tag: Tag) {
        mIsoDep?.let {
            // nfc设备已连接
            return
        }

        mIsoDep = IsoDep.get(tag)

        mIsoDep?.let {
            try {
                it.connect()
                doOnCardConnected(true)
            } catch (e: IOException) {
                e.printStackTrace()
                doOnCardConnected(false)
            }
        } ?: doOnCardConnected(false)
    }

    /**
     * 处理回调操作
     */
    private fun doOnCardConnected(isConnect: Boolean) {
        callback?.onNfcConnected(isConnect)

        if (!isConnect) mIsoDep = null
        else checkConnected()
    }

    /**
     * 每隔500毫秒，检测一下NFC设备是否连接
     */
    private fun checkConnected() {
        handler?.postDelayed({
            mIsoDep?.let {
                if (it.isConnected) {
                    checkConnected()
                } else {
                    doOnCardConnected(false)
                }
            } ?: doOnCardConnected(false)
        }, 500)
    }

    open fun enablePlatformSound(enableSound: Boolean) {

    }

    open fun setReaderPresenceCheckDelay(delay: Int) {

    }

    /**
     * 发送数据
     */
    @Throws(IOException::class)
    fun tranceive(data: ByteArray): ByteArray {
        return mIsoDep?.transceive(data) ?: throw TagLostException("IsoDep is null")
    }

    fun setCallBackListener(callBackListener: CallBackListener) {
        this.callback = callBackListener
        startCheckThread()
    }

    /**
     * 开启Handler线程，并处理心跳检测NFC连接
     */
    private fun startCheckThread() {
        handlerThread = HandlerThread("checkConnectThread")
        handlerThread?.let {
            it.start()
            handler = Handler(it.looper)
        }
    }

    /**
     * 销毁Handler线程，移除心跳检测
     */
    fun stopCheckThread() {
        handler?.let {
            it.removeCallbacksAndMessages(null)
            handler = null
        }
        handlerThread?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                it.quitSafely()
            } else {
                it.quit()
            }
            handlerThread = null
        }
    }

}