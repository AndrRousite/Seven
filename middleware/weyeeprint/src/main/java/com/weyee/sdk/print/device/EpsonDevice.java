package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 爱普生210mm打印机
 *
 * @author wuqi by 2019-06-14.
 */
public class EpsonDevice extends BasePrinterDevice {

    public EpsonDevice() {
        super(DeviceCode.DEVICE_EPSON, PrintType.BLE);
    }

    private EpsonDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }
}
