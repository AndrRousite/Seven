package com.wuqi.a_browser

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceResponse
import com.tencent.sonic.sdk.SonicRuntime
import com.tencent.sonic.sdk.SonicSessionClient
import com.weyee.poscore.base.ThreadPool
import java.io.File
import java.io.InputStream


/**
 *
 * @author wuqi by 2019-06-06.
 */
class SonicRuntimeImpl(context: Context) : SonicRuntime(context) {
    override fun showToast(text: CharSequence?, duration: Int) {
    }

    override fun log(tag: String?, level: Int, message: String?) {
        when (level) {
            Log.ERROR -> Log.e(tag, message)
            Log.INFO -> Log.i(tag, message)
            else -> Log.d(tag, message)
        }
    }

    override fun getUserAgent(): String {
        return "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Mobile Safari/537.36"
    }

    override fun isNetworkValid(): Boolean = true

    override fun postTaskToThread(task: Runnable?, delayMillis: Long) {
        ThreadPool.run(task)
    }

    override fun isSonicUrl(url: String?): Boolean = true

    override fun setCookie(url: String?, cookies: MutableList<String>?): Boolean {
        if (!TextUtils.isEmpty(url) && cookies != null && cookies.size > 0) {
            val cookieManager = CookieManager.getInstance()
            for (cookie in cookies) {
                cookieManager.setCookie(url, cookie)
            }
            return true
        }
        return false
    }

    override fun getCookie(url: String?): String? {
        return if (TextUtils.isEmpty(url)) "" else CookieManager.getInstance().getCookie(url)
    }

    override fun createWebResourceResponse(
        mimeType: String?,
        encoding: String?,
        data: InputStream?,
        headers: MutableMap<String, String>?
    ): Any {
        val resourceResponse = WebResourceResponse(mimeType, encoding, data)
        if (SDK_INT >= VERSION_CODES.LOLLIPOP) {
            resourceResponse.responseHeaders = headers
        }
        return resourceResponse
    }

    override fun getCurrentUserAccount(): String {
        return "sonic-demo-master"
    }

    override fun notifyError(client: SonicSessionClient?, url: String?, errorCode: Int) {
    }

    override fun getSonicResourceCacheDir(): File {
        if (BuildConfig.DEBUG) {
            val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "sonic/"
            val file = File(path.trim { it <= ' ' })
            if (!file.exists()) {
                file.mkdir()
            }
            return file
        }
        return super.getSonicCacheDir()
    }

}