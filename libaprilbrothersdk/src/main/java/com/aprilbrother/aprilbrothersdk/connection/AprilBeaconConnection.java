package com.aprilbrother.aprilbrothersdk.connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.Utils;
import com.aprilbrother.aprilbrothersdk.globalvariables.GlobalVariables;
import com.aprilbrother.aprilbrothersdk.internal.ABAcceleration;
import com.aprilbrother.aprilbrothersdk.receiver.ABeaconUARTStatusChangeReceiver.MyABeaconCallBack;
import com.aprilbrother.aprilbrothersdk.services.ABeaconUartService;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;
import com.aprilbrother.aprilbrothersdk.utils.UUID2bytesUtils;

/**
 * AprilBeacon连接 与AprilBeacon建立连接 并可对特性值进行更改
 */
public class AprilBeaconConnection {
    protected static final String TAG = "AprilBeaconConnection";
    private Context context;
    private Beacon beacon;
    private String address;
    private BluetoothGatt mBluetoothGatt;
    private MyWriteCallback mWriteCallback;

    // private static final String NAME = "AprilBeacon";
    // private static final String NAME1 = "ABSensor";
    // private static final String NAME2 = "ABLight";
    // private static final String NAME3 = "BEACONSTREAM";
    private static final String NAME4 = "abeacon";
    private static final String NAME5 = "asensor";
    // private static final String NAME5 = "Aikaka";//普通Aikaka
    private static final String NAME6 = "aikaka";// 省电王aikaka
    private static final String NAME7 = "abtemp";//

    private String str[] = {"AprilBeacon", "ABSensor", "ABLight",
            "BEACONSTREAM", "abeacon", "asensor", "Aikaka", "PROX", "abtemp"};
    // private String str[] = { "ABSensor" };
    private ArrayList<String> names;

    private String password;

    public final static String ACTION_GATT_CONNECTED = "aprilbrothersdk.com.nordicsemi.nrfUART.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "aprilbrothersdk.com.nordicsemi.nrfUART.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "aprilbrothersdk.com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "aprilbrothersdk.com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "aprilbrothersdk.com.nordicsemi.nrfUART.EXTRA_DATA";
    public final static String EXTRA_DATA_L = "aprilbrothersdk.com.nordicsemi.nrfUART.EXTRA_DATA_L";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART = "aprilbrothersdk.com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";

    private ABAcceleration abAcceleration;

    private boolean isWriteReset;

    public static abstract interface WriteCallback {
        /**
         * 连接过程发生错误
         */
        public abstract void onErrorOfConnection();

        /**
         * 校验密码过程中发生错误
         */
        public abstract void onErrorOfPassword();

        /**
         * 查找服务过程中发生错误
         */
        public abstract void onErrorOfDiscoveredServices();

        /**
         * 修改major成功时的回调
         *
         * @param oldMajor 修改前的major
         * @param newMajor 修改后的major
         */
        public abstract void onWriteMajorSuccess(int oldMajor, int newMajor);

        /**
         * 修改major成功时的回调
         */
        public abstract void onWriteMajor2Success();

        /**
         * 修改measuredPower成功时的回调
         *
         * @param newMeasuredPower
         */
        public abstract void onWriteMeasuredPowerSuccess(int newMeasuredPower);

        /**
         * 校验密码成功
         */
        public abstract void onWritePasswordSuccess();

        /**
         * 重启成功
         */
        public abstract void onResetSuccess();

        /**
         * 修改minor成功时的回调
         *
         * @param oldMionr 修改前的minor
         * @param newMinor 修改后的minor
         */
        public abstract void onWriteMinorSuccess(int oldMionr, int newMinor);

        /**
         * 修改minor成功时的回调
         */
        public abstract void onWriteMinor2Success();

        /**
         * 修改UUID成功时的回调
         */
        public abstract void onWriteUUIDSuccess();

        /**
         * 修改UUID2成功时的回调
         */
        public abstract void onWriteUUID2Success();


        /**
         * 修改广播频率成功时的回调
         */
        public abstract void onWriteAdvertisingIntervalSuccess();

        /**
         * 修改发射功率成功时的回调
         */
        public abstract void onWriteTxPowerSuccess();

        /**
         * 修改校验密码成功时的回调
         *
         * @param oldPassword 修改前的校验密码
         * @param newPassword 修改后的检验密码
         */
        public abstract void onWriteNewPasswordSuccess(String oldPassword,
                                                       String newPassword);

        /**
         * 修改检验密码过程中发生错误时的回调
         *
         * @param oldPassword 修改前的校验密码
         * @param newPassword 要修改的检验密码值
         */
        public abstract void onErrorOfWriteNewPassword(String oldPassword,
                                                       String newPassword);

        /**
         * 检验密码错误的回调
         *
         * @param oldPassword 输入的校验密码
         */
        public abstract void onPasswordWrong(String oldPassword);

        /**
         * 连接的不是固件2.0及以上AprilBeacon的回调
         */
        public abstract void onBeaconError();

        /**
         * 开启三轴加速度计通知的回调
         *
         * @param abAcceleration 加速度类 有x,y,z轴加速度属性
         */
        public abstract void notifyABAcceleration(ABAcceleration abAcceleration);

        /**
         * 开启光线传感器通知的回调
         *
         * @param light
         */
        public abstract void notifyABLight(double light);

        /**
         * 加速计状态改变时回调
         *
         * @param state true为打开 false为关闭
         */
        public abstract void accelerometerStateChange(boolean state);

        /**
         * 光线传感器状态改变时回调
         *
         * @param state true为打开 false为关闭
         */
        public abstract void lightStateChange(boolean state);

        /**
         * 传感器执行打开或关闭错误时的回调
         */
        public abstract void turnOnOffError();

        /**
         * 三轴加速度计开始通知的回调
         */
        public abstract void enableAccelerometerNotification();

        /**
         * 三轴加速度计开始通知错误的回调
         */
        public abstract void enableAccelerometerNotificationError();

        /**
         * 光线传感器开始通知的回调
         */
        public abstract void enableLightNotification();

        /**
         * 光线传感器开始通知错误的回调
         */
        public abstract void enableLightNotificationError();

        /**
         * 连接到gatt的回调
         */
        public abstract void connected();

        /**
         * 写入过程中出现错误
         */
        public abstract void onErrorOfWrite();

        /**
         * 写入成功
         */
        public abstract void onWriteSuccess();

    }

    public static class MyWriteCallback implements WriteCallback {

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onErrorOfConnection()
         */
        public void onErrorOfConnection() {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onErrorOfPassword()
         */
        public void onErrorOfPassword() {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onErrorOfDiscoveredServices()
         */
        public void onErrorOfDiscoveredServices() {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onWriteMajorSuccess(int, int)
         */
        public void onWriteMajorSuccess(int oldMajor, int newMajor) {
        }

        @Override
        public void onWriteMajor2Success() {

        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onWritePasswordSuccess()
         */
        public void onWritePasswordSuccess() {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onWriteMinorSuccess(int, int)
         */
        public void onWriteMinorSuccess(int oldMionr, int newMinor) {
        }

        @Override
        public void onWriteMinor2Success() {

        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onWriteUUIDSuccess()
         */
        public void onWriteUUIDSuccess() {
        }

        @Override
        public void onWriteUUID2Success() {

        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onWriteAdvertisingIntervalSuccess()
         */
        public void onWriteAdvertisingIntervalSuccess() {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onWriteTxPowerSuccess()
         */
        public void onWriteTxPowerSuccess() {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onWriteNewPasswordSuccess(java.lang.String,
         * java.lang.String)
         */
        public void onWriteNewPasswordSuccess(String oldPassword,
                                              String newPassword) {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onErrorOfWriteNewPassword(java.lang.String,
         * java.lang.String)
         */
        public void onErrorOfWriteNewPassword(String password,
                                              String newPassword) {
        }

        ;

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onPasswordWrong(java.lang.String)
         */
        public void onPasswordWrong(String password) {
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection
         * .WriteCallback#onBeaconError()
         */
        public void onBeaconError() {
        }

        ;

        public void notifyABAcceleration(ABAcceleration abAcceleration) {
        }

        public void notifyABLight(double light) {
        }

        public void accelerometerStateChange(boolean b) {

        }

        public void turnOnOffError() {

        }

        public void lightStateChange(boolean state) {

        }

        public void enableAccelerometerNotification() {

        }

        public void enableAccelerometerNotificationError() {

        }

        public void enableLightNotification() {

        }

        public void enableLightNotificationError() {

        }

        public void connected() {

        }

        public void onWriteMeasuredPowerSuccess(int newMeasuredPower) {

        }

        public void onResetSuccess() {

        }

        public void onErrorOfWrite() {

        }

        public void onErrorOfWrite(int status) {

        }

        public void onWriteSuccess() {

        }
    }

    private ArrayList<BluetoothGattCharacteristic> characteristics = new ArrayList<BluetoothGattCharacteristic>();

    public AprilBeaconConnection(Context context, Beacon beacon) {
        names = new ArrayList<String>();
        names.addAll(Arrays.asList(str));
        setDefaultWrite();
        begainConnection(context, beacon);
    }

    private void setDefaultWrite() {
        GlobalVariables.isWriteMajor = false;
        GlobalVariables.isWriteMinor = false;
        GlobalVariables.isWriteMeasuredPower = false;
        GlobalVariables.isWriteUUID = false;
        GlobalVariables.isWriteAdvertisingInterval = false;
        GlobalVariables.isWriteTxPower = false;
        GlobalVariables.isWritePassword = false;

        GlobalVariables.isWriteMajor2 = false;
        GlobalVariables.isWriteMinor2 = false;
        GlobalVariables.isWriteUUID2 = false;

        isWriteReset = false;
    }

    private void begainConnection(Context context, Beacon beacon) {
        this.context = context;
        this.beacon = beacon;
        address = beacon.getMacAddress();
        context.registerReceiver(
                UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
        abAcceleration = new ABAcceleration();
    }

    public AprilBeaconConnection(Context context, String address) {
        names = new ArrayList<String>();
        names.addAll(Arrays.asList(str));
        setDefaultWrite();
        begainConnection(context, address);
    }

    private void begainConnection(Context context, String address) {
        this.context = context;
        this.address = address;
        context.registerReceiver(
                UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
        abAcceleration = new ABAcceleration();
    }

    /**
     * 连接gatt并对特征值进行写操作
     *
     * @param mWriteCallback 连接gatt并进行写操作时的回调
     * @param password       密码
     */
    public void connectGattToWrite(MyWriteCallback mWriteCallback,
                                   String password) {
        begainWrite(mWriteCallback, password);
    }

    private void begainWrite(MyWriteCallback mWriteCallback, String password) {
        this.mWriteCallback = mWriteCallback;
        boolean isNameContains = false;
        if (beacon != null) {
            for (int i = 0; i < names.size(); i++) {
                if (beacon.getName().toUpperCase()
                        .contains(names.get(i).toUpperCase())) {
                    isNameContains = true;
                    break;
                }
            }
        }
        if (beacon != null && !isNameContains) {
            mWriteCallback.onBeaconError();
        } else if (beacon != null && beacon.getName().startsWith("a")) {
            this.password = password;
            connectABeacon(false, password);
        } else {
            if (password != null && password.length() != 12) {
                mWriteCallback.onPasswordWrong(password);
            } else {
                if (password != null) {
                    this.password = "AT+AUTH" + password;
                }
                BluetoothDevice device;
                if (beacon != null) {
                    device = deviceFromBeacon(beacon);
                } else {
                    device = deviceFromMac(address);
                }
                mBluetoothGatt = device.connectGatt(context, false,
                        mGattCallback);
            }
        }
    }

    private void connectABeacon(boolean shouldReadInfo, final String password2) {
        if (password.length() != 6) {
            mWriteCallback.onPasswordWrong(password);
        } else {
            BeaconConnection.connABeacon(context, address, shouldReadInfo,
                    new MyABeaconCallBack() {
                        @Override
                        public void onNotificationOpen() {
                            byte[] a = Utils.password2byte(password2);
                            BeaconConnection.writeValue(
                                    AprilBeaconUUID.ABEACON_SERVICE_UUID,
                                    AprilBeaconUUID.ABEACON_READ_UUID, a,
                                    ABeaconUartService.class.getSimpleName());
                            super.onNotificationOpen();
                        }

                        @Override
                        public void onWriteFinished() {
                            mWriteCallback.onWriteSuccess();
                        }

                        @Override
                        public void onConnError(String errorMessage) {
                            mWriteCallback.onErrorOfConnection();
                        }

                        @Override
                        public void onPasswordError() {
                            mWriteCallback.onPasswordWrong(password);
                        }

                        @Override
                        public void onWriteUUIDSuccess() {
                            mWriteCallback.onWriteUUIDSuccess();
                        }

                        @Override
                        public void onWriteUUID2Success() {
                            mWriteCallback.onWriteUUID2Success();
                        }

                        @Override
                        public void onWriteMajor2Success() {
                            mWriteCallback.onWriteMajor2Success();
                        }

                        @Override
                        public void onWriteMinor2Success() {
                            mWriteCallback.onWriteMinor2Success();
                        }
                        @Override
                        public void onWriteMajorSuccess() {
                            mWriteCallback.onWriteMajorSuccess(
                                    beacon.getMajor(), newMajor);
                        }

                        @Override
                        public void onWriteMinorSuccess() {
                            mWriteCallback.onWriteMinorSuccess(
                                    beacon.getMinor(), newMinor);
                        }
                        @Override
                        public void onWriteAdvSuccess() {
                            mWriteCallback.onWriteAdvertisingIntervalSuccess();
                        }

                        @Override
                        public void onWriteMeasuredPowerSuccess() {

                            mWriteCallback
                                    .onWriteMeasuredPowerSuccess(newMeasuredPower - 256);
                        }

                        @Override
                        public void onWriteTxPowerSuccess() {
                            mWriteCallback.onWriteTxPowerSuccess();
                        }

                        @Override
                        public void onWritePassCodeSuccess() {
                            mWriteCallback.onWriteNewPasswordSuccess(password,
                                    newPassword);
                        }
                    });
        }

    }

    public void writeSecretKey(String secretKey) {
        this.preparWriteSecretKey(secretKey);
    }

    private void preparWriteSecretKey(String secretKey) {
        if (secretKey.length() == 32) {
            GlobalVariables.isWriteSecretKey = true;
            GlobalVariables.beaconWriteInfo.setSecretKey(secretKey);
        } else {
            String a = "密匙格式不正确";
            AprilL.e(a);
        }

    }

    private int newMajor;

    /**
     * 设置写入major值
     *
     * @param major 要写入的major值 值在0-65535之间
     */
    public void writeMajor(int major) {
        preparWriteMajor(major);
    }

    private void preparWriteMajor(int major) {
        if (major >= 0 && major <= 65535) {
            newMajor = major;
            GlobalVariables.isWriteMajor = true;
            GlobalVariables.beaconWriteInfo.setWriteMajor(newMajor);
        } else {
            String a = "major 需在0-65535之间";
            AprilL.e(a);
        }
    }

    /**
     * 设置写入major2值
     *
     * @param major2 要写入的major2值 值在0-65535之间
     */
    public void writeMajor2(int major2) {
        preparWriteMajor2(major2);
    }

    private void preparWriteMajor2(int major2) {
        if (major2 >= 0 && major2 <= 65535) {
            GlobalVariables.isWriteMajor2 = true;
            GlobalVariables.beaconWriteInfo.setWriteMajor2(major2);
        } else {
            String a = "major 需在0-65535之间";
            AprilL.e(a);
        }
    }

    private int newMinor;

    /**
     * 设置写入minor值
     *
     * @param minor 要写入的minor值 值在0-65535之间
     */
    public void writeMinor(int minor) {
        preparWriteMinor(minor);
    }

    private void preparWriteMinor(int minor) {
        if (minor >= 0 && minor <= 65535) {
            newMinor = minor;
            GlobalVariables.isWriteMinor = true;
            GlobalVariables.beaconWriteInfo.setWriteMinor(newMinor);
        } else {
            String a = "minor 需在0-65535之间";
            AprilL.e(a);
        }
    }

    /**
     * 设置写入minor2值
     *
     * @param minor2 要写入的minor值 值在0-65535之间
     */
    public void writeMinor2(int minor2) {
        preparWriteMinor2(minor2);
    }

    private void preparWriteMinor2(int minor2) {
        if (minor2 >= 0 && minor2 <= 65535) {
            GlobalVariables.isWriteMinor2 = true;
            GlobalVariables.beaconWriteInfo.setWriteMinor2(minor2);
        } else {
            String a = "minor 需在0-65535之间";
            AprilL.e(a);
        }
    }

    private int newMeasuredPower;

    /**
     * 设置写入measuredPower值
     *
     * @param measuredPower 要写入的measuredPower值 值在-256-0之间
     */
    public void writeMeasuredPower(int measuredPower) {
        preparWriteMeasuredPower(measuredPower);
    }

    private void preparWriteMeasuredPower(int measuredPower) {
        if (measuredPower < 0 && measuredPower > -256) {
            newMeasuredPower = measuredPower + 256;
            GlobalVariables.isWriteMeasuredPower = true;
            GlobalVariables.beaconWriteInfo
                    .setWriteMeasuredPower(newMeasuredPower);
        } else {
            String a = "measuredPower 需在-256-0之间";
            AprilL.e(a);
        }
    }

    private String newUUID;

    /**
     * 设置写入uuid值
     *
     * @param uuid 要写入的uuid值
     */
    public void writeUUID(String uuid) {
        preparWriteUUID(uuid);
    }

    private void preparWriteUUID(String uuid) {
        String reg = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        boolean matches = uuid.matches(reg);
        if (matches) {
            newUUID = uuid;
            GlobalVariables.isWriteUUID = true;
            GlobalVariables.beaconWriteInfo.setWriteUuid(newUUID);
        } else {
            String a = "uuid格式不正确";
            AprilL.e(a);
        }
    }

    /**
     * 设置写入uuid2值
     *
     * @param uuid2 要写入的uuid2值
     */
    public void writeUUID2(String uuid2) {
        preparWriteUUID2(uuid2);
    }

    private void preparWriteUUID2(String uuid2) {
        String reg = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        boolean matches = uuid2.matches(reg);
        if (matches) {
            GlobalVariables.isWriteUUID2 = true;
            GlobalVariables.beaconWriteInfo.setWriteUuid2(uuid2);
        } else {
            String a = "uuid格式不正确";
            AprilL.e(a);
        }
    }

    private int newAdvertisingInterval;

    /**
     * 设置写入广播频率
     *
     * @param advertisingInterval 要写入的广播频率 值在1-100之间 单位为100ms
     */
    public void writeAdvertisingInterval(int advertisingInterval) {
        preparWriteAdvertisingInterval(advertisingInterval);
    }

    private void preparWriteAdvertisingInterval(int advertisingInterval) {
        if (advertisingInterval > 0 && advertisingInterval < 101) {
            newAdvertisingInterval = advertisingInterval;
            GlobalVariables.isWriteAdvertisingInterval = true;
            GlobalVariables.beaconWriteInfo.setWriteAdv(newAdvertisingInterval);
        } else {
            String a = "advertisingInterval需在1-100之间";
            AprilL.e(a);
        }
    }

    private int newTxPower;

    /**
     * 设置写入发射功率
     *
     * @param txPower 要写入的发射功率 值为0，1，2，3 N04最大到8
     */
    public void writeTxPower(int txPower) {
        preparWriteTxPower(txPower);
    }

    private void preparWriteTxPower(int txPower) {
        if (txPower >= 0 && txPower <= 8) {
            newTxPower = txPower;
            GlobalVariables.isWriteTxPower = true;
            GlobalVariables.beaconWriteInfo.setWriteTxPower(newTxPower);
        } else {
            String a = "txPower需在0-8之间";
            AprilL.e(a);
        }
    }

    private String newPassword;

    /**
     * 设置写入新密码
     *
     * @param password 新密码为12位数字及字母组合
     */
    public void writePassword(String password) {
        preparWritePassword(password);
    }

    private void preparWritePassword(String password) {
        if (password.trim().length() == 12 || password.trim().length() == 6) {
            newPassword = password;
            GlobalVariables.isWritePassword = true;
            GlobalVariables.beaconWriteInfo.setWritePassword(newPassword);
        } else {
            String a = "password为12位或6位数字及字母组合";
            AprilL.e(a);
        }
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            myOnConnectionStateChange(status, newState);
        }

        private void myOnConnectionStateChange(int status, int newState) {
            // 如果连接成功 扫描服务修改特征值
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 开始扫描服务
                mBluetoothGatt.discoverServices();
            } else if (!isWriteReset) {
                mWriteCallback.onErrorOfConnection();
            }
        }

        ;

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            myOnServicesDiscovered(gatt, status);
        }

        private void myOnServicesDiscovered(BluetoothGatt gatt, int status) {
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            if (status == 0) {
                if (password != null) {
                    for (BluetoothGattService service : gatt.getServices()) {
                        // 写入密码 然后进行修改
                        if (service.getUuid().equals(
                                AprilBeaconUUID.BEACON_SERVICE_UUID)) {
                            writecharacterpassword(service, gatt);
                        }
                    }
                }
            } else {
                // 查找服务失败
                mWriteCallback.onErrorOfDiscoveredServices();
                gatt.disconnect();
                gatt.close();
                gatt = null;
            }
        }

        ;

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            myOnCharacteristicChanged(characteristic);
            super.onCharacteristicChanged(gatt, characteristic);
        }

        private void myOnCharacteristicChanged(
                BluetoothGattCharacteristic characteristic) {
            byte[] value = characteristic.getValue();
            try {
                Thread.sleep(5);
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, value);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            if (characteristic.getUuid()
                    .equals(AprilBeaconUUID.BEACON_PASSWORD) && status == 133) {
                mWriteCallback.onResetSuccess();
            } else {
                myOnCharacteristicWrite(gatt, characteristic, status);
            }

        }

        private void myOnCharacteristicWrite(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic, int status) {
            if (password != null) {
                characteristics.add(characteristic);
                if (status == 0
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_MAJOR_UUID)) {
                    mWriteCallback.onWriteMajorSuccess(beacon.getMajor(),
                            newMajor);
                }
                if (status == 0
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_MINOR_UUID)) {
                    mWriteCallback.onWriteMinorSuccess(beacon.getMinor(),
                            newMinor);
                }
                if (status == 0
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_PROXIMITY_UUID)) {
                    mWriteCallback.onWriteUUIDSuccess();
                }
                if (status == 0
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_ADVINTERVAL_UUID)) {
                    mWriteCallback.onWriteAdvertisingIntervalSuccess();
                }
                if (status == 0
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_TXPOWER_UUID)) {
                    mWriteCallback.onWriteTxPowerSuccess();
                }
                if (status == 0
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_MEASURED_POWER_UUID)) {
                    mWriteCallback
                            .onWriteMeasuredPowerSuccess(newMeasuredPower - 256);
                }

                if (status == 0
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_PASSCODE_UUID)) {
                    mWriteCallback.onWriteNewPasswordSuccess(
                            password.substring(7), newPassword);
                }
                if (status == 0) {
                    if (GlobalVariables.isWriteMajor) {
                        writeCharactersMajor(gatt,
                                AprilBeaconUUID.BEACON_MAJOR_UUID);
                        GlobalVariables.isWriteMajor = false;
                    } else if (GlobalVariables.isWriteMinor) {
                        writeCharactersMinor(gatt,
                                AprilBeaconUUID.BEACON_MINOR_UUID);
                        GlobalVariables.isWriteMinor = false;
                    } else if (GlobalVariables.isWriteUUID) {
                        writeCharactersUUID(gatt,
                                AprilBeaconUUID.BEACON_PROXIMITY_UUID);
                        GlobalVariables.isWriteUUID = false;
                    } else if (GlobalVariables.isWriteAdvertisingInterval) {
                        writeCharactersAdvertisingInterval(gatt,
                                AprilBeaconUUID.BEACON_ADVINTERVAL_UUID);
                        GlobalVariables.isWriteAdvertisingInterval = false;
                    } else if (GlobalVariables.isWriteTxPower) {
                        writeCharactersTxPower(gatt,
                                AprilBeaconUUID.BEACON_TXPOWER_UUID);
                        GlobalVariables.isWriteTxPower = false;
                    } else if (GlobalVariables.isWriteMeasuredPower) {
                        writeCharactersMeasuredPower(gatt,
                                AprilBeaconUUID.BEACON_MEASURED_POWER_UUID);
                        GlobalVariables.isWriteMeasuredPower = false;
                    } else if (GlobalVariables.isWritePassword) {
                        GlobalVariables.isWritePassword = false;
                        writecharacterpassword(gatt);
                    } else if (!GlobalVariables.isWriteMajor
                            && !GlobalVariables.isWriteMinor
                            && !GlobalVariables.isWriteUUID
                            && !GlobalVariables.isWriteAdvertisingInterval
                            && !GlobalVariables.isWriteTxPower
                            && !GlobalVariables.isWriteMeasuredPower
                            && !GlobalVariables.isWritePassword) {
                        writecharacterReset(gatt);
                    }

                } else if (status == 3
                        && characteristic.getUuid().equals(
                        AprilBeaconUUID.BEACON_PASSWORD)) {
                    String inputpassword = password.substring(7);
                    mWriteCallback.onPasswordWrong(inputpassword);
                    gatt.disconnect();
                    gatt.close();
                    gatt = null;
                } else {
                    mWriteCallback.onErrorOfWrite(status);
                    gatt.disconnect();
                    gatt.close();
                    gatt = null;
                }
            }
        }

        ;
    };

    private BluetoothDevice deviceFromBeacon(Beacon beacon) {
        BluetoothManager bluetoothManager = (BluetoothManager) this.context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.getRemoteDevice(beacon.getMacAddress());
    }

    private BluetoothDevice deviceFromMac(String address) {
        BluetoothManager bluetoothManager = (BluetoothManager) this.context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter.getRemoteDevice(address);
    }

    protected void writecharacterpassword(BluetoothGatt gatt) {
        myWritecharacterpassword(gatt);
    }

    private void myWritecharacterpassword(BluetoothGatt gatt) {
        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(AprilBeaconUUID.BEACON_PASSCODE_UUID);
                byte[] value = newPassword.getBytes();
                boolean setValue1 = characteristic.setValue(value);
                if (setValue1 && !characteristics.contains(characteristic)) {
                    boolean b = gatt.writeCharacteristic(characteristic);
                    if (!b) {
                        mWriteCallback.onErrorOfWriteNewPassword(password,
                                newPassword);
                    }
                }
            }
        }
    }

