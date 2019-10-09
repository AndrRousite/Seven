package com.weyee.sdk.network.api19

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weyee.sdk.network.NetworkCallback
import com.weyee.sdk.network.NetworkManager
import com.weyee.sdk.network.Type

/**
 *
 * @author wuqi by 2019-10-09.
 */
class NetworkBroadcastReceiver(private val helper: NetworkCallback) : NetworkCallback,
    BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.action, true)) {
                post(helper.netType(context))
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
        NetworkManager.instance.getApplication().unregisterReceiver(this)
    }
}