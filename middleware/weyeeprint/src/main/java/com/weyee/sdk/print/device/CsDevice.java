package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 芝科打印机110mm
 *
 * @author wuqi by 2019-06-14.
 */
public class CsDevice extends BasePrinterDevice {

    public CsDevice() {
        super(DeviceCode.DEVICE_CS, PrintType.BLE);
    }

    private CsDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }
}
