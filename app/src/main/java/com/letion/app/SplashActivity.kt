package com.letion.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SizeUtils
import com.weyee.possupport.arch.RxLiftUtils
import com.weyee.poswidget.stateview.view.LoadingView
import com.weyee.sdk.api.rxutil.RxJavaUtils
import com.weyee.sdk.dialog.MessageDialog
import com.weyee.sdk.permission.PermissionIntents
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import java.util.concurrent.TimeUnit


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("创建：" + System.currentTimeMillis())
        BarUtils.setStatusBarAlpha(this@SplashActivity, 112, true)
        BarUtils.setStatusBarVisibility(this@SplashActivity, false)

        val content = findViewById<ViewGroup>(android.R.id.content)
        val loadingView = LoadingView(this)

        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.CENTER
        content.addView(loadingView, params)

        val textView = TextView(this)
        textView.setTextColor(Color.parseColor("#999999"))
        textView.gravity = Gravity.CENTER_HORIZONTAL
        textView.setLineSpacing(textView.lineSpacingExtra, 1.5f)
        textView.text = String.format("v %s\n客服服务热线：400-606-0201", AppUtils.getAppVersionName())
        val params2 = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params2.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params2.bottomMargin = SizeUtils.dp2px(20f)
        content.addView(textView, params2)
    }

    override fun onResume() {
        super.onResume()
        println("展示：" + System.currentTimeMillis())
        RxJavaUtils.delay(2L, TimeUnit.SECONDS)
            .`as`(RxLiftUtils.bindLifecycle(this))
            .subscribe {
                toMain()
            }
    }

    private fun toMain() {
        AndPermission.with(this)
            .runtime()
            .permission(Permission.READ_PHONE_STATE, Permission.WRITE_EXTERNAL_STORAGE)
            .onGranted {
                val animatorX = ObjectAnimator.ofFloat(window.decorView, "scaleX", 1f, 1.15f)
                val animatorY = ObjectAnimator.ofFloat(window.decorView, "scaleY", 1f, 1.15f)

                val animatorSet = AnimatorSet()
                animatorSet.setDuration(1000).play(animatorX).with(animatorY)
                animatorSet.start()
                animatorSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        // Preview Window设置的背景图如果不做处理，图片就会一直存在于内存中
                        window.decorView.postDelayed({
                            window.setBackgroundDrawable(null)
                            finish()
                        }, 50)
                    }
                })
            }.onDenied {
                val dialog = MessageDialog(this@SplashActivity)
                dialog.setMsg("为保证您正常使用，请授予「APP」使用存储空间、获取设备识别权限")
                dialog.setCancelText("退出")
                dialog.setConfirmText("去设置")
                dialog.setOnClickCancelListener { finish() }
                dialog.setOnClickConfirmListener { PermissionIntents.toPermissionSetting(this@SplashActivity) }
                dialog.show()
            }
            .start()

    }
}
