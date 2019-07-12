package com.weyee.sdk.print.scan.usb;

import android.hardware.usb.UsbDevice;

import java.util.Set;

/**
 * 监听USB插拔广播信息
 *
 * @author wuqi by 2019-07-11.
 */
public interface IUsbAble {
    int CHANGE = 1;

    boolean initialize();

    /**
     * 当前设备是否支持Usb
     *
     * @return
     */
    boolean isSupportUsb();

    /**
     * Usb监听
     *
     * @param usbListener
     */
    void setIUsbListener(IUsbListener usbListener);

    void destroy();


    /**
     * 获取usb列表
     *
     * @return
     */
    Set<UsbDevice> getUsbDevices();
}
