package com.weyee.sdk.print.manager.ble.classicBle;

import android.bluetooth.BluetoothDevice;
import com.weyee.sdk.print.manager.ble.IBluetoothListener;

/**
 * @author wuqi by 2019-06-19.
 */
public interface BluetoothClassicListener extends IBluetoothListener {
    void read(BluetoothDevice device, byte[] buffer, int length);

    void read(BluetoothDevice device, String read);

    void write(BluetoothDevice device, byte[] buffer, int length);

    void write(BluetoothDevice device, String write);
}
