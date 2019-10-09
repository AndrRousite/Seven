package com.wuqi.a_service

import android.annotation.SuppressLint
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.mvp.BaseModel
import com.weyee.poscore.mvp.BasePresenter
import com.weyee.poscore.mvp.IView
import com.weyee.sdk.event.Bus
import com.weyee.sdk.network.NetworkTypeEvent
import com.weyee.sdk.network.Type
import com.weyee.sdk.router.Path
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_network.*

@Route(path = Path.Service + "Network")
class NetworkActivity : BaseActivity<BasePresenter<BaseModel, IView>>() {
    override fun setupActivityComponent(appComponent: AppComponent?) {
    }

    override fun getResourceId(): Int = R.layout.activity_network

    override fun initView(savedInstanceState: Bundle?) {
    }

    @SuppressLint("SetTextI18n")
    override fun initData(savedInstanceState: Bundle?) {
        Bus.getDefault()
            .subscribe(
                this,
                "RxBus",
                AndroidSchedulers.mainThread(),
                com.weyee.sdk.event.Callback<NetworkTypeEvent> {
                    when (it.type) {
                        Type.NONE -> tvContent.text = "网络断开"
                        Type.MOBILE -> tvContent.text = "移动网络"
                        Type.WIFI -> tvContent.text = "WiFi连接"
                        Type.AUTO -> tvContent.text = "内容"
                    }
                })
    }

    override fun useEventBus(): Boolean {
        return true
    }

}
