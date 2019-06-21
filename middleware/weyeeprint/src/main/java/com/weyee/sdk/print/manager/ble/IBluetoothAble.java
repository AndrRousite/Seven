package com.weyee.sdk.print.manager.ble;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * 蓝牙连接
 * 实现方式有两种：经典蓝牙和低功耗蓝牙
 *
 * @author wuqi by 2019-06-16.
 */
public interface IBluetoothAble {

    //Action
    String ACTION_DISCONNECTED = "com.weyee.sdk.print.manager.ble.ACTION_DISCONNECTED";
    String ACTION_CONNECTING = "com.weyee.sdk.print.manager.ble.ACTION_CONNECTING";
    String ACTION_CONNECTED = "com.weyee.sdk.print.manager.ble.ACTION_CONNECTED";
    String ACTION_DISCONNECTING = "com.weyee.sdk.print.manager.ble.ACTION_DISCONNECTING";
    String ACTION_SERVICES_DISCOVERED = "com.weyee.sdk.print.manager.ble.ACTION_SERVICES_DISCOVERED";
    String ACTION_BLUETOOTH_DEVICE = "com.weyee.sdk.print.manager.ble.ACTION_BLUETOOTH_DEVICE";
    String ACTION_SCAN_FINISHED = "com.weyee.sdk.print.manager.ble.ACTION_SCAN_FINISHED";

    int SCAN = 1;
    int CONNECT = 2;
    int READ = 3;
    int WRITE = 4;

    int STATE_WRITE_COMPLETED = 101;
    int STATE_READ_COMPLETED = 102;

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

    /**
     * 连接
     */
    boolean connect(String address);

    void write(byte[] bytes);

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 关闭连接通道
     */
    void close();

    void destroy();


    /**
     * 获取已配对的蓝牙列表
     *
     * @return
     */
    Set<BluetoothDevice> getBondedDevices();

    /**
     * 获取扫描到的蓝牙列表（包括已配对的）
     *
     * @return
     */
    Set<BluetoothDevice> getScanDevices();
}
