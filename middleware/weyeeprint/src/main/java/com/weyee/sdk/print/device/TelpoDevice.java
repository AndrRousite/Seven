package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 天波POS58mm自带打印机
 *
 * @author wuqi by 2019-06-14.
 */
public class TelpoDevice extends BasePrinterDevice {
    public TelpoDevice() {
        super(DeviceCode.DEVICE_TPS900, PrintType.NULL);
    }

    TelpoDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
        this.printManagerAble = null;
    }

    @Override
    public void otherPrint() {

    }
}
