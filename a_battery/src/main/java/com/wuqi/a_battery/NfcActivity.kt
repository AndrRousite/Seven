package com.wuqi.a_battery

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.mvp.BaseModel
import com.weyee.poscore.mvp.BasePresenter
import com.weyee.poscore.mvp.IView
import com.weyee.sdk.nfc.NfcCardReaderManager
import com.weyee.sdk.nfc.callback.CallBackListener
import com.weyee.sdk.nfc.chain.BaseCardEntity
import com.weyee.sdk.nfc.chain.NfcClient
import com.weyee.sdk.nfc.chain.SZTReader
import com.weyee.sdk.nfc.chain.YCTReader
import com.weyee.sdk.router.Path
import com.weyee.sdk.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_nfc.*
import java.io.IOException


@Route(path = Path.Battery + "Nfc")
class NfcActivity : BaseActivity<BasePresenter<BaseModel, IView>>() {
    private lateinit var nfcCardReaderManager: NfcCardReaderManager
    private lateinit var nfcClient: NfcClient

    override fun setupActivityComponent(appComponent: AppComponent?) {
    }

    override fun getResourceId(): Int = R.layout.activity_nfc

    override fun initView(savedInstanceState: Bundle?) {
        nfcCardReaderManager = NfcCardReaderManager.Builder(this)
            .enableSound(true)
            .delay(500)
            .cardReader(null)
            .callback(object : CallBackListener {
                override fun onNfcNotSupport() {
                    ToastUtils.show("当前机器不支持Nfc")
                }

                override fun onNfcNotEnable() {
                    ToastUtils.show("当前机器未打开Nfc")
                }

                override fun onNfcConnected(isConnected: Boolean) {
                    ToastUtils.show(if (isConnected) "感应到NFC" else "未感应到NFC")
                    if (isConnected) execute()
                }

            }).build()
        nfcClient = NfcClient.Builder().nfcManager(nfcCardReaderManager)
            .addReader(SZTReader())
            .addReader(YCTReader())
            .build()

    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    private fun execute() {
        val cardInfo: BaseCardEntity?
        try {
            cardInfo = nfcClient.execute()
            runOnUiThread {
                textView.text = cardInfo?.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcCardReaderManager.onCreate(intent)
    }

    override fun onResume() {
        super.onResume()
        nfcCardReaderManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        nfcCardReaderManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        nfcCardReaderManager.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcCardReaderManager.onNewIntent(intent)
    }
}
