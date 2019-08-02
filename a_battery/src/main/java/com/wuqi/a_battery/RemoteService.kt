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
class RemoteService : Service() {
    private lateinit var mBinder: RemoteBinder
    private lateinit var mConnection: RemoteConnection

    override fun onCreate() {
        super.onCreate()
        mBinder = RemoteBinder()
        mConnection = RemoteConnection()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        //绑定远程服务
        bindService(
            Intent(this@RemoteService, LocalService::class.java),
            mConnection,
            Context.BIND_IMPORTANT
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //todo 启动子线程执行耗时操作
        val timerTask = object : TimerTask() {
            override fun run() {
                println("RemoteService ---------- onStartCommand Service工作了")
            }
        }
        val timer = Timer()
        timer.scheduleAtFixedRate(timerTask, 1000, 1000)

        return START_STICKY
    }

    inner class RemoteBinder : IMyAidlInterface.Stub() {
        override fun getServiceName(): String = "RemoteService"
    }

    inner class RemoteConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtils.d("Remote连接远程服务成功 --------")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            startService(Intent(this@RemoteService, LocalService::class.java))
            bindService(Intent(this@RemoteService, LocalService::class.java), mConnection, Context.BIND_IMPORTANT)
        }

    }

}