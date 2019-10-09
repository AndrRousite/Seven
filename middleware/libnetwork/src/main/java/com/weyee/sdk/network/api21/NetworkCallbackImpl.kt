package com.weyee.sdk.network.api21

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.weyee.sdk.network.NetworkCallback
import com.weyee.sdk.network.NetworkManager
import com.weyee.sdk.network.Type


/**
 *
 * @author wuqi by 2019-10-09.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class NetworkCallbackImpl(private val helper: NetworkCallback) : NetworkCallback,
    ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network?) {
        super.onAvailable(network)
        // 网络连接
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            post(helper.netType(NetworkManager.instance.getApplication()))
        }
    }

    override fun onLost(network: Network?) {
        super.onLost(network)
        // 网络断开
        post(Type.NONE)
    }

    override fun onCapabilitiesChanged(
        network: Network?,
        networkCapabilities: NetworkCapabilities?
    ) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCapabilities?.let {
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        // WiFi连接
                        post(Type.WIFI)
                    } else {
                        // 移动数据连接
                        post(Type.MOBILE)
                    }
                }
            }
        }
    }

    override fun post(type: Type) {
        helper.post(type)
    }

    override fun register(observer: Any) {
        helper.register(observer)
    }

    override fun unregister(observer: Any) {
        helper.unregister(observer)
    }

    override fun unregisterAll() {
        helper.unregisterAll()
        val cmgr =
            NetworkManager.instance.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cmgr?.unregisterNetworkCallback(this)
    }
}