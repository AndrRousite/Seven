package com.weyee.sdk.print.device.external;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.weyee.sdk.print.Interface.IExternalAble;
import com.weyee.sdk.print.constant.ConnectStatus;
import com.weyee.sdk.print.listener.PrintConnectListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuqi by 2019-06-14.
 */
public abstract class BasePrintManager implements IExternalAble {
    protected ExecutorService writeThreadExecutor;  // 发送数据的线程池
    protected ExecutorService connectThreadExecutor;  // 连接设备的线程池

    protected PrintConnectListener listener;

    @SuppressLint("HandlerLeak")
    protected final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConnectStatus.STATE_CONNECTING: // 正在连接
                    if (listener != null) {
                        listener.onStart();
                    }
                    break;
                case ConnectStatus.STATE_CONNECTED: // 已连接连接
                case ConnectStatus.STATE_DISCONNECTING: // 正在断开连接
                case ConnectStatus.STATE_WRITE_BEGIN: // 正在写入数据
                    if (listener != null) {
                        listener.onProcess(msg.what);
                    }
                    break;
                case ConnectStatus.STATE_DISCONNECTED: // 已断开
                    if (listener != null) {
                        listener.onError(-1, "设备连接失败");
                        listener.onComplete();
                    }
                    break;
                case ConnectStatus.STATE_WRITE_SUCCESS: // 写入数据完成
                    if (listener != null) {
                        listener.onSuccess();
                        listener.onComplete();
                    }
                    break;
                case ConnectStatus.STATE_WRITE_FAILURE: // 写入数据失败
                    if (listener != null) {
                        listener.onError(-2, "发送数据失败");
                        listener.onComplete();
                    }
                    break;
            }
        }
    };

    @Override
    public void create() {
        // 创建单线程的线程池，保证每次数据队列有序的发送数据
        if (writeThreadExecutor == null) {
            writeThreadExecutor = Executors.newSingleThreadExecutor();
        }
        if (connectThreadExecutor == null) {
            connectThreadExecutor = Executors.newSingleThreadExecutor();
        }
    }

    @Override
    public void fail() {

    }

    @Override
    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void connect(String address, int port) {
        mHandler.obtainMessage(ConnectStatus.STATE_CONNECTING, -1, -1, null).sendToTarget();
    }

    @Override
    public void write(byte[] bytes) {

    }

    @Override
    public void disconnect() {
        mHandler.obtainMessage(ConnectStatus.STATE_DISCONNECTED, -1, -1, null).sendToTarget();
    }

    @Override
    public void resetConfig() {

    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public int splitLength() {
        return 2048;
    }

    @Override
    public void callback(PrintConnectListener listener) {
        this.listener = listener;
    }
}
