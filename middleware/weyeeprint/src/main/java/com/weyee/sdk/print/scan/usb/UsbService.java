package com.weyee.sdk.print.scan.usb;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import androidx.annotation.Nullable;
import com.weyee.sdk.util.Tools;

import java.util.*;

/**
 * @author wuqi by 2019-07-11.
 */
public class UsbService extends Service implements IUsbAble {


    private final IBinder mBinder = new UsbService.LocalBinder();

    private UsbManager usbManager;
    private UsbReceiver usbReceiver;

    private IUsbListener usbListener;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CHANGE) {
                if (usbListener != null) {
                    usbListener.onUsbDevicesChange(getUsbDevices());
                }
            }
        }
    };

    @Override
    public boolean initialize() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        usbReceiver = new UsbReceiver();
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        Tools.getApp().registerReceiver(usbReceiver, usbFilter);
        return usbManager != null;
    }

    @Override
    public boolean isSupportUsb() {
        return true;
    }

    @Override
    public void setIUsbListener(IUsbListener usbListener) {
        this.usbListener = usbListener;
    }

    @Override
    public void destroy() {
        if (usbReceiver != null) {
            Tools.getApp().unregisterReceiver(usbReceiver);
            usbReceiver = null;
        }
    }

    @Override
    public Set<UsbDevice> getUsbDevices() {
        if (usbManager != null) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            Set<UsbDevice> usbDevices = new HashSet<>();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                usbDevices.add(device);
            }
            return usbDevices;
        }
        return Collections.emptySet();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        destroy();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    class UsbReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        mHandler.obtainMessage(CHANGE, -1, -1, null).sendToTarget();
                        break;
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        mHandler.obtainMessage(CHANGE, -1, -1, null).sendToTarget();
                        break;
//                    case ACTION_USB_DEVICE_PERMISSION:
//                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                        if (device != null) {
//                            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                                LogUtils.d("USBReceiver: " + "获取权限成功：" + device.getDeviceName());
//                            } else {
//                                LogUtils.d("USBReceiver: " + "获取权限失败：" + device.getDeviceName());
//                            }
//                        }
//                        break;
                    default:
                        break;
                }
            }
        }

    }
}
