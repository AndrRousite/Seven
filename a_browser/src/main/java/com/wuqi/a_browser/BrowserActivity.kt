package com.wuqi.a_browser

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
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
                resolveElements()
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
        webView.addJavascriptInterface(ImageJavaScriptInterface(), "injectedObject")

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

    /**
     * 处理部分标签，并给他添加js代码，通过js调用Java处理
     */
    private fun resolveElements() {
// 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // 如要点击一张图片在弹出的页面查看所有的图片集合,则获取的值应该是个图片数组
        webView.loadUrl(
            "javascript:(function(){" +
                    "var objs = document.getElementsByTagName(\"img\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"alt\"));}" +
                    "}" +
                    "})()"
//            "javascript:(function () {" +
//                    "    var objs = document.getElementsByTagName(\"img\");" +
//                    "    var links = [], alts = [];" +
//                    "    for (var i = 0; i < objs.length; i++) {" +
//                    "        links.push(objs[i].getAttribute(\"src\"));" +
//                    "        alts.push(objs[i].getAttribute(\"alt\"));" +
//                    "    }" +
//                    "    for (var i = 0; i < objs.length; i++) {" +
//                    "        objs[i].onclick = function () {" +
//                    "            window.injectedObject.imageClick(i, links, alts);" +
//                    "        }" +
//                    "    }" +
//                    "})()"
        )
        // 遍历所有的a节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
        webView.loadUrl(
            "javascript:(function(){" +
                    "var objs =document.getElementsByTagName(\"a\");" +
                    "for(var i=0;i<objs.length;i++)" +
                    "{" +
                    "objs[i].onclick=function(){" +
                    "window.injectedObject.textClick(this.getAttribute(\"href\"),this.getAttribute" +
                    "(\"target\"));}" +
                    "}" +
                    "})()"
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack()
                return true
            } else {
                webView.loadUrl("about:blank")
            }
        }
        return super.onKeyDown(keyCode, event)
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
