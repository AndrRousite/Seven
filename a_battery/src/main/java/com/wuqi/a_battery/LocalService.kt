package com.wuqi.a_battery

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.weyee.sdk.log.LogUtils
import java.util.*

/**
 *
 * @author wuqi by 2019-07-31.
 */
class LocalService : Service() {
    private lateinit var mBinder: LocalBinder
    private lateinit var mConnection: LocalConnection

    override fun onCreate() {
        super.onCreate()
        mBinder = LocalBinder()
        mConnection = LocalConnection()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        //绑定远程服务
        bindService(
            Intent(this@LocalService, RemoteService::class.java),
            mConnection,
            Context.BIND_IMPORTANT
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //todo 启动子线程执行耗时操作
        val timerTask = object : TimerTask() {
            override fun run() {
                LogUtils.d("LocalService ---------- onStartCommand Service工作了")
            }
        }
        val timer = Timer()
        timer.scheduleAtFixedRate(timerTask, 1000, 1000)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class LocalBinder : IMyAidlInterface.Stub() {
        override fun getServiceName(): String = "LocalService"
    }

    inner class LocalConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtils.d("Local连接远程服务成功 --------")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            startService(Intent(this@LocalService, RemoteService::class.java))
            bindService(Intent(this@LocalService, RemoteService::class.java), mConnection, Context.BIND_IMPORTANT)
        }

    }

}