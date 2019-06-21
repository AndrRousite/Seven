package com.weyee.sdk.print.device;

import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.constant.PrintType;

/**
 * 映美LQ-200KⅢ针式打印机150mm
 *
 * @author wuqi by 2019-06-14.
 */
public class JolimarkDevice extends BasePrinterDevice{
    public JolimarkDevice() {
        super(DeviceCode.DEVICE_JOLIMARK, PrintType.NULL);
    }

    private JolimarkDevice(int deviceCode, int printType) {
        super(deviceCode, printType);
    }

    @Override
    public void otherPrint() {

    }
}
