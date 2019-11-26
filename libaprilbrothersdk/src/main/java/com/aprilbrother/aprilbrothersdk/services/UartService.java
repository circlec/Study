/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aprilbrother.aprilbrothersdk.services;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import com.aprilbrother.aprilbrothersdk.connection.AprilBeaconUUID;
import com.aprilbrother.aprilbrothersdk.constants.Constants;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class UartService extends Service {
	private final static String TAG = UartService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	public BluetoothGatt mBluetoothGatt;
	private int mConnectionState = STATE_DISCONNECTED;

	private static final int STATE_DISCONNECTED = 0;
	private static final int STATE_CONNECTING = 1;
	private static final int STATE_CONNECTED = 2;

	public final static String ACTION_GATT_CONNECTED = "com.nordicsemi.nrfUART.ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = "com.nordicsemi.nrfUART.ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED_NEW = "com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED_NEW";
	public final static String ACTION_DATA_AVAILABLE = "com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE";
	public final static String ACTION_DATA_CHANGE = "com.nordicsemi.nrfUART.ACTION_DATA_CHANGE";
	public final static String ACTION_DATA_WRITE = "com.nordicsemi.nrfUART.ACTION_DATA_WRITE";
	public final static String ACTION_DATA_RESET = "com.nordicsemi.nrfUART.ACTION_DATA_RESET";

	public final static String EXTRA_DATA = "com.nordicsemi.nrfUART.EXTRA_DATA";
	public final static String DEVICE_DOES_NOT_SUPPORT_UART = "com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";

	public final static String ACTION_GATT_SERVICES_DISCOVERED_ERROR = "com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED_ERROR";
	public final static String ACTION_DATA_AVAILABLE_ERROR = "com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE_ERROR";
	public final static String ACTION_DATA_WRITE_ERROR = "com.nordicsemi.nrfUART.ACTION_DATA_WRITE_ERROR";
	public final static String ACTION_DATA_WRITE_PASSWORD_ERROR = "com.nordicsemi.nrfUART.ACTION_DATA_WRITE_PASSWORD_ERROR";

	public static final UUID TX_POWER_UUID = UUID
			.fromString("00001804-0000-1000-8000-00805f9b34fb");
	public static final UUID TX_POWER_LEVEL_UUID = UUID
			.fromString("00002a07-0000-1000-8000-00805f9b34fb");
	public static final UUID CCCD = UUID
			.fromString("00002901-0000-1000-8000-00805f9b34fb");
	public static final UUID FIRMWARE_REVISON_UUID = UUID
			.fromString("00002a26-0000-1000-8000-00805f9b34fb");
	public static final UUID DIS_UUID = UUID
			.fromString("0000180a-0000-1000-8000-00805f9b34fb");
	public static final UUID RX_SERVICE_UUID = UUID
			.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
	public static final UUID RX_CHAR_UUID = UUID
			.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
	public static final UUID TX_CHAR_UUID = UUID
			.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;

			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = ACTION_GATT_CONNECTED;
				mConnectionState = STATE_CONNECTED;
				broadcastUpdate(intentAction);
				mBluetoothGatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = ACTION_GATT_DISCONNECTED;
				mConnectionState = STATE_DISCONNECTED;
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(Constants.ACTION_GATT_SERVICES_DISCOVERED_NEW);
			} else {
				broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED_ERROR);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(Constants.ACTION_DATA_AVAILABLE, characteristic);
			} else {
				broadcastUpdate(ACTION_DATA_AVAILABLE_ERROR);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(ACTION_DATA_CHANGE, characteristic);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(Constants.ACTION_DATA_WRITE, characteristic);
			} else if (status == 3) {
				broadcastUpdate(Constants.ACTION_DATA_WRITE_PASSWORD_ERROR);
			} else {
				broadcastUpdate(ACTION_DATA_WRITE_ERROR, characteristic);
			}

			super.onCharacteristicWrite(gatt, characteristic, status);
		}
	};

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		if (action.equals(Constants.ACTION_DATA_AVAILABLE)) {
			if (AprilBeaconUUID.BATTERY_LEVEL.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "battery");
				final Integer batteryLevel = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
				int mBatteryLevel = batteryLevel;
				intent.putExtra("batteryLevel", mBatteryLevel);
			} else if (AprilBeaconUUID.MODEL_NUMBER.equals(characteristic
					.getUuid())) {
				intent.putExtra("isWhat", "model_number");
				final String s = characteristic.getStringValue(0);
				intent.putExtra("model_number", s);
			} else if (AprilBeaconUUID.SERIAL_NUMBER.equals(characteristic
					.getUuid())) {
				intent.putExtra("isWhat", "serial_number");
				final String s = characteristic.getStringValue(0);
				intent.putExtra("serial_number", s);
			} else if (AprilBeaconUUID.FW.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "fw");
				final String s = characteristic.getStringValue(0);
				intent.putExtra("fw", s);
			} else if (AprilBeaconUUID.HW.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "hw");
				final String s = characteristic.getStringValue(0);
				intent.putExtra("hw", s);
			} else if (AprilBeaconUUID.SW.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "sw");
				final String s = characteristic.getStringValue(0);
				intent.putExtra("sw", s);
			} else if (AprilBeaconUUID.MANUFACTURER.equals(characteristic
					.getUuid())) {
				intent.putExtra("isWhat", "manufacturer");
				final String s = characteristic.getStringValue(0);
				intent.putExtra("manufacturer", s);
			} else if (AprilBeaconUUID.IEEE.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "ieee");
				String newString = "";
				try {
					byte[] s = characteristic.getStringValue(0).getBytes(
							"gb2312");
					newString = new String(s, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				intent.putExtra("ieee", newString);
			} else if (AprilBeaconUUID.BEACON_ADVINTERVAL_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "devicesadvert");
				Integer devicesAdvert = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
				int mDevicesAdvert = devicesAdvert;
				intent.putExtra("devicesadvert", mDevicesAdvert);
			} else if (AprilBeaconUUID.BEACON_TXPOWER_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "devicestxpower");
				Integer devicesTxpower = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
				int mDevicesTxpower = devicesTxpower;
				intent.putExtra("devicestxpower", mDevicesTxpower);
			} else if (AprilBeaconUUID.BEACON_MEASURED_POWER_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "measured_power");
				Integer measured_power = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0);
				int mp = measured_power;
				intent.putExtra("measured_power", mp);
			}
		} else if (action.equals(Constants.ACTION_DATA_WRITE)) {
			if (AprilBeaconUUID.BEACON_PASSWORD
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "isPassword");
			} else if (AprilBeaconUUID.BEACON_PROXIMITY_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "isUUID");
			} else if (AprilBeaconUUID.BEACON_MAJOR_UUID.equals(characteristic
					.getUuid())) {
				intent.putExtra("isWhat", "isMajor");
			} else if (AprilBeaconUUID.BEACON_MINOR_UUID.equals(characteristic
					.getUuid())) {
				intent.putExtra("isWhat", "isMinor");
			} else if (AprilBeaconUUID.BEACON_TXPOWER_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "isTxpower");
			} else if (AprilBeaconUUID.BEACON_MEASURED_POWER_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "isMeasuredpower");
			} else if (AprilBeaconUUID.BEACON_ADVINTERVAL_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "isAdvinterval");
			} else if (AprilBeaconUUID.BEACON_PASSCODE_UUID
					.equals(characteristic.getUuid())) {
				intent.putExtra("isWhat", "isPasscode");
			}
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public UartService getService() {
			return UartService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		close();
		return super.onUnbind(intent);
	}

	private final IBinder mBinder = new LocalBinder();

	/**
	 * Initializes a reference to the local Bluetooth adapter.
	 * 
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				return false;
			}
		}

		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			return false;
		}

		return true;
	}

	/**
	 * Connects to the GATT server hosted on the Bluetooth LE device.
	 * 
	 * @param address
	 *            The device address of the destination device.
	 * 
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {
		initialize();
		if (mBluetoothAdapter == null || address == null) {
			return false;
		}

		// Previously connected device. Try to reconnect.
		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				mConnectionState = STATE_CONNECTING;
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			return false;
		}
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		mBluetoothDeviceAddress = address;
		mConnectionState = STATE_CONNECTING;
		return true;
	}

	/**
	 * Disconnects an existing connection or cancel a pending connection. The
	 * disconnection result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 * callback.
	 */
	public void disconnect() {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.disconnect();
	}

	/**
	 * After using a given BLE device, the app must call this method to ensure
	 * resources are released properly.
	 */
	public void close() {
		if (mBluetoothGatt == null) {
			return;
		}
		mBluetoothDeviceAddress = null;
		mBluetoothGatt.close();
		mBluetoothGatt = null;
	}

	/**
	 * Request a read on a given {@code BluetoothGattCharacteristic}. The read
	 * result is reported asynchronously through the
	 * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
	 * callback.
	 * 
	 * @param characteristic
	 *            The characteristic to read from.
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			return;
		}
		mBluetoothGatt.readCharacteristic(characteristic);
	}

	/**
	 * Enable TXNotification
	 * 
	 * @return
	 */
	public void enableTXNotification(UUID service, UUID characteristic,
			UUID myDescriptor) {
		BluetoothGattService RxService = mBluetoothGatt.getService(service);
		if (RxService == null) {
			showMessage("Rx service not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		BluetoothGattCharacteristic TxChar = RxService
				.getCharacteristic(characteristic);
		if (TxChar == null) {
			showMessage("Tx charateristic not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(TxChar, true);
		BluetoothGattDescriptor descriptor = TxChar.getDescriptor(myDescriptor);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(descriptor);

	}

	public void writeRXCharacteristic(UUID serviceUUID,
			UUID characteristicUUID, byte[] value) {

		BluetoothGattService RxService = mBluetoothGatt.getService(serviceUUID);
		showMessage("mBluetoothGatt null" + mBluetoothGatt);
		if (RxService == null) {
			showMessage("Rx service not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		BluetoothGattCharacteristic RxChar = RxService
				.getCharacteristic(characteristicUUID);
		if (RxChar == null) {
			showMessage("Rx charateristic not found!");
			broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		RxChar.setValue(value);
		boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
		Log.d(TAG, "write TXchar - status=" + status);
	}

	private void showMessage(String msg) {
		Log.e(TAG, msg);
	}

	/**
	 * Retrieves a list of supported GATT services on the connected device. This
	 * should be invoked only after {@code BluetoothGatt#discoverServices()}
	 * completes successfully.
	 * 
	 * @return A {@code List} of supported services.
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (mBluetoothGatt == null) {
			return null;
		}
		return mBluetoothGatt.getServices();
	}
}
