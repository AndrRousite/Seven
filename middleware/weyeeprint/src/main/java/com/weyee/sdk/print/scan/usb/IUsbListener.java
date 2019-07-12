package com.weyee.sdk.print.scan.usb;

import android.hardware.usb.UsbDevice;

import java.util.Set;

/**
 * @author wuqi by 2019-06-19.
 */
public interface IUsbListener {

    void onUsbDevicesChange(Set<UsbDevice> devices);
}
