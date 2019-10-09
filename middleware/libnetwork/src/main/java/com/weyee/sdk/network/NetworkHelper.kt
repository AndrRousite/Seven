package com.weyee.sdk.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager


/**
 *
 * @see NetworkHelper2
 * @author wuqi by 2019-10-09.
 */
@Deprecated("使用反射，废弃")
class NetworkHelper : NetworkCallback{
    private val observers: MutableMap<Any, MutableList<MethodManager>> by lazy { mutableMapOf<Any, MutableList<MethodManager>>() }

    override fun post(type: Type) {
        val sets = observers.keys
        for (observer in sets) {
            val methodList = observers[observer]
            methodList?.forEach {
                if (it.type.isAssignableFrom(type.javaClass)) {
                    if (it.netType === type || type === Type.NONE || it.netType === Type.AUTO) {
                        Invoke.invoke(it, observer, type)
                    }
                }
            }

        }
    }

    override fun register(observer: Any) {
        var o = observers[observer]
        if (o == null) {
            o = Invoke.getAnnotationMethod(observer)
            observers[observer] = o
        }
    }

    override fun unregister(observer: Any) {
        if (observers.isNotEmpty()) {
            observers.remove(observer)
        }
    }

    override fun unregisterAll() {
        if (observers.isNotEmpty()) {
            observers.clear()
        }
    }

    @SuppressLint("MissingPermission")
    override fun netType(context: Context?): Type {
        context?.let {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            //获取当前激活的网络连接状态
            val networkInfo = connMgr.activeNetworkInfo ?: return Type.NONE

            val nType = networkInfo.type
            if (nType == ConnectivityManager.TYPE_MOBILE) {
                return Type.MOBILE
            } else if (nType == ConnectivityManager.TYPE_WIFI) {
                return Type.WIFI
            }
        }
        return Type.NONE
    }
}