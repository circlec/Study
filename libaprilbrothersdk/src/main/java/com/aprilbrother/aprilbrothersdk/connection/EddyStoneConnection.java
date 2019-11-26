package com.aprilbrother.aprilbrothersdk.connection;

import android.content.Context;

import com.aprilbrother.aprilbrothersdk.EddyStone;
import com.aprilbrother.aprilbrothersdk.Utils;
import com.aprilbrother.aprilbrothersdk.globalvariables.GlobalVariables;
import com.aprilbrother.aprilbrothersdk.receiver.ABeaconUARTStatusChangeReceiver.MyABeaconCallBack;
import com.aprilbrother.aprilbrothersdk.receiver.AprilBeaconUARTStatusChangeReceiver.MyAprBeaconCallBack;
import com.aprilbrother.aprilbrothersdk.services.ABeaconUartService;
import com.aprilbrother.aprilbrothersdk.services.UartService;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;

public class EddyStoneConnection {
	protected static final String TAG = "EddyStoneConnection";
	private final String name;
	private final String macAddress;
	private final String model;
	private Context context;

	private String password;

	private EddyStoneWriteCallBack callback;

	public EddyStoneConnection(String name, String macAddress, String model,
			Context context) {
		this.name = name;
		this.macAddress = macAddress;
		this.model = model;
		this.context = context;
		GlobalVariables.shouldChangeModel = false;
		GlobalVariables.shouldChangeUid = false;
		GlobalVariables.shouldChangeUrl = false;
	}

	public void connectGattToWrite(final String password,
			EddyStoneWriteCallBack mCallback) {
		this.callback = mCallback;
		this.password = password;
		if (name.startsWith("abeacon")||name.startsWith("asensor")) {
			BeaconConnection.connABeacon(context, macAddress, false,
					new MyABeaconCallBack() {
						@Override
						public void onNotificationOpen() {
							byte[] a = Utils.password2byte(password);
							BeaconConnection.writeValue(
									AprilBeaconUUID.ABEACON_SERVICE_UUID,
									AprilBeaconUUID.ABEACON_READ_UUID, a,
									ABeaconUartService.class.getSimpleName());
							super.onNotificationOpen();
						}

						@Override
						public void onWriteFinished() {
							callback.onWriteSuccess();
						}

						@Override
						public void onConnError(String errorMessage) {
							callback.onErrorOfConnection();
						}

						@Override
						public void onPasswordError() {
							callback.onPasswordWrong(password);
						}

					});
		} else if (name.startsWith("AprilBeacon") || name.startsWith("PROX")) {
			BeaconConnection.connAprilBeacon(context, macAddress, false,
					new MyAprBeaconCallBack() {

						@Override
						public void onWriteFinished() {
							// TODO 修改成功
							callback.onWriteSuccess();
						}

						@Override
						public void onConnected() {
							// TODO 连接成功 写入密码
							String myPassWord = "AT+AUTH" + password;
							byte[] value = myPassWord.getBytes();
							BeaconConnection.writeValue(
									AprilBeaconUUID.BEACON_SERVICE_UUID,
									AprilBeaconUUID.BEACON_PASSWORD, value,
									UartService.class.getSimpleName());
						}

					});
		}
	}

	/**
	 * 切换EddyStone广播模式
	 * 
	 * @param model
	 *            三种模式 EddyStone.MODEL_URL/EddyStone.MODEL_UID/EddyStone.
	 *            MODEL_IBEACON
	 */
	public void changerModel(String model) {
		GlobalVariables.shouldChangeModel = true;
		if (model.equals(EddyStone.MODEL_IBEACON)) {
			GlobalVariables.beaconWriteInfo.setModel(0);
		} else if (model.equals(EddyStone.MODEL_UID)) {
			GlobalVariables.beaconWriteInfo.setModel(1);
		} else if (model.equals(EddyStone.MODEL_URL)) {
			GlobalVariables.beaconWriteInfo.setModel(2);
		} else if (model.equals(EddyStone.MODEL_IBEACON_UID_URL)) {
			GlobalVariables.beaconWriteInfo.setModel(3);
		}
	}

