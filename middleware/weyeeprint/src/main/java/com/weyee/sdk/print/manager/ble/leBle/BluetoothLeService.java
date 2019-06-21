package com.weyee.sdk.print.manager.ble.leBle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.*;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.weyee.sdk.print.manager.ble.IBluetoothAble;
import com.weyee.sdk.print.manager.ble.IBluetoothListener;

import java.util.*;

/**
 * 低功耗蓝牙连接
 *
 * @author wuqi by 2019-06-16.
 */
public class BluetoothLeService extends Service implements IBluetoothAble {
    //Debug
    private static final String TAG = BluetoothLeService.class.getName();

    private final IBinder mBinder = new LocalBinder();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    /**
     * 记住上次连接的设备
     */
    private String mBluetoothDeviceAddress;

    private List<BluetoothDevice> mScanLeDeviceList = new ArrayList<>();
    private boolean isScanning;

    private IBluetoothListener bluetoothLeListener;


    @Override
    public boolean initialize() {
        //For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, ":Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to initialize BluetoothAdapter.");
            return false;
        }
        return true;
    }

    @Override
    public boolean isSupportBle() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean isEnableBluetooth() {
        return mBluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean enableBluetooth(boolean enable) {
        if (enable) {
            if (!mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.enable();
            }
            return true;
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                return mBluetoothAdapter.disable();
            }
            return false;
        }
    }

    @Override
    public void setIBluetoothListener(IBluetoothListener bluetoothLeListener) {
        this.bluetoothLeListener = bluetoothLeListener;
    }

    @Override
    public void startDiscovery() {
        startDiscovery(10000); // 10秒超时
    }

    @SuppressLint("MissingPermission")
    @Override
    public void startDiscovery(long scanPeriod) {
        if (isEnableBluetooth()) {
            if (isScanning()) return;
            //Stop scanning after a predefined scan period.
            new Handler().postDelayed(() -> {
                changeScanState(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
                } else {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
                broadcastUpdate(ACTION_SCAN_FINISHED);
            }, scanPeriod);
            mScanLeDeviceList.clear();
            changeScanState(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        } else {
            cancelDiscovery();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void cancelDiscovery() {
        changeScanState(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        broadcastUpdate(ACTION_SCAN_FINISHED);
        mScanLeDeviceList.clear();
    }

    @Override
    public boolean isScanning() {
        return isScanning;
    }

    /**
     * 改变蓝牙扫描的状态
     *
     * @param isScanning
     */
    private void changeScanState(boolean isScanning) {
        this.isScanning = isScanning;
        if (bluetoothLeListener != null) {
            bluetoothLeListener.onScanStateChange(isScanning);
        }
    }

    @Override
    public boolean connect(String address) {
        if (isScanning()) cancelDiscovery();
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        //Previously connected device.  Try to reconnect.
        if (mBluetoothGatt != null && address.equals(mBluetoothDeviceAddress)) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            return mBluetoothGatt.connect();
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        //We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        return true;
    }

    @Override
    public void write(byte[] bytes) {
    }

    @Override
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    @Override
    public void destroy() {
        cancelDiscovery();
        close();
    }

    @SuppressLint("MissingPermission")
    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return Collections.emptySet();
        }
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public Set<BluetoothDevice> getScanDevices() {
        Set<BluetoothDevice> deviceSet = new HashSet<>(mScanLeDeviceList);
        return Collections.unmodifiableSet(deviceSet);
    }


    /**
     * 获取当前连接的蓝牙设备
     *
     * @return
     */
    public BluetoothDevice getConnectDevice() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getDevice();
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> getConnectDevices() {
        if (mBluetoothManager == null) return null;
        return mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        destroy();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }


    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the BluetoothGattCallback#onCharacteristicRead.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized.");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}, specific service UUID
     * and characteristic UUID. The read result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt,
     * android.bluetooth.BluetoothGattCharacteristic, int)} callback.
     *
     * @param serviceUUID        remote device service uuid
     * @param characteristicUUID remote device characteristic uuid
     */
    public void readCharacteristic(String serviceUUID, String characteristicUUID) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service =
                    mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(characteristicUUID));
            mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public void readCharacteristic(String address, String serviceUUID, String characteristicUUID) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service =
                    mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(characteristicUUID));
            mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    /**
     * Write data to characteristic, and send to remote bluetooth le device.
     *
     * @param serviceUUID        remote device service uuid
     * @param characteristicUUID remote device characteristic uuid
     * @param value              Send to remote ble device data.
     */
    public void writeCharacteristic(String serviceUUID, String characteristicUUID, String value) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service =
                    mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(characteristicUUID));
            characteristic.setValue(value);
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    public boolean writeCharacteristic(String serviceUUID, String characteristicUUID, byte[] value) {
        if (mBluetoothGatt != null) {
            BluetoothGattService service =
                    mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            BluetoothGattCharacteristic characteristic =
                    service.getCharacteristic(UUID.fromString(characteristicUUID));
            characteristic.setValue(value);
            return mBluetoothGatt.writeCharacteristic(characteristic);
        }
        return false;
    }

    /**
     * Write value to characteristic, and send to remote bluetooth le device.
     *
     * @param characteristic remote device characteristic
     * @param value          New value for this characteristic
     * @return if write success return true
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, String value) {
        return writeCharacteristic(characteristic, value.getBytes());
    }

    /**
     * Writes a given characteristic and its values to the associated remote device.
     *
     * @param characteristic remote device characteristic
     * @param value          New value for this characteristic
     * @return if write success return true
     */
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] value) {
        if (mBluetoothGatt != null) {
            characteristic.setValue(value);
            return mBluetoothGatt.writeCharacteristic(characteristic);
        }
        return false;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(GattAttributes.DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION));
        descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE :
                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    public void setCharacteristicNotification(String serviceUUID, String characteristicUUID, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        BluetoothGattService service =
                mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        BluetoothGattCharacteristic characteristic =
                service.getCharacteristic(UUID.fromString(characteristicUUID));

        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                UUID.fromString(GattAttributes.DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION));
        descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE :
                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * Reads the value for a given descriptor from the associated remote device.
     *
     * <p>Once the read operation has been completed, the
     * {@link BluetoothGattCallback#onDescriptorRead} callback is
     * triggered, signaling the result of the operation.
     *
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * @param descriptor Descriptor value to read from the remote device
     * @return true, if the read operation was initiated successfully
     */
    public boolean readDescriptor(BluetoothGattDescriptor descriptor) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt is null");
            return false;
        }
        return mBluetoothGatt.readDescriptor(descriptor);
    }

    /**
     * Reads the value for a given descriptor from the associated remote device.
     *
     * @param serviceUUID        remote device service uuid
     * @param characteristicUUID remote device characteristic uuid
     * @param descriptorUUID     remote device descriptor uuid
     * @return true, if the read operation was initiated successfully
     */
    public boolean readDescriptor(String serviceUUID, String characteristicUUID,
                                  String descriptorUUID) {
        if (mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt is null");
            return false;
        }
//        try {
        BluetoothGattService service =
                mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        BluetoothGattCharacteristic characteristic =
                service.getCharacteristic(UUID.fromString(characteristicUUID));
        BluetoothGattDescriptor descriptor =
                characteristic.getDescriptor(UUID.fromString(descriptorUUID));
        return mBluetoothGatt.readDescriptor(descriptor);
//        } catch (Exception e) {
//            Log.e(TAG, "read descriptor exception", e);
//            return false;
//        }
    }

    /**
     * Read the RSSI for a connected remote device.
     *
     * <p>The {@link BluetoothGattCallback#onReadRemoteRssi} callback will be
     * invoked when the RSSI value has been read.
     *
     * <p>Requires {@link android.Manifest.permission#BLUETOOTH} permission.
     *
     * @return true, if the RSSI value has been requested successfully
     */
    public boolean readRemoteRssi() {
        if (mBluetoothGatt == null) return false;
        return mBluetoothGatt.readRemoteRssi();
    }

    /**
     * Request an MTU size used for a given connection.
     *
     * <p>When performing a write request operation (write without response),
     * the data sent is truncated to the MTU size. This function may be used
     * to request a larger MTU size to be able to send more data at once.
     *
     * <p>A {@link BluetoothGattCallback#onMtuChanged} callback will indicate
     * whether this operation was successful.
     *
     * <p>Requires {@link Manifest.permission#BLUETOOTH} permission.
     *
     * @param mtu mtu
     * @return true, if the new MTU value has been requested successfully
     */
    public boolean requestMtu(int mtu) {
        if (mBluetoothGatt == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android API level >= 21
            return mBluetoothGatt.requestMtu(mtu);
        } else {
            return false;
        }
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String address) {
        final Intent intent = new Intent(action);
        intent.putExtra("address", address);
        sendBroadcast(intent);
    }

    @SuppressLint("MissingPermission")
    private void broadcastUpdate(final String action, BluetoothDevice device) {
        final Intent intent = new Intent(action);
        intent.putExtra("name", device.getName());
        intent.putExtra("address", device.getAddress());
        sendBroadcast(intent);
    }


    /**
     * Device scan callback.
     * <p>
     * Use mScanCallback if Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP,
     * else use mLeScanCallback.
     */
    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (mScanLeDeviceList.contains(result.getDevice())) return;
                    mScanLeDeviceList.add(result.getDevice());
                    if (bluetoothLeListener != null) {
                        bluetoothLeListener.onLeScan(result.getDevice(), result.getRssi(), Objects.requireNonNull(result.getScanRecord()).getBytes());
                    }
                    broadcastUpdate(ACTION_BLUETOOTH_DEVICE, result.getDevice());
                }
            };
        } else {
            mLeScanCallback = (device, rssi, scanRecord) -> {
                if (device == null || mScanLeDeviceList.contains(device)) return;
                mScanLeDeviceList.add(device);
                if (bluetoothLeListener != null) {
                    bluetoothLeListener.onLeScan(device, rssi, scanRecord);
                }
                broadcastUpdate(ACTION_BLUETOOTH_DEVICE, device);
            };
        }
    }

    /**
     * Implements callback methods for GATT events that the app cares about.  For example,
     * connection change and services discovered.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (bluetoothLeListener != null) {
                bluetoothLeListener.onConnectionStateChange(gatt, status, newState);
            }
            String intentAction;
            String address = gatt.getDevice().getAddress();
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "onConnectionStateChange: DISCONNECTED: " + getConnectDevices().size());
                intentAction = ACTION_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction, address);
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                Log.i(TAG, "onConnectionStateChange: CONNECTING: " + getConnectDevices().size());
                intentAction = ACTION_CONNECTING;
                Log.i(TAG, "Connecting to GATT server.");
                broadcastUpdate(intentAction, address);
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "onConnectionStateChange: CONNECTED: " + getConnectDevices().size());
                intentAction = ACTION_CONNECTED;
                broadcastUpdate(intentAction, address);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                Log.i(TAG, "onConnectionStateChange: DISCONNECTING: " + getConnectDevices().size());
                intentAction = ACTION_DISCONNECTING;
                Log.i(TAG, "Disconnecting from GATT server.");
                broadcastUpdate(intentAction, address);
            }
        }

        // New services discovered
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (bluetoothLeListener != null && bluetoothLeListener instanceof BluetoothLeListener) {
                ((BluetoothLeListener) bluetoothLeListener).onServicesDiscovered(gatt, status);
            }
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        // Result of a characteristic read operation
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (bluetoothLeListener != null && bluetoothLeListener instanceof BluetoothLeListener) {
                ((BluetoothLeListener) bluetoothLeListener).onCharacteristicRead(gatt, characteristic, status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            String address = gatt.getDevice().getAddress();
            for (int i = 0; i < characteristic.getValue().length; i++) {
                Log.i(TAG, "address: " + address + ",Write: " + characteristic.getValue()[i]);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            if (bluetoothLeListener != null && bluetoothLeListener instanceof BluetoothLeListener) {
                ((BluetoothLeListener) bluetoothLeListener).onCharacteristicChanged(gatt, characteristic);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (bluetoothLeListener != null && bluetoothLeListener instanceof BluetoothLeListener) {
                ((BluetoothLeListener) bluetoothLeListener).onDescriptorRead(gatt, descriptor, status);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (bluetoothLeListener != null && bluetoothLeListener instanceof BluetoothLeListener) {
                ((BluetoothLeListener) bluetoothLeListener).onReadRemoteRssi(gatt, rssi, status);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (bluetoothLeListener != null && bluetoothLeListener instanceof BluetoothLeListener) {
                ((BluetoothLeListener) bluetoothLeListener).onMtuChanged(gatt, mtu, status);
            }
        }
    };
}
