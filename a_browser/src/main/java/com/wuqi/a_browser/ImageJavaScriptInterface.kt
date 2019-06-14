package com.wuqi.a_browser

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ActivityUtils
import com.weyee.sdk.log.LogUtils

/**
 *
 * @author wuqi by 2019-06-14.
 */
class ImageJavaScriptInterface {
    @JavascriptInterface
    fun imageClick(index: Int, urls: Array<String>?, alts: Array<String>?) {
        LogUtils.d(String.format("----点击了图片 url: %s", urls?.reduce { acc, s -> "$acc,$s" }))
        //创建一个隐式的 Intent 对象：Category 类别
        val intent = Intent()
        intent.action = "com.letion.app.photoview"
        intent.addCategory("com.letion.app.category.photoview")
        val bundle = Bundle()
        bundle.putInt("index", index)
        bundle.putStringArray("urls", urls)
        intent.putExtras(bundle)
        ActivityUtils.startActivity(intent)
    }

    @JavascriptInterface
    fun imageClick(url: String?, alt: String?) {
        LogUtils.d(String.format("----点击了图片 url: %s", url))
        //创建一个隐式的 Intent 对象：Category 类别
        val intent = Intent()
        intent.action = "com.letion.app.photoview"
        intent.addCategory("com.letion.app.category.photoview")
        val bundle = Bundle()
        bundle.putStringArray("urls", arrayOf(url))
        intent.putExtras(bundle)
        ActivityUtils.startActivity(intent)
    }

    @JavascriptInterface
    fun textClick(href: String?, target: String?) {
        LogUtils.d(String.format("----点击了链接：%s", href))
    }
}