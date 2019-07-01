package com.weyee.sdk.print.scan.ble;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * 蓝牙连接
 * 实现方式有两种：经典蓝牙和低功耗蓝牙
 *
 * @author wuqi by 2019-06-16.
 */
public interface IBluetoothAble {

    int SCAN = 1;

    boolean initialize();

    /**
     * 当前设备是否支持蓝牙
     *
     * @return
     */
    boolean isSupportBle();

    /**
     * 蓝牙是否打开
     *
     * @return
     */
    boolean isEnableBluetooth();

    /**
     * 打开/关闭蓝牙连接
     *
     * @param enable
     * @return
     */
    boolean enableBluetooth(boolean enable);

    /**
     * 蓝牙监听
     *
     * @param bluetoothLeListener
     */
    void setIBluetoothListener(IBluetoothListener bluetoothLeListener);

    /**
     * 开始搜索设备
     */
    void startDiscovery();

    /**
     * @param scanPeriod 超时时间
     */
    void startDiscovery(long scanPeriod);

    /**
     * 结束搜索设备
     */
    void cancelDiscovery();

    /**
     * 是否是扫描状态
     *
     * @return
     */
    boolean isScanning();

    void destroy();


    /**
     * 获取已配对的蓝牙列表
     *
     * @return
     */
    Set<BluetoothDevice> getBondedDevices();
}
