package com.weyee.sdk.print;

import android.util.SparseArray;
import com.weyee.sdk.print.constant.DeviceCode;
import com.weyee.sdk.print.device.*;
import com.weyee.sdk.print.listener.PrintConnectListener;

/**
 * @author wuqi by 2019-06-28.
 */
public class PrintManager {

    private static PrintManager instance;

    private SparseArray<BaseDevice> deviceSparseArray = new SparseArray<>();
    private BaseDevice mCurrentDevice = null;

    private PrintManager() {
        deviceSparseArray.put(DeviceCode.DEVICE_TPS900, new TelpoDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_HUIZHI, new HuaZhiDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_GPRINTER, new GprinterDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_QS, new QsDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_ZONERICH, new ZonerichDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_CS, new CsDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_JOLIMARK, new JolimarkDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_EPSON, new EpsonDevice());
        deviceSparseArray.put(DeviceCode.DEVICE_DS, new DsDevice());

        switchDevice(DeviceCode.DEVICE_QS);
    }

    public static PrintManager getInstance() {
        if (instance == null) {
            synchronized (PrintManager.class) {
                if (instance == null) {
                    instance = new PrintManager();
                }
            }
        }
        return instance;
    }

    /**
     * 切换打印机
     *
     * @param deviceCode
     */
    public void switchDevice(int deviceCode) {
        mCurrentDevice = deviceSparseArray.get(deviceCode);
    }

    /**
     * 获取当前打印机的机器编号
     *
     * @return
     */
    public int getCurrentDeviceCode() {
        if (mCurrentDevice != null) return mCurrentDevice.getDeviceCode();
        return DeviceCode.DEVICE_NULL;
    }

    public void connect(String address, PrintConnectListener listener){
        mCurrentDevice.connect(address, listener);
    }

    public void printLines(byte[] bytes) {
       mCurrentDevice.write(bytes);
    }
}
