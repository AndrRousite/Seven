package com.weyee.sdk.print.scan.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;

/**
 * @author wuqi by 2019-06-19.
 */
public interface IBluetoothListener {
    /**
     * Callback reporting an LE device scan state change
     * by the {@link IBluetoothAble#isScanning()} function.
     *
     * @param isScanning The bluetooth is scaning
     */
    void onScanStateChange(boolean isScanning);

    /**
     * callback by {@link BroadcastReceiver}
     *
     * @param newState {@link BluetoothDevice#BOND_NONE,BluetoothDevice#BOND_BONDING,BluetoothDevice#BOND_BONDED}
     */
    void onBondStateChange(BluetoothDevice device,int newState);

    /**
     * Callback reporting an LE device found during a device scan initiated
     * by the {@link BluetoothAdapter#startLeScan} function.
     *
     * @param device     Identifies the remote device
     * @param rssi       The RSSI value for the remote device as reported by the
     *                   Bluetooth hardware. 0 if no RSSI value is available.
     * @param scanRecord The content of the advertisement record offered by
     *                   the remote device.
     */
    default void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    }


    /**
     * Callback reporting an LE device found during a device scan initiated
     * by the {@link BluetoothAdapter#startDiscovery()} function.
     *
     * @param device Identifies the remote device
     */
    default void onClassicScan(BluetoothDevice device) {
    }


    /**
     * Callback indicating when GATT client has connected/disconnected to/from a remote GATT server.
     *
     * @param gatt     GATT client
     * @param status   Status of the connect or disconnect operation.
     *                 {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     * @param newState Returns the new connection state. Can be one of
     *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
     *                 {@link BluetoothProfile#STATE_CONNECTED}
     */
    default void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
    }

    /**
     * Callback indicating when 2.0 client has connected/disconnected to/from a remote Classic Bluetooth.
     *
     * @param device   Bluetooth client
     * @param status   Status of the connect or disconnect operation.
     *                 {@link BluetoothGatt#GATT_SUCCESS} if the operation succeeds.
     * @param newState Returns the new connection state. Can be one of
     *                 {@link BluetoothProfile#STATE_DISCONNECTED} or
     *                 {@link BluetoothProfile#STATE_CONNECTED}
     */
    default void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
    }
}
