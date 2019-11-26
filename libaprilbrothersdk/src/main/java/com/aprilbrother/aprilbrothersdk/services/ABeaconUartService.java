package com.aprilbrother.aprilbrothersdk.services;

import java.util.List;
import java.util.UUID;

import com.aprilbrother.aprilbrothersdk.connection.AprilBeaconUUID;
import com.aprilbrother.aprilbrothersdk.constants.Constants;

import android.annotation.SuppressLint;
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
public class ABeaconUartService extends Service {
	private final static String TAG = ABeaconUartService.class.getSimpleName();

	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private String mBluetoothDeviceAddress;
	public BluetoothGatt mBluetoothGatt;

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			String intentAction;
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				intentAction = Constants.AB_ACTION_GATT_CONNECTED;
				broadcastUpdate(intentAction);
				mBluetoothGatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				intentAction = Constants.AB_ACTION_GATT_DISCONNECTED;
				broadcastUpdate(intentAction);
			} else {
				intentAction = Constants.AB_ACTION_GATT_DISCONNECTED;
				broadcastUpdate(intentAction);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(Constants.AB_ACTION_GATT_SERVICES_DISCOVERED_NEW);
			} else {
				broadcastUpdate(Constants.AB_ACTION_GATT_SERVICES_DISCOVERED_ERROR);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				broadcastUpdate(Constants.AB_ACTION_DATA_AVAILABLE,
						characteristic);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			broadcastUpdate(Constants.AB_ACTION_DATA_CHANGE, characteristic);
		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status != 0) {
				broadcastUpdate(Constants.AB_ACTION_DATA_WRITE_ERROR,
						characteristic);
			} else if (characteristic.getUuid().equals(
					AprilBeaconUUID.ABEACON_WRITE_UUID)) {
				broadcastUpdate(Constants.AB_ACTION_DATA_WRITE, characteristic);
			}
			super.onCharacteristicWrite(gatt, characteristic, status);
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
				BluetoothGattDescriptor descriptor, int status) {
			broadcastUpdate(Constants.AB_ACTION_DATA_DESCRIPTORWRITE);
			super.onDescriptorWrite(gatt, descriptor, status);
		}

	};

	/**
	 * bytes转换成十六进制字符串
	 */
	private String byte2HexStr(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		if (action.equals(Constants.AB_ACTION_DATA_CHANGE)) {
			intent.putExtra("intValue", characteristic.getIntValue(
					BluetoothGattCharacteristic.FORMAT_UINT8, 0));

			byte[] value = characteristic.getValue();
			String byte2HexStr = byte2HexStr(value);
			intent.putExtra("stringValue", byte2HexStr);

		} else if (action.equals(Constants.AB_ACTION_DATA_WRITE)) {
			intent.putExtra("intValue", characteristic.getIntValue(
					BluetoothGattCharacteristic.FORMAT_UINT8, 0));
		}else if (action.equals(Constants.AB_ACTION_DATA_AVAILABLE)) {
			intent.putExtra("uuid", characteristic.getUuid().toString());
            intent.putExtra("intValue", characteristic.getIntValue(
                    BluetoothGattCharacteristic.FORMAT_UINT8, 0));
            try {
                String strValue = characteristic.getStringValue(0);
                intent.putExtra("stringValue", strValue);
                byte[] value = characteristic.getValue();
                String byte2HexStr = byte2HexStr(value);
                intent.putExtra("byteString", byte2HexStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
		sendBroadcast(intent);
	}

	public class LocalBinder extends Binder {
		public ABeaconUartService getService() {
			return ABeaconUartService.this;
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
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
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
			Log.w(TAG,
					"BluetoothAdapter not initialized or unspecified address.");
			return false;
		}

		if (mBluetoothDeviceAddress != null
				&& address.equals(mBluetoothDeviceAddress)
				&& mBluetoothGatt != null) {
			if (mBluetoothGatt.connect()) {
				return true;
			} else {
				return false;
			}
		}

		final BluetoothDevice device = mBluetoothAdapter
				.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "Device not found.  Unable to connect.");
			return false;
		}
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
		mBluetoothDeviceAddress = address;
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
			Log.w(TAG, "BluetoothAdapter not initialized");
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
			Log.w(TAG, "BluetoothAdapter not initialized");
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
			broadcastUpdate(Constants.AB_DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		BluetoothGattCharacteristic TxChar = RxService
				.getCharacteristic(characteristic);
		if (TxChar == null) {
			showMessage("Tx charateristic not found!");
			broadcastUpdate(Constants.AB_DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(TxChar, true);
		BluetoothGattDescriptor descriptor = TxChar.getDescriptor(myDescriptor);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
		boolean writeDescriptor = mBluetoothGatt.writeDescriptor(descriptor);
	}

	public void writeRXCharacteristic(UUID serviceUUID,
			UUID CharacteristicUUID, byte[] value) {

		BluetoothGattService RxService = mBluetoothGatt.getService(serviceUUID);
		showMessage("mBluetoothGatt null" + mBluetoothGatt);
		if (RxService == null) {
			showMessage("Rx service not found!");
			broadcastUpdate(Constants.AB_DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		BluetoothGattCharacteristic RxChar = RxService
				.getCharacteristic(CharacteristicUUID);

		if (RxChar == null) {
			showMessage("Rx charateristic not found!");
			broadcastUpdate(Constants.AB_DEVICE_DOES_NOT_SUPPORT_UART);
			return;
		}
		boolean setValue = RxChar.setValue(value);
		if (!setValue) {
			broadcastUpdate(Constants.AB_ACTION_DATA_WRITE_ERROR);
		}
		boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
		if (!status) {
			broadcastUpdate(Constants.AB_ACTION_DATA_WRITE_ERROR);
			Log.d(TAG, "CharacteristicUUID=" + CharacteristicUUID.toString());
		}
		Log.d(TAG, "write TXchar - status=" + status);
	}

	public void readCharacteristic(UUID serviceUUID, UUID CharacteristicUUID) {
		BluetoothGattService RxService = this.mBluetoothGatt
				.getService(serviceUUID);
		this.showMessage("mBluetoothGatt null" + this.mBluetoothGatt);
		if (RxService != null
				&& RxService.getCharacteristic(CharacteristicUUID) != null) {
			boolean isRead = this.mBluetoothGatt.readCharacteristic(RxService
					.getCharacteristic(CharacteristicUUID));
		}
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
		if (mBluetoothGatt == null)
			return null;

		return mBluetoothGatt.getServices();
	}
}
