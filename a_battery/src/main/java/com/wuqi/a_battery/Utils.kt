package com.wuqi.a_battery

import com.blankj.utilcode.util.Utils
import com.wuqi.a_battery.daemon.DaemonHolder
import com.wuqi.a_battery.daemon.HeartBeatService

/**
 *
 * @author wuqi by 2019-07-31.
 */
class Utils {
    companion object {
        fun init() {
            //ServiceUtils.startService(LocalService::class.java)
            //ServiceUtils.startService(RemoteService::class.java)

            //DaemonHolder.init(Utils.getApp(), HeartBeatService::class.java)
        }

        fun startService() {
            DaemonHolder.startService()
        }

        fun stopService() {
            DaemonHolder.stopService()
        }
    }
}