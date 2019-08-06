package com.wuqi.a_battery

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SizeUtils
import com.hailong.biometricprompt.fingerprint.FingerprintCallback
import com.hailong.biometricprompt.fingerprint.FingerprintVerifyManager
import com.weyee.poscore.base.App
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.mvp.BaseModel
import com.weyee.poscore.mvp.BasePresenter
import com.weyee.poscore.mvp.IView
import com.weyee.sdk.imageloader.glide.GlideImageConfig
import com.weyee.sdk.router.MainNavigation
import com.weyee.sdk.router.Path
import com.weyee.sdk.toast.ToastUtils
import kotlinx.android.synthetic.main.activity_fingerprint.*

@Route(path = Path.Battery + "Fingerprint")
class FingerprintActivity : BaseActivity<BasePresenter<BaseModel, IView>>() {
    override fun setupActivityComponent(appComponent: AppComponent?) {
    }

    override fun getResourceId(): Int = R.layout.activity_fingerprint

    override fun initView(savedInstanceState: Bundle?) {
        BarUtils.setStatusBarAlpha(this@FingerprintActivity, 112, true)
        (applicationContext as App).appComponent.imageLoader().loadImage(
            context, GlideImageConfig.builder()
                .imageRadius(SizeUtils.dp2px(10f))
                .resource(R.drawable.action_bar_img)
                .imageView(button).build()
        )
    }

    override fun initData(savedInstanceState: Bundle?) {
        button.performClick()
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                val builder = FingerprintVerifyManager.Builder(this@FingerprintActivity)
                builder.callback(object : FingerprintCallback {
                    override fun onFailed() {
                        ToastUtils.show("onFailed")
                    }

                    override fun onCancel() {
                        ToastUtils.show("onCancel")
                    }

                    override fun onSucceeded() {
                        v.postDelayed({
                            MainNavigation(context).toMainActivity()
                        }, 500)
                    }

                    override fun onUsepwd() {
                        ToastUtils.show("onUsepwd")
                    }

                    override fun onNoneEnrolled() {
                        ToastUtils.show("onNoneEnrolled")
                    }

                    override fun onHwUnavailable() {
                        ToastUtils.show("onHwUnavailable")
                    }

                }).usepwdVisible(true)
                    .title("请验证指纹")
                    .description("请验证指纹")
                    .build()
            }
        }
    }

    override fun hasToolbar(): Boolean {
        return !super.hasToolbar()
    }
}
