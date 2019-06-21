package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 群索80mm打印机
 *
 * @author wuqi by 2019-06-14.
 */
public class QsDevice extends BasePrinterDevice {

    public QsDevice() {
        super(DeviceCode.DEVICE_QS, PrintType.BLE);
    }

    QsDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }
}
