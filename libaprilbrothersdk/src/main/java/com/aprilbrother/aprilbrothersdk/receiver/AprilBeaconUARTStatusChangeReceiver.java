package com.aprilbrother.aprilbrothersdk.receiver;

import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Global;
import android.util.Log;

import com.aprilbrother.aprilbrothersdk.Utils;
import com.aprilbrother.aprilbrothersdk.bean.BeaconDetailInfo;
import com.aprilbrother.aprilbrothersdk.connection.AprilBeaconUUID;
import com.aprilbrother.aprilbrothersdk.constants.Constants;
import com.aprilbrother.aprilbrothersdk.globalvariables.GlobalVariables;
import com.aprilbrother.aprilbrothersdk.services.UartService;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;
import com.aprilbrother.aprilbrothersdk.utils.BLEScanRecordUtil;
import com.aprilbrother.aprilbrothersdk.utils.UUID2bytesUtils;

public class AprilBeaconUARTStatusChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "AprilBeaconUARTStatusChangeReceiver";

	private UartService mService;
	private MyAprBeaconCallBack callBack;
	private boolean shouldRead;
	BeaconDetailInfo detailInfo = new BeaconDetailInfo();

	public AprilBeaconUARTStatusChangeReceiver(UartService mService,
			MyAprBeaconCallBack callBack) {
		this.callBack = callBack;
		this.mService = mService;
	}

	public AprilBeaconUARTStatusChangeReceiver(UartService mService,
			boolean shouldRead, MyAprBeaconCallBack callBack) {
		this.shouldRead = shouldRead;
		this.callBack = callBack;
		this.mService = mService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		setReceiver(intent);
	}

	private void setReceiver(Intent intent) {
		String action = intent.getAction();
		if (action.equals(Constants.ACTION_DATA_WRITE_PASSWORD_ERROR)) {// 密码错误
			callBack.onPasswordWrong();
		}

		// *********************//
		if (action.equals(Constants.ACTION_GATT_SERVICES_DISCOVERED_NEW)) {// 连接成功发现服务后接收的广播
			if (shouldRead) {
				readAprilBeacon();
			}
			callBack.onConnected();
		}
		// *********************//
		if (action.equals(Constants.ACTION_DATA_AVAILABLE)) {// 接收 值读取时的广播
			parseAndReadAprilBeacon(intent);
		}
		if (action.equals(Constants.ACTION_DATA_WRITE)) {// 接收 值写入时的广播
			if (GlobalVariables.shouldChangeModel) {
				// change model
				changeModel();
				GlobalVariables.shouldChangeModel = false;
			} else if (GlobalVariables.shouldChangeUrl) {
				// change url
				changeUrl();
				GlobalVariables.shouldChangeUrl = false;
			} else if (GlobalVariables.shouldChangeUid) {
				// change uid
				changeUid();
				GlobalVariables.shouldChangeUid = false;
			} else if (GlobalVariables.shouldChangeUidNameSpace) {
				// TODO 更改uid_namespace
				changeUidNameSpace();
				GlobalVariables.shouldChangeUidNameSpace = false;
			} else if (GlobalVariables.shouldChangeUidCustom) {
				// TODO 更改uid_custom
				changeUidCustom();
				GlobalVariables.shouldChangeUidCustom = false;
			} else if (GlobalVariables.isWriteUUID) {
				changeUid();
				GlobalVariables.isWriteUUID = false;
			} else if (GlobalVariables.isWriteMajor) {
				changeMajor();
				GlobalVariables.isWriteMajor = false;
			} else if (GlobalVariables.isWriteMinor) {
				changeMinor();
				GlobalVariables.isWriteMinor = false;
			} else if (GlobalVariables.isWriteAdvertisingInterval) {
				changeAdv();
				GlobalVariables.isWriteAdvertisingInterval = false;
			} else if (GlobalVariables.isWriteMeasuredPower) {
				changeMeasuredPower();
				GlobalVariables.isWriteMeasuredPower = false;
			} else if (GlobalVariables.isWriteTxPower) {
				changeTxPower();
				GlobalVariables.isWriteTxPower = false;
			} else if (GlobalVariables.isWritePassword) {
				changePassword();
				GlobalVariables.isWritePassword = false;
			} else {
				String reset = "AT+RESET";
				byte[] value = reset.getBytes();
				mService.writeRXCharacteristic(
						AprilBeaconUUID.BEACON_SERVICE_UUID,
						AprilBeaconUUID.BEACON_PASSWORD, value);
				callBack.onWriteFinished();
			}
		}
	}

	private void changeUidNameSpace() {
		byte[] data1 = "AT+ENID".getBytes();
		byte[] data2 = Utils.hexStringToBytes(GlobalVariables.beaconWriteInfo
				.getUid_namespace());
		byte[] data3 = new byte[data1.length + data2.length];
		System.arraycopy(data1, 0, data3, 0, data1.length);
		System.arraycopy(data2, 0, data3, data1.length, data2.length);
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_PASSWORD, data3);
	}

	private void changeUidCustom() {
		byte[] data1 = "AT+EBID".getBytes();
		byte[] data2 = Utils.hexStringToBytes(GlobalVariables.beaconWriteInfo
				.getUid_custom());
		byte[] data3 = new byte[data1.length + data2.length];
		System.arraycopy(data1, 0, data3, 0, data1.length);
		System.arraycopy(data2, 0, data3, data1.length, data2.length);
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_PASSWORD, data3);
	}

	private void changeMajor() {
		byte[] majorbyte = {
				(byte) ((byte) (GlobalVariables.beaconWriteInfo.getWriteMajor() >> 8) & 0xff),
				(byte) ((byte) GlobalVariables.beaconWriteInfo.getWriteMajor() & 0xff) };
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_MAJOR_UUID, majorbyte);
	}

	private void changeMinor() {
		byte[] minorbyte = {
				(byte) ((byte) (GlobalVariables.beaconWriteInfo.getWriteMinor() >> 8) & 0xff),
				(byte) ((byte) GlobalVariables.beaconWriteInfo.getWriteMinor() & 0xff) };
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_MINOR_UUID, minorbyte);
	}

	private void changeAdv() {
		byte[] advertisingInterval = { (byte) (GlobalVariables.beaconWriteInfo
				.getWriteAdv() & 0xff) };
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_ADVINTERVAL_UUID, advertisingInterval);
	}

	private void changeMeasuredPower() {
		byte[] measuredPower = { (byte) (GlobalVariables.beaconWriteInfo
				.getWriteMeasuredPower() & 0xff) };
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_MEASURED_POWER_UUID, measuredPower);
	}

	private void changeTxPower() {
		byte[] txPowerbyte = { (byte) (GlobalVariables.beaconWriteInfo
				.getWriteTxPower() & 0xff) };
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_TXPOWER_UUID, txPowerbyte);
	}

	private void changePassword() {
		byte[] newPasswordByte = GlobalVariables.beaconWriteInfo
				.getWritePassword().getBytes();
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_PASSCODE_UUID, newPasswordByte);
	}

	private void changeUid() {
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_PROXIMITY_UUID, UUID2bytesUtils
						.uuid2Bytes(GlobalVariables.beaconWriteInfo.getUid()));
	}

	private void changeUrl() {
		String strUrl = GlobalVariables.beaconWriteInfo.getUrl();

		byte first = 0;
		if (strUrl.startsWith("http://www.")) {
			first = (byte) 0;
			strUrl = strUrl.substring(11);
		} else if (strUrl.startsWith("https://www.")) {
			first = (byte) 1;
			strUrl = strUrl.substring(12);
		} else if (strUrl.startsWith("http://")) {
			first = (byte) 2;
			strUrl = strUrl.substring(7);
		} else if (strUrl.startsWith("https://")) {
			first = (byte) 3;
			strUrl = strUrl.substring(8);
		} else {
			return;
		}
		byte[] value = BLEScanRecordUtil.setWriteURLValue(strUrl, first);
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_PASSWORD, value);
	}

	private void changeModel() {
		String urlModel = "AT+BROD";
		byte[] value = urlModel.getBytes();
		byte b = (byte) GlobalVariables.beaconWriteInfo.getModel();
		byte[] byte_3 = new byte[value.length + 1];
		System.arraycopy(value, 0, byte_3, 0, value.length);
		byte_3[byte_3.length - 1] = b;
		mService.writeRXCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
				AprilBeaconUUID.BEACON_PASSWORD, byte_3);
	}

	private void readAprilBeacon() {
		BluetoothGattService service = mService.mBluetoothGatt
				.getService(AprilBeaconUUID.BATTERY_UUID);
		if (service != null) {
			BluetoothGattCharacteristic characteristic = service
					.getCharacteristic(AprilBeaconUUID.BATTERY_LEVEL);
			if (characteristic != null)
				mService.readCharacteristic(characteristic);
		} else if (mService.mBluetoothGatt
				.getService(AprilBeaconUUID.DEVICE_INFORMATION) != null) {
			BluetoothGattService service1 = mService.mBluetoothGatt
					.getService(AprilBeaconUUID.DEVICE_INFORMATION);
			BluetoothGattCharacteristic characteristic = service1
					.getCharacteristic(AprilBeaconUUID.MODEL_NUMBER);
			if (characteristic != null)
				mService.readCharacteristic(characteristic);
		} else if (mService.mBluetoothGatt
				.getService(AprilBeaconUUID.BEACON_SERVICE_UUID) != null) {
			BluetoothGattService service2 = mService.mBluetoothGatt
					.getService(AprilBeaconUUID.BEACON_SERVICE_UUID);
			BluetoothGattCharacteristic characteristic = service2
					.getCharacteristic(AprilBeaconUUID.BEACON_ADVINTERVAL_UUID);
			if (characteristic != null) {
				mService.readCharacteristic(characteristic);
			}
		}
	}

	private void parseAndReadAprilBeacon(Intent intent) {
		String isWhat = intent.getStringExtra("isWhat");
		if (isWhat != null) {
			if (isWhat.equals("battery")) {
				int battery = intent.getIntExtra("batteryLevel", 0);
				detailInfo.setBattery(battery);
				begineReadCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.MODEL_NUMBER);

			} else if (isWhat.equals("model_number")) {
				String model_number = intent.getStringExtra("model_number");
				if (model_number != null)
					detailInfo.setModelNumber(model_number);
				begineReadCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.SERIAL_NUMBER);
			} else if (isWhat.equals("serial_number")) {
				String serial_number = intent.getStringExtra("serial_number");
				if (serial_number != null)
					detailInfo.setSerialNumber(serial_number);
				begineReadCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.FW);
			} else if (isWhat.equals("fw")) {
				String fw = intent.getStringExtra("fw");
				if (fw != null)
					detailInfo.setFw(fw);
				begineReadCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.HW);
			} else if (isWhat.equals("hw")) {
				String hw = intent.getStringExtra("hw");
				if (hw != null)
					detailInfo.setHw(hw);
				begineReadCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.SW);
			} else if (isWhat.equals("sw")) {
				String sw = intent.getStringExtra("sw");
				if (sw != null)
					detailInfo.setSw(sw);
				begineReadCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.MANUFACTURER);
			} else if (isWhat.equals("manufacturer")) {
				String manufacturer = intent.getStringExtra("manufacturer");
				if (manufacturer != null)
					detailInfo.setManufacturer(manufacturer);
				begineReadCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.IEEE);
			} else if (isWhat.equals("ieee")) {
				String ieee = intent.getStringExtra("ieee");
				if (ieee != null)
					detailInfo.setIeee(ieee);
				begineReadCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
						AprilBeaconUUID.BEACON_ADVINTERVAL_UUID);
			} else if (isWhat.equals("devicesadvert")) {
				int devicesadvert = intent.getIntExtra("devicesadvert", -1);
				if (devicesadvert != -1)
					detailInfo.setAdv(devicesadvert);
				begineReadCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
						AprilBeaconUUID.BEACON_TXPOWER_UUID);

			} else if (isWhat.equals("devicestxpower")) {
				int devicestxpower = intent.getIntExtra("devicestxpower", -1);
				if (devicestxpower != -1)
					detailInfo.setTxPower(devicestxpower);
				begineReadCharacteristic(AprilBeaconUUID.BEACON_SERVICE_UUID,
						AprilBeaconUUID.BEACON_MEASURED_POWER_UUID);
			} else if (isWhat.equals("measured_power")) {
				int measured_power = intent.getIntExtra("measured_power", -1);
				measured_power = measured_power - 256;
				if (measured_power != -1) {
					detailInfo.setMeasuredPower(measured_power);
					callBack.onReadFinished(detailInfo);
				}
			}
		}
	}

	private void begineReadCharacteristic(UUID serviceUUID,
			UUID characteristicUUID) {
		BluetoothGattService service = mService.mBluetoothGatt
				.getService(serviceUUID);
		if (service != null) {
			BluetoothGattCharacteristic characteristic = service
					.getCharacteristic(characteristicUUID);
			if (characteristic != null) {
				mService.readCharacteristic(characteristic);
			}
		}
	}

	public static class MyAprBeaconCallBack {
		public void onReadFinished(BeaconDetailInfo detailInfo) {
		};

		public void onPasswordWrong() {
		};

		public void onWriteFinished() {
		};

		public void onConnected() {
		};
	}
}