	/**
	 * 更改广播的uid uid格式 12345678-1234-1234-1234-123456789012
	 * 
	 * @param uid
	 */
	public void changeUid(String uid) {
		if (GlobalVariables.shouldChangeModel) {
			return;
		}
		GlobalVariables.shouldChangeUid = true;
		GlobalVariables.beaconWriteInfo.setUid(uid);
	}
	public void changeUuid2(String uuid2) {
		if (GlobalVariables.shouldChangeModel) {
			return;
		}
		GlobalVariables.isWriteUUID2 = true;
		GlobalVariables.beaconWriteInfo.setWriteUuid2(uuid2);
	}
	/**
	 * 更改uid的前面部分 格式11223344556677889900
	 * 
	 * @param uid_namespace
	 */
	public void changeUidNameSpace(String uid_namespace) {
		if (GlobalVariables.shouldChangeModel) {
			return;
		}
		GlobalVariables.shouldChangeUidNameSpace = true;
		GlobalVariables.beaconWriteInfo.setUid_namespace(uid_namespace);
	}

	/**
	 * 更改uid的前面部分 格式112233445566
	 * 
	 * @param uid_custom
	 */
	public void changeUidCustom(String uid_custom) {
		if (GlobalVariables.shouldChangeModel) {
			return;
		}
		GlobalVariables.shouldChangeUidCustom = true;
		GlobalVariables.beaconWriteInfo.setUid_custom(uid_custom);
	}

	/**
	 * 更改广播的url url需要以一下格式开头 http://www. https://www. http:// https://
	 * 且内容长度不超过16
	 * 
	 * @param url
	 */
	public void changeUrl(String url) {
		if (GlobalVariables.shouldChangeModel) {
			return;
		}
		GlobalVariables.shouldChangeUrl = true;
		GlobalVariables.beaconWriteInfo.setUrl(url);
	}

	public void changeMajor(int major) {
		if (major >= 0 && major <= 65535) {
			GlobalVariables.isWriteMajor = true;
			GlobalVariables.beaconWriteInfo.setWriteMajor(major);
		} else {
			String a = "major 需在0-65535之间";
			AprilL.e(a);
		}
	}
	
	public void changeMajor2(int major2) {
		if (major2 >= 0 && major2 <= 65535) {
			GlobalVariables.isWriteMajor2 = true;
			GlobalVariables.beaconWriteInfo.setWriteMajor2(major2);
		} else {
			String a = "major2 需在0-65535之间";
			AprilL.e(a);
		}
	}

	public void changeMinor(int minor) {
		if (minor >= 0 && minor <= 65535) {
			GlobalVariables.isWriteMinor = true;
			GlobalVariables.beaconWriteInfo.setWriteMinor(minor);
		} else {
			String a = "minor 需在0-65535之间";
			AprilL.e(a);
		}
	}
	
	public void changeMinor2(int minor2) {
		if (minor2 >= 0 && minor2 <= 65535) {
			GlobalVariables.isWriteMinor2 = true;
			GlobalVariables.beaconWriteInfo.setWriteMinor2(minor2);
		} else {
			String a = "minor2 需在0-65535之间";
			AprilL.e(a);
		}
	}

	public void changeMeasuredPower(int measuredPower) {
		if (measuredPower < 0 && measuredPower > -256) {
			GlobalVariables.isWriteMeasuredPower = true;
			GlobalVariables.beaconWriteInfo
					.setWriteMeasuredPower(measuredPower + 256);
		} else {
			String a = "measuredPower 需在-256-0之间";
			AprilL.e(a);
		}
	}

	public void changeAdvertisingInterval(int advertisingInterval) {
		if (advertisingInterval > 0 && advertisingInterval < 101) {
			GlobalVariables.isWriteAdvertisingInterval = true;
			GlobalVariables.beaconWriteInfo.setWriteAdv(advertisingInterval);
		} else {
			String a = "advertisingInterval需在1-100之间";
			AprilL.e(a);
		}
	}

	public void changeTxPower(int txPower) {
		if (txPower >= 0 && txPower <= 8) {
			GlobalVariables.isWriteTxPower = true;
			GlobalVariables.beaconWriteInfo.setWriteTxPower(txPower);
		} else {
			String a = "txPower需在0-8之间";
			AprilL.e(a);
		}
	}

	public void changePassword(String password) {
		if (password.trim().length() == 12 || password.trim().length() == 6) {
			GlobalVariables.isWritePassword = true;
			GlobalVariables.beaconWriteInfo.setWritePassword(password);
		} else {
			String a = "password为12位或6位数字及字母组合";
			AprilL.e(a);
		}
	}

	public static class EddyStoneWriteCallBack {

		public void onErrorOfConnection() {
		};

		public void onPasswordWrong(String password) {
		};

		public void onWriteSuccess() {

		};
	}

	public void close() {
		BeaconConnection.close();
	}
}
