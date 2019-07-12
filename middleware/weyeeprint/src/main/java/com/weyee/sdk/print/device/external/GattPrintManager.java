package com.weyee.sdk.print.device.external;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.content.Context;
import android.os.Build;
import com.weyee.sdk.log.LogUtils;
import com.weyee.sdk.print.constant.ConnectStatus;
import com.weyee.sdk.print.utils.Utils;
import com.weyee.sdk.util.Tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Queue;

/**
 * le蓝牙ESC打印
 *
 * @author wuqi by 2019-06-14.
 */
public class GattPrintManager extends BasePrintManager {
    //private static final String UUID_SERVICE_DEVICE = "00001800-0000-1000-8000-00805f9b34fb"; // 进行通信的服务UUID
    private static final String UUID_SERVICE_DEVICE = "0000fff0-0000-1000-8000-00805f9b34fb"; // 进行通信的服务UUID
    //private static final String UUID_CHARACTERISTIC_DEVICE = "00002a00-0000-1000-8000-00805f9b34fb";  //进行通信的CharacteristicUUID
    private static final String UUID_CHARACTERISTIC_DEVICE = "0000fff2-0000-1000-8000-00805f9b34fb";  //进行通信的CharacteristicUUID

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothGatt mBluetoothGatt;

    @Override
    public void create() {
        super.create();
        BluetoothManager bluetoothManager = (BluetoothManager) Tools.getApp().getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) bluetoothAdapter = bluetoothManager.getAdapter();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void connect(String address, int port) {
        super.connect(address, port);
        connectThreadExecutor.execute(new ConnectRunnable(address));
    }

    @Override
    public void write(byte[] bytes) {
        super.write(bytes);
        writeThreadExecutor.execute(new WriteRunnable(bytes));
    }

    @Override
    public void disconnect() {
        super.disconnect();
        try {
            Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (mBluetoothGatt != null) {
                // 清楚
                refresh.invoke(mBluetoothGatt);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    @Override
    public boolean isConnect() {
        return mBluetoothGatt != null;
    }

    @Override
    public int splitLength() {
        return 1024;
    }

    /**
     * 连接线程
     */
    final class ConnectRunnable implements Runnable {
        String address;

        ConnectRunnable(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            if (bluetoothAdapter == null || address == null) {
                return;
            }

            //Previously connected device.  Try to reconnect.
            if (mBluetoothGatt != null && address.equals(mBluetoothGatt.getDevice().getAddress())) {
                mBluetoothGatt.connect();
                return;
            }
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                mHandler.obtainMessage(ConnectStatus.STATE_DISCONNECTED, -1, -1, null).sendToTarget();
                return;
            }
            //We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothGatt = device.connectGatt(Tools.getApp(), false, new QQBluetoothGattCallback(), BluetoothDevice.TRANSPORT_LE);
            } else {

                mBluetoothGatt = device.connectGatt(Tools.getApp(), false, new QQBluetoothGattCallback());
            }
        }
    }

    /**
     * 该类支持阻塞性的发送数据
     */
    final class WriteRunnable implements Runnable {
        byte[] bytes;

        WriteRunnable(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public void run() {
            // 先判断数据和是否连接
            if (bytes == null || !isConnect()) {
                mHandler.obtainMessage(ConnectStatus.STATE_DISCONNECTED, -1, -1, null).sendToTarget();
                return;
            }

            mHandler.obtainMessage(ConnectStatus.STATE_WRITE_BEGIN, -1, -1, null).sendToTarget();

            List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

            for (BluetoothGattService gattService : gattServices) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    final int charaProp = gattCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                        // 写入数据
                        //获取特定特征成功

                        // 每次发送20个字节
                        Queue<byte[]> queue = Utils.splitByte(bytes, splitLength());
                        while (!queue.isEmpty()) {
                            //写入你需要传递给外设的特征值（即传递给外设的信息）
                            gattCharacteristic.setValue(queue.poll());
                            //通过GATt实体类将，特征值写入到外设中。
                            mBluetoothGatt.writeCharacteristic(gattCharacteristic);

                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.obtainMessage(ConnectStatus.STATE_WRITE_SUCCESS, -1, -1, null).sendToTarget();
                        return;
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        // 发送通知
                        mBluetoothGatt.setCharacteristicNotification(gattCharacteristic, true);
                        continue;
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // 通过Gatt对象读取特定特征（Characteristic）的特征值
                        mBluetoothGatt.readCharacteristic(gattCharacteristic);
                    }
                }
            }

            //获取特定服务失败
            mHandler.obtainMessage(ConnectStatus.STATE_WRITE_FAILURE, -1, -1, null).sendToTarget();
        }
    }

    final class QQBluetoothGattCallback extends BluetoothGattCallback {
        //连接状态回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // status 用于返回操作是否成功,会返回异常码。
            // newState 返回连接状态，如BluetoothProfile#STATE_DISCONNECTED、BluetoothProfile#STATE_CONNECTED

            //操作成功的情况下
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //判断是否连接码
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // 调用服务发现
                    mBluetoothGatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //判断是否断开连接码
                    disconnect();
                }
            } else {
                //异常码
                disconnect();
            }
        }

        //服务发现回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mHandler.obtainMessage(ConnectStatus.STATE_CONNECTED, -1, -1, null).sendToTarget();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //获取读取到的特征值
                byte[] value = characteristic.getValue();
                LogUtils.d(String.format("%s->%s", "onCharacteristicRead", new String(value)));
            }
        }

        //特征写入回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //获取写入到外设的特征值
                byte[] value = characteristic.getValue();
                LogUtils.d(String.format("%s->%s", "onCharacteristicWrite", new String(value)));
            }
        }

        //外设特征值改变回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //获取外设修改的特征值
            byte[] value = characteristic.getValue();
            //对特征值进行解析
            LogUtils.d(String.format("%s->%s", "onCharacteristicChanged", new String(value)));
        }

        //描述写入回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    }
}
