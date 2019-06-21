package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 得实210mm针式打印机
 *
 * @author wuqi by 2019-06-14.
 */
public class DsDevice extends BasePrinterDevice {

    public DsDevice() {
        // 暂时只支持蓝牙ESC打印，后续如果支持别的连接方式，则需要开放构造器，调用者需传递print type进行连接
        super(DeviceCode.DEVICE_DS, PrintType.BLE);
    }

    private DsDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }
}
