package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;

/**
 * 佳博80mm打印机
 *
 * @author wuqi by 2019-06-14.
 */
public class GprinterDevice extends BasePrinterDevice {

    public GprinterDevice(int printType) {
        // 佳博打印机支持蓝牙和USB打印
        super(DeviceCode.DEVICE_GPRINTER, printType);
    }

    private GprinterDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }

    @Override
    public void otherPrint() {

    }
}
