package com.wuqi.a_browser

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.*
import androidx.annotation.RequiresApi
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.Utils
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSession
import com.tencent.sonic.sdk.SonicSessionConfig
import com.weyee.poscore.base.BaseActivity
import com.weyee.poscore.di.component.AppComponent
import com.weyee.poscore.mvp.IView
import com.weyee.sdk.log.LogUtils
import com.weyee.sdk.router.Path
import kotlinx.android.synthetic.main.activity_browser.*


@Route(path = Path.Browser + "Browser")
class BrowserActivity : BaseActivity<BrowserPresenter>(), IView {

    private lateinit var url: String
    private var sonicSession: SonicSession? = null

    override fun setupActivityComponent(appComponent: AppComponent?) {
    }

    override fun getResourceId(): Int = R.layout.activity_browser

    @SuppressLint("SetJavaScriptEnabled", "RtlHardcoded")
    override fun initView(savedInstanceState: Bundle?) {
        url = intent.getStringExtra("url")

        headerView.setTitle("加载中...", Gravity.LEFT or Gravity.CENTER_VERTICAL)

        // step 1: Initialize sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(SonicRuntimeImpl(Utils.getApp()), SonicConfig.Builder().build())
        }
        // step 2: Create SonicSession
        sonicSession = SonicEngine.getInstance().createSession(url, SonicSessionConfig.Builder().build())
        if (null != sonicSession) {
            sonicSession?.bindClient(SonicClientImpl())
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following codes to feedback as a default mode.
            //throw UnknownError("create session fail!")
        }

        // step 3: BindWebView for sessionClient and bindClient for SonicSession
        // in the real world, the init flow may cost a long time as startup
        // runtime、init configs....
        webView.webViewClient = object : WebViewClient() {
            @SuppressLint("RtlHardcoded")
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                headerView.setTitle("加载中...", Gravity.LEFT or Gravity.CENTER_VERTICAL)
                return super.shouldOverrideUrlLoading(view, request)
            }

            @SuppressLint("RtlHardcoded")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                headerView.setTitle("加载中...", Gravity.LEFT or Gravity.CENTER_VERTICAL)
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                sonicSession?.sessionClient?.pageFinish(url)
                headerView.setTitle(view?.title)
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return sonicSession?.sessionClient?.requestResource(request?.url?.toString()) as WebResourceResponse?
            }

            override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
                return sonicSession?.sessionClient?.requestResource(url) as WebResourceResponse?
            }

            /**
             * support sdk version 6.0 above
             *
             * @param view
             * @param request
             * @param error
             */
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                //view.loadUrl("file:///android_asset/error.html");
                LogUtils.d("加载出错了~")
            }

            /**
             * support sdk version 6.0 below
             *
             * @param view
             * @param errorCode
             * @param description
             * @param failingUrl
             */
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                // @Deprecated js加载错误也会导致加载error.html ，so remove.
                //view.loadUrl("file:///android_asset/error.html");
                LogUtils.d("加载出错了~")
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                LogUtils.d(String.format("加载进度: %d", newProgress))
            }
        }

        val webSettings = webView.settings
        // step 4: bind javascript
        // note:if api level lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.javaScriptEnabled = true
        webView.removeJavascriptInterface("searchBoxJavaBridge_")
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis())
        webView.addJavascriptInterface(
            SonicJavaScriptInterface(sonicSession?.sessionClient, intent),
            "sonic"
        )

        // init webview settings
        webSettings.allowContentAccess = true
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.savePassword = false
        webSettings.saveFormData = false
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        // step 5: webview is ready now, just tell session client to bind
        if (sonicSession?.sessionClient != null) {
            (sonicSession?.sessionClient as SonicClientImpl).bindWebView(webView)
            sonicSession?.sessionClient?.clientReady()
        } else {
            webView.loadUrl(url)
        }

    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun useProgressAble(): Boolean {
        return !super.useProgressAble()
    }

    override fun onDestroy() {
        if (null != sonicSession) {
            sonicSession?.destroy()
            sonicSession = null
        } else {
            if (webView != null) {
                val parent = webView.parent as ViewGroup
                parent.removeView(webView)
                webView.removeAllViews()
                webView.loadUrl("about:blank")
                webView.stopLoading()
                webView.webChromeClient = null
                webView.webViewClient = null
                webView.destroy()//solve the problem of audio and video backstage play
            }
        }
        super.onDestroy()
    }

}