    protected void writecharacterReset(BluetoothGatt gatt) {
        myWritecharacterReset(gatt);
    }

    private void myWritecharacterReset(BluetoothGatt gatt) {
        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(AprilBeaconUUID.BEACON_PASSWORD);
                byte[] value = "AT+RESET".getBytes();
                boolean setValue1 = characteristic.setValue(value);
                if (setValue1) {
                    boolean b = gatt.writeCharacteristic(characteristic);
                    isWriteReset = true;
                }
            }
        }
    }

    /**
     * 写入密码
     *
     * @param service
     * BluetoothGattService
     * @param gatt
     */
    private boolean writePasswordSuccess = true;

    private void writecharacterpassword(BluetoothGattService service,
                                        BluetoothGatt gatt) {
        if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {
            BluetoothGattCharacteristic characteristic = service
                    .getCharacteristic(AprilBeaconUUID.BEACON_PASSWORD);
            if (characteristic != null) {
                int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {

                    byte[] value = password.getBytes();
                    characteristic
                            .setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

                    boolean setValue = characteristic.setValue(value);
                    if (setValue) {
                        boolean b = gatt.writeCharacteristic(characteristic);
                        if (b && writePasswordSuccess) {
                            writePasswordSuccess = false;
                            mWriteCallback.onWritePasswordSuccess();
                        } else {
                            mWriteCallback.onErrorOfPassword();
                        }
                    }
                }
            }
        }
    }

    ;

    /**
     * 写入major值
     *
     * @param gatt
     * BluetoothGatt
     * @param uuid
     * major对应的服务
     */
    private boolean writeMajorSuccess = true;

    private void writeCharactersMajor(BluetoothGatt gatt, UUID uuid) {

        byte[] majorbyte = {(byte) ((byte) (newMajor >> 8) & 0xff),
                (byte) ((byte) newMajor & 0xff)};
        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(uuid);
                boolean setValue1 = characteristic.setValue(majorbyte);
                if (setValue1 && !characteristics.contains(characteristic)) {
                    gatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    /**
     * 写入minor值
     *
     * @param gatt
     * BluetoothGatt
     * @param uuid
     * Minor对应的服务uuid
     */
    private boolean writeMinorSuccess = true;

    private void writeCharactersMinor(BluetoothGatt gatt, UUID uuid) {

        // 将Integer值转换为字节数组
        byte[] minorbyte = {(byte) ((byte) (newMinor >> 8) & 0xff),
                (byte) ((byte) newMinor & 0xff)};

        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {
                // 获取对于uuid的特性值
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(uuid);
                // 将value值设置进特性值中
                boolean setValue1 = characteristic.setValue(minorbyte);
                if (setValue1 && !characteristics.contains(characteristic)) {
                    // 开始远程写入
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    /**
     * 写入uuid值
     *
     * @param gatt
     * BluetoothGatt
     * @param uuid
     * uuid对应的服务
     */
    private boolean writeUUIDSuccess = true;

    private void writeCharactersUUID(BluetoothGatt gatt, UUID uuid) {
        byte[] value = UUID2bytesUtils.uuid2Bytes(newUUID);
        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(uuid);
                boolean setValue1 = characteristic.setValue(value);
                if (setValue1 && !characteristics.contains(characteristic)) {
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    /**
     * 写入AdvertisingInterval值
     *
     * @param gatt
     * BluetoothGatt
     * @param uuid
     * AdvertisingInterval对应的服务uuid
     */
    private boolean writeAdvertisingIntervalSuccess = true;

    private void writeCharactersAdvertisingInterval(BluetoothGatt gatt,
                                                    UUID uuid) {

        // 将Integer值转换为字节数组
        byte[] advertisingInterval = {(byte) (newAdvertisingInterval & 0xff)};

        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {

                // 获取对于uuid的特性值
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(uuid);

                // 将value值设置进特性值中
                boolean setValue1 = characteristic
                        .setValue(advertisingInterval);
                if (setValue1 && !characteristics.contains(characteristic)) {
                    // 开始远程写入
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    /**
     * 写入txpower值
     *
     * @param gatt
     * BluetoothGatt
     * @param uuid
     * txpower对应的服务uuid
     */
    private boolean writeTxPowerSuccess = true;

    private void writeCharactersTxPower(BluetoothGatt gatt, UUID uuid) {

        // 将Integer值转换为字节数组
        byte[] txPowerbyte = {(byte) (newTxPower & 0xff)};

        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {

                // 获取对于uuid的特性值
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(uuid);
                // 将value值设置进特性值中
                boolean setValue1 = characteristic.setValue(txPowerbyte);
                if (setValue1 && !characteristics.contains(characteristic)) {
                    // 开始远程写入
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    /**
     * 写入measuredPower
     *
     * @param gatt
     * @param uuid
     */
    private void writeCharactersMeasuredPower(BluetoothGatt gatt, UUID uuid) {

        // 将Integer值转换为字节数组
        byte[] measuredPower = {(byte) (newMeasuredPower & 0xff)};

        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().equals(AprilBeaconUUID.BEACON_SERVICE_UUID)) {

                // 获取对于uuid的特性值
                BluetoothGattCharacteristic characteristic = service
                        .getCharacteristic(uuid);
                // 将value值设置进特性值中
                boolean setValue1 = characteristic.setValue(measuredPower);
                if (setValue1 && !characteristics.contains(characteristic)) {
                    // 开始远程写入
                    mBluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }

    /**
     * 判断是否是连接状态
     *
     * @return true为已连接 false为未连接
     */
    public boolean isConnected() {
        return myIsConnected();
    }

    private boolean myIsConnected() {
        BluetoothManager bluetoothManager = (BluetoothManager) this.context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        int connectionState;
        if (beacon != null) {
            connectionState = bluetoothManager.getConnectionState(
                    this.deviceFromBeacon(beacon), 7);
        } else {
            connectionState = bluetoothManager.getConnectionState(
                    this.deviceFromMac(address), 7);
        }

        return (connectionState == 2);
    }

    /**
     * 断开连接
     */
    public void close() {
        myClose();
    }

    private void myClose() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
        context.unregisterReceiver(
                UARTStatusChangeReceiver);
        BeaconConnection.close();
    }

    // TODO 添加ABSensor传感器操作
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // *********************//
            // if (action.equals(ACTION_GATT_CONNECTED)) {
            //
            // }

            // *********************//
            // if (action.equals(ACTION_GATT_DISCONNECTED)) {
            //
            // }

            // *********************//
            if (action.equals(ACTION_GATT_SERVICES_DISCOVERED)) {
                mWriteCallback.connected();
            }
            // *********************//
            if (action.equals(ACTION_DATA_AVAILABLE)) {
                String isWhat = intent.getStringExtra("isWhat");
                if (isWhat.equals("isC")) {
                    byte[] txValue = intent.getByteArrayExtra(EXTRA_DATA);
                    // int x = txValue[0] & 0xff;
                    // int y = txValue[1] & 0xff;
                    // int z = txValue[2] & 0xff;
                    double x = txValue[0];
                    double y = txValue[1];
                    double z = txValue[2];
                    double newx = x * 0.488280 * 128 / 1000;
                    double newy = y * 0.488280 * 128 / 1000;
                    double newz = z * 0.488280 * 128 / 1000;
                    abAcceleration.setX(newx);
                    abAcceleration.setY(newy);
                    abAcceleration.setZ(newz);
                    // TODO 通过回调将abAcceleration返回给使用者
                    mWriteCallback.notifyABAcceleration(abAcceleration);

                } else if (isWhat.equals("isL")) {
                    byte[] txValue = intent.getByteArrayExtra(EXTRA_DATA_L);
                    int fir = (txValue[0] & 0xff);
                    int sec = ((txValue[1] << 8) & 0xff);
                    double light = (fir + sec) * 64000.0 / 65536.0;
                    mWriteCallback.notifyABLight(light);
                }
            }
            // *********************//
            // if (action.equals(DEVICE_DOES_NOT_SUPPORT_UART)) {
            //
            // }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (AprilBeaconUUID.ACCELEROMETER_NOTIFY_UUID.equals(characteristic
                .getUuid())) {
            intent.putExtra(EXTRA_DATA, characteristic.getValue());
        } else if (AprilBeaconUUID.LIGHT_NOTIFY_UUID.equals(characteristic
                .getUuid())) {
            intent.putExtra(EXTRA_DATA_L, characteristic.getValue());
        }
        context.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic, byte[] myValue) {
        final Intent intent = new Intent(action);
        if (AprilBeaconUUID.ACCELEROMETER_NOTIFY_UUID.equals(characteristic
                .getUuid())) {
            intent.putExtra("isWhat", "isC");
            intent.putExtra(EXTRA_DATA, myValue);
        } else if (AprilBeaconUUID.LIGHT_NOTIFY_UUID.equals(characteristic
                .getUuid())) {
            intent.putExtra("isWhat", "isL");
            intent.putExtra(EXTRA_DATA_L, characteristic.getValue());
        }
        context.sendBroadcast(intent);
    }

    /**
     * Enable Notification
     *
     * @return
     */
    private boolean enableNotification(UUID mService, UUID mCharacteristic,
                                       UUID mDescriptor) {

        BluetoothGattService RxService = mBluetoothGatt.getService(mService);
        if (RxService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }
        BluetoothGattCharacteristic TxChar = RxService
                .getCharacteristic(mCharacteristic);
        if (TxChar == null) {
            showMessage("Tx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar, true);

        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(mDescriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        boolean writeDescriptor = mBluetoothGatt.writeDescriptor(descriptor);
        return writeDescriptor;

    }

    private boolean writeCharacteristic(byte[] value, UUID mService,
                                        UUID mCharacteristic) {

        BluetoothGattService RxService = mBluetoothGatt.getService(mService);

        showMessage("mBluetoothGatt null" + mBluetoothGatt);
        if (RxService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }
        BluetoothGattCharacteristic RxChar = RxService
                .getCharacteristic(mCharacteristic);
        if (RxChar == null) {
            showMessage("Rx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }
        RxChar.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
        return status;
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt == null) {
            AprilL.w("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    private void showMessage(String msg) {
        AprilL.e(msg);
    }

    /**
     * 打开三轴加速计
     */
    public void turnOnCalu() {
        myTurnOnCalu();
    }

    private void myTurnOnCalu() {
        byte[] value = {(byte) (1 & 0xff)};
        boolean isTurnOn = writeCharacteristic(value,
                AprilBeaconUUID.ACCELEROMETER_SERVICE_UUID,
                AprilBeaconUUID.ACCELEROMETER_SWITCH_UUID);
        if (isTurnOn) {
            mWriteCallback.accelerometerStateChange(true);
        } else {
            mWriteCallback.turnOnOffError();
        }
    }

    /**
     * 关闭三轴加速计
     */
    public void turnOffCalu() {
        myTurnOffCalu();
    }

    private void myTurnOffCalu() {
        byte[] value = {(byte) (0 & 0xff)};
        boolean isTurnOff = writeCharacteristic(value,
                AprilBeaconUUID.ACCELEROMETER_SERVICE_UUID,
                AprilBeaconUUID.ACCELEROMETER_SWITCH_UUID);
        if (isTurnOff) {
            mWriteCallback.accelerometerStateChange(false);
        } else {
            mWriteCallback.turnOnOffError();
        }
    }

    /**
     * 开启加速度通知
     */
    public void enableACNotification() {
        myEnableACNotification();
    }

    private void myEnableACNotification() {
        boolean enableNotification = enableNotification(
                AprilBeaconUUID.ACCELEROMETER_SERVICE_UUID,
                AprilBeaconUUID.ACCELEROMETER_NOTIFY_UUID,
                AprilBeaconUUID.DESCRIPTOR);
        if (enableNotification) {
            mWriteCallback.enableAccelerometerNotification();
        } else {
            mWriteCallback.enableAccelerometerNotificationError();
        }
    }

    /**
     * 打开光线传感计
     */
    public void turnOnLight() {
        myTurnOnLight();
    }

    private void myTurnOnLight() {
        byte[] value = {(byte) (1 & 0xff)};
        boolean isLightOn = writeCharacteristic(value,
                AprilBeaconUUID.LIGHT_SERVICE_UUID,
                AprilBeaconUUID.LIGHT_SWITCH_UUID);
        if (isLightOn) {
            mWriteCallback.lightStateChange(true);
        } else {
            mWriteCallback.turnOnOffError();
        }
    }

    /**
     * 关闭光线传感计
     */
    public void turnOffLight() {
        myTurnOffLight();
    }

    private void myTurnOffLight() {
        byte[] value = {(byte) (0 & 0xff)};
        boolean isLightOff = writeCharacteristic(value,
                AprilBeaconUUID.LIGHT_SERVICE_UUID,
                AprilBeaconUUID.LIGHT_SWITCH_UUID);
        if (isLightOff) {
            mWriteCallback.lightStateChange(false);
        } else {
            mWriteCallback.turnOnOffError();
        }
    }

    /**
     * 开启光传感通知
     */
    public void enableLightNotification() {
        myEnableLightNotification();
    }

    private void myEnableLightNotification() {
        boolean enableNotification = enableNotification(
                AprilBeaconUUID.LIGHT_SERVICE_UUID,
                AprilBeaconUUID.LIGHT_NOTIFY_UUID, AprilBeaconUUID.DESCRIPTOR);
        if (enableNotification) {
            mWriteCallback.enableLightNotification();
        } else {
            mWriteCallback.enableLightNotificationError();
        }
    }

    public void addName(String name) {
        mAddName(name);
    }

    private void mAddName(String name) {
        if (names != null && !names.contains(name)) {
            names.add(name);
        }
    }
}
