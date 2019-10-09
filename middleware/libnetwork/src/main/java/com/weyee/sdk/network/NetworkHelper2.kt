package com.weyee.sdk.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.weyee.sdk.event.Bus


/**
 * @author wuqi by 2019-10-09.
 */
class NetworkHelper2 : NetworkCallback{

    override fun post(type: Type) {
        Bus.getDefault().post(NetworkTypeEvent(type))
    }

    override fun register(observer: Any) {
        Bus.getDefault().register(observer)
    }

    override fun unregister(observer: Any) {
        Bus.getDefault().unregister(observer)
    }

    override fun unregisterAll() {

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