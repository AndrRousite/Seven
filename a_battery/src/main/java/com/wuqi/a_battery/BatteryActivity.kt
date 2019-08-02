package com.wuqi.a_battery

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.mvp.BaseModel
import com.weyee.poscore.mvp.BasePresenter
import com.weyee.poscore.mvp.IView
import com.weyee.sdk.router.Path

@Route(path = Path.Battery + "Battery")
class BatteryActivity : BaseActivity<BasePresenter<BaseModel, IView>>() {
    override fun setupActivityComponent(appComponent: AppComponent?) {
    }

    override fun getResourceId(): Int = R.layout.activity_battery

    override fun initView(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarAlpha(this@BatteryActivity, 112, true)
        BarUtils.setStatusBarVisibility(this@BatteryActivity, true)
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun hasToolbar(): Boolean {
        return !super.hasToolbar()
    }

    fun onClick(v : View){
        when(v.id){
            R.id.startService -> Utils.startService()
            R.id.stopService -> Utils.stopService()
        }
    }
}
