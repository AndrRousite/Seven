package com.weyee.sdk.network

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import com.weyee.sdk.network.api19.NetworkBroadcastReceiver
import com.weyee.sdk.network.api21.NetworkCallbackImpl

/**
 *
 * @author wuqi by 2019-10-09.
 */
class NetworkManager {
    companion object SingletonHolder {
        val instance = NetworkManager()

    }

    private lateinit var application: Application
    private lateinit var callback: NetworkCallback

    @SuppressLint("MissingPermission")
    fun init(application: Application) {
        this.application = application
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            callback = NetworkBroadcastReceiver(NetworkHelper2())
            val filter = IntentFilter()
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            application.registerReceiver(callback as NetworkBroadcastReceiver, filter)
        } else {
            callback = NetworkCallbackImpl(NetworkHelper2())
            val cmgr =
                application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cmgr?.registerDefaultNetworkCallback(callback as NetworkCallbackImpl)
            } else {
                val request = NetworkRequest.Builder().build()
                cmgr?.registerNetworkCallback(request, callback as NetworkCallbackImpl)
            }
        }
    }

    fun register(`object`: Any) {
        callback.register(`object`)
    }

    fun unregister(`object`: Any) {
        callback.unregister(`object`)
    }

    fun unregisterAll() {
        callback.unregisterAll()
    }

    fun getApplication(): Application {
        return application
    }

}