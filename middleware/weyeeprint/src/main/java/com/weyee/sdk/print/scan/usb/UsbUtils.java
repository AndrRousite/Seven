package com.weyee.sdk.print.scan.usb;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import androidx.annotation.NonNull;
import com.blankj.utilcode.util.ServiceUtils;
import com.weyee.sdk.print.constant.PaperSize;
import com.weyee.sdk.toast.ToastUtils;

import java.util.Collections;
import java.util.Set;

/**
 * 蓝牙工具类
 *
 * @author wuqi by 2019-06-18.
 */
public class UsbUtils {
    private IUsbAble iUsbAble;
    private int currentPaperSize;
    private IUsbListener listener;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof UsbService.LocalBinder) {
                iUsbAble = ((UsbService.LocalBinder) service).getService();
            }
            if (iUsbAble == null) return;
            if (iUsbAble.initialize()) {
                if (iUsbAble.isSupportUsb()) {
                    callback();
                }
            } else {
                ToastUtils.show("not support Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iUsbAble = null;
        }
    };

    public UsbUtils(int currentPaperSize, IUsbListener listener) {
        this.currentPaperSize = currentPaperSize;
        this.listener = listener;
    }

    /**
     * 绑定服务
     */
    public void bindService() {
        ServiceUtils.bindService(UsbService.class, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务
     */
    public void unBindService() {
        try {
            ServiceUtils.unbindService(serviceConnection);
        } catch (IllegalArgumentException ignore) {
        }
    }


    /**
     * 获取usb设备列表
     *
     * @return
     */
    public Set<UsbDevice> getUsbDevices() {
        if (iUsbAble != null) {
            return iUsbAble.getUsbDevices();
        }
        return Collections.emptySet();
    }

    private void callback() {
        if (iUsbAble != null) {
            iUsbAble.setIUsbListener(devices -> {
                if (listener != null) {
                    listener.onUsbDevicesChange(filterDevice(devices));
                }
            });
        }
    }

    /**
     * 过滤蓝牙设备，根据选择的打印机尺寸过滤规则
     */
    private Set<UsbDevice> filterDevice(@NonNull Set<UsbDevice> devices) {
        switch (currentPaperSize) {
            case PaperSize.PAPER_SIZE_58:
            case PaperSize.PAPER_SIZE_80:
            case PaperSize.PAPER_SIZE_110:
            case PaperSize.PAPER_SIZE_150:
            case PaperSize.PAPER_SIZE_210:
            default:
                return devices;
        }
    }
}
