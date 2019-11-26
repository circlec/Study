package com.aprilbrother.aprilbrothersdk.receiver;

import java.util.List;
import java.util.UUID;

import com.aprilbrother.aprilbrothersdk.bean.BeaconDetailInfo;
import com.aprilbrother.aprilbrothersdk.connection.AprilBeaconConnection;
import com.aprilbrother.aprilbrothersdk.connection.AprilBeaconUUID;
import com.aprilbrother.aprilbrothersdk.constants.Constants;
import com.aprilbrother.aprilbrothersdk.globalvariables.GlobalVariables;
import com.aprilbrother.aprilbrothersdk.services.ABeaconUartService;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;
import com.aprilbrother.aprilbrothersdk.utils.BLEScanRecordUtil;
import com.aprilbrother.aprilbrothersdk.utils.UUID2bytesUtils;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 省电王beacon的修改设备时的广播接收器 在这里接收操作后的反馈及收到反馈后的写入或者读取操作
 * 
 * @author think_admin
 * 
 */
public class ABeaconUARTStatusChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "ABeaconUARTStatusChangeReceiver";

	private ABeaconUartService mService;
	private MyABeaconCallBack callBack;
	private boolean shouldABeaconRead;
	private boolean isReadBattery;
	private boolean isSecondUUID;
	BeaconDetailInfo detailInfo = new BeaconDetailInfo();

	public ABeaconUARTStatusChangeReceiver(ABeaconUartService mService,
			MyABeaconCallBack callBack) {
		this.mService = mService;
		this.callBack = callBack;
	}

	/**
	 * 广播接收器的构造函数
	 * 
	 * @param mService
	 *            对应的修改beacon操作的service
	 * @param shouldABeaconRead
	 *            是否要读取beacon设备信息
	 * @param callBack
	 *            操作的回调
	 */
	public ABeaconUARTStatusChangeReceiver(ABeaconUartService mService,
			boolean shouldABeaconRead, MyABeaconCallBack callBack) {
		this.shouldABeaconRead = shouldABeaconRead;
		this.mService = mService;
		this.callBack = callBack;
	}

	public ABeaconUARTStatusChangeReceiver(ABeaconUartService mService,
			boolean shouldABeaconRead, boolean isReadBattery,
			MyABeaconCallBack callBack) {
		this.shouldABeaconRead = shouldABeaconRead;
		this.isReadBattery = isReadBattery;
		this.mService = mService;
		this.callBack = callBack;
	}

	public ABeaconUARTStatusChangeReceiver(ABeaconUartService mService,
			boolean shouldABeaconRead, boolean isReadBattery,
			boolean isSecondUUID, MyABeaconCallBack callBack) {
		this.shouldABeaconRead = shouldABeaconRead;
		this.isReadBattery = isReadBattery;
		this.isSecondUUID = isSecondUUID;
		this.mService = mService;
		this.callBack = callBack;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		setReceiver(intent);
	}

	private void setReceiver(Intent intent) {
		String action = intent.getAction();

		// *********************//
		if (action.equals(Constants.AB_ACTION_GATT_DISCONNECTED)) {// 设备断开连接时接收的广播
			callBack.onConnError(Constants.AB_ACTION_GATT_DISCONNECTED);
		}
		// *********************//
		if (action.equals(Constants.AB_ACTION_GATT_SERVICES_DISCOVERED_NEW)) {// 连接成功发现服务后接收的广播
			// 检测是否有电量服务 有电量服务则支持eddystone格式广播
			boolean haveBattery = false;
			List<BluetoothGattService> services = mService
					.getSupportedGattServices();
			if (services != null && services.size() > 0) {
				for (BluetoothGattService service : services) {
					if (service.getUuid().equals(AprilBeaconUUID.BATTERY_UUID)) {
						haveBattery = true;
						detailInfo.setHaveBatteryService(true);
						break;
					}
				}
			}
			if (!isReadBattery) {
				mService.enableTXNotification(
						AprilBeaconUUID.ABEACON_SERVICE_UUID,
						AprilBeaconUUID.ABEACON_NOTIFY_UUID,
						AprilBeaconUUID.CCCD);
			} else if (haveBattery) {
				mService.readCharacteristic(AprilBeaconUUID.BATTERY_UUID,
						AprilBeaconUUID.BATTERY_LEVEL);
			} else {
				callBack.onConnError("no battery service,please use new firmware abeacon");
			}
		}

		if (action.equals(Constants.AB_ACTION_DATA_DESCRIPTORWRITE)) {
			if (shouldABeaconRead) {// 需要读取设备信的话执行读取操作
				byte[] a = { (byte) (0 & 0xff) };
				mService.writeRXCharacteristic(
						AprilBeaconUUID.ABEACON_SERVICE_UUID,
						AprilBeaconUUID.ABEACON_READ_UUID, a);
			}
			callBack.onNotificationOpen();
		}
		if (action.equals(Constants.AB_ACTION_DATA_CHANGE)) {// 接收值改变时的广播
			int intExtra = intent.getIntExtra("intValue", -1);
			String stringValue = intent.getStringExtra("stringValue");
			if (stringValue.equals("07010001")) {// 写入密码成功返回
				if (GlobalVariables.shouldChangeModel) {
					begainWriteABeaconModel();
				} else if (GlobalVariables.shouldChangeUrl) {
					begainWriteABeaconURL();
				} else {
					begainWriteABeaconInfo();
				}
			} else if (stringValue.equals("07010000")) {// 写入密码失败的返回 密码错误
				callBack.onPasswordError();
			} else {// 读取信息操作
				readABeaconInfo(intExtra, stringValue);
			}
		}
		if (action.equals(Constants.AB_ACTION_DATA_WRITE)) {// 接收值写入时的广播
			int intExtra = intent.getIntExtra("intValue", -1);
			switch (intExtra) {
			case 0:
				if (GlobalVariables.shouldChangeUid) {// 如果是写入uid则到此步骤完成
					// 变更逻辑 修改uid成功后也需要更改其他参数
					// callBack.onWriteFinished();
					setWriteMajor();
				} else {// 否则的话是修改iBeacon 这时看major等其他参数是否需要修改
					if (GlobalVariables.isWriteUUID)
						callBack.onWriteUUIDSuccess();
					setWriteMajor();
				}
				break;
			case 1:// 更改major成功返回1
				callBack.onWriteMajorSuccess();
				setWriteMinor();
				break;
			case 2:// 更改minor成功返回2
				callBack.onWriteMinorSuccess();
				setWriteAvd();
				break;
			case 4:// 更改广播频率成功返回4
				callBack.onWriteAdvSuccess();
				setWriteMeasuredPower();
				break;
			case 5:// 更改校准值成功返回5
				callBack.onWriteMeasuredPowerSuccess();
				setWriteTxPower();
				break;
			case 6:// 更改发射功率成功返回6
				callBack.onWriteTxPowerSuccess();
				// setWriteMajor2();
				setUuid2();
				break;
			case 7:// 更改密码成功返回7
				callBack.onWritePassCodeSuccess();
				callBack.onWriteFinished();
				break;
			case 8:// 更改模式成功
				callBack.onWriteFinished();
				break;
			case 10:// 更改url成功
				// callBack.onWriteFinished();
				// 变更逻辑 修改url成功后也需要更改其他参数
				begainWriteABeaconInfo();
				break;
			case 11:
				setWriteMajor();
				break;
			case 14:
				setWriteMajor2();
				callBack.onWriteUUID2Success();
				break;
			case 15:// 更改major2成功
				setWriteMinor2();
				callBack.onWriteMajor2Success();
				break;
			case 16:// 更改minor2成功
				setWriteSecretKey();
				callBack.onWriteMinor2Success();
				break;
			case 17:
				setWritePassCode();
				break;
			default:
				break;
			}
		}
		if (action.equals(Constants.AB_ACTION_DATA_AVAILABLE)) {// 读取电量信息
			// int battery = intent.getIntExtra("intValue", -1);
			// detailInfo.setBattery(battery);
			// callBack.onReadFinished(detailInfo);
			int intExtra = intent.getIntExtra("intValue", -1);
			String stringValue = intent.getStringExtra("stringValue");
			String byteString = intent.getStringExtra("byteString");
			String uuid = intent.getStringExtra("uuid");
			if (AprilBeaconUUID.BATTERY_LEVEL.toString().toUpperCase()
					.equals(uuid.toUpperCase())) {
				detailInfo.setBattery(intExtra);
				mService.readCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.FW);
			} else if (AprilBeaconUUID.FW.toString().toUpperCase()
					.equals(uuid.toUpperCase())) {
				detailInfo.setFw(stringValue);
				callBack.onReadFinished(detailInfo);
			}
		}
		// *********************//
		if (action.equals(Constants.AB_DEVICE_DOES_NOT_SUPPORT_UART)) {// 设备不支持UART时的广播
			callBack.onConnError(Constants.AB_DEVICE_DOES_NOT_SUPPORT_UART);
		}

		if (action.equals(Constants.AB_ACTION_DATA_WRITE_ERROR)) {// 写入错误
			callBack.onConnError(Constants.AB_ACTION_DATA_WRITE_ERROR);
		}

		if (action.equals(Constants.AB_ACTION_GATT_SERVICES_DISCOVERED_ERROR)) {// 查找服务时出现错误
			callBack.onConnError(Constants.AB_ACTION_GATT_SERVICES_DISCOVERED_ERROR);
		}
	}

	private void begainWriteABeaconModel() {
		byte[] model = { 0x08, 0x01,
				(byte) GlobalVariables.beaconWriteInfo.getModel() };
		writeCharacteristic(model, AprilBeaconUUID.ABEACON_SERVICE_UUID,
				AprilBeaconUUID.ABEACON_WRITE_UUID);
	}

	private void begainWriteABeaconURL() {
		String strUrl = GlobalVariables.beaconWriteInfo.getUrl();
		byte first = 0;
		// 将头部转换为一个字节 节省有限的字节长度
		// 如果格式不正确则不进行操作 （如果有个错误的回调会比较合理 暂时不做了）
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
		byte[] url = BLEScanRecordUtil.setWriteABURLValue(strUrl, first);
		writeCharacteristic(url, AprilBeaconUUID.ABEACON_SERVICE_UUID,
				AprilBeaconUUID.ABEACON_WRITE_UUID);
	}

	private void setPassword(String password) {
		byte[] a = password2byte(password);
		writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
				AprilBeaconUUID.ABEACON_WRITE_UUID);
	}

	/**
	 * 写入密码时 字符串密码转换为byte数组
	 * 
	 * @param password
	 *            写入的密码的字符串形式
	 * @return 写入密码的byte数组
	 */
	private byte[] password2byte(String password) {
		byte[] a = new byte[8];
		a[0] = 7 & 0xff;
		a[1] = 6 & 0xff;
		byte[] bytes = password.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			a[i + 2] = bytes[i];
		}
		return a;
	}

	private void begainWriteABeaconInfo() {
		if (GlobalVariables.shouldChangeUid) {
			byte[] a = UUID2bytesUtils
					.uuid2Bytes_ABeacon(GlobalVariables.beaconWriteInfo
							.getUid());
			writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
					AprilBeaconUUID.ABEACON_WRITE_UUID);
		} else {
			setUuid();
		}
	}

	private void setWriteSecretKey() {
		if (GlobalVariables.isWriteSecretKey) {
			byte[] a = UUID2bytesUtils
					.secret_ABeacon(GlobalVariables.beaconWriteInfo
							.getSecretKey());
			writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
					AprilBeaconUUID.ABEACON_WRITE_UUID);
		} else {
			setWritePassCode();
		}
	}

	private void setUuid() {
		if (GlobalVariables.isWriteUUID) {
			byte[] a = UUID2bytesUtils
					.uuid2Bytes_ABeacon(GlobalVariables.beaconWriteInfo
							.getWriteUuid());
			writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
					AprilBeaconUUID.ABEACON_WRITE_UUID);
		} else {
			setWriteMajor();
		}
	}

	private void setUuid2() {
		if (GlobalVariables.isWriteUUID2) {
			byte[] a = UUID2bytesUtils
					.uuid22Bytes_ABeacon(GlobalVariables.beaconWriteInfo
							.getWriteUuid2());
			writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
					AprilBeaconUUID.ABEACON_WRITE_UUID);
		} else {
			setWriteMajor2();
		}
	}

	private void setWriteMajor() {
		if (GlobalVariables.isWriteMajor) {
			writeValue(1, GlobalVariables.beaconWriteInfo.getWriteMajor());
		} else {
			setWriteMinor();
		}
	}

	private void setWriteMajor2() {
		if (GlobalVariables.isWriteMajor2) {
			writeValue(15, GlobalVariables.beaconWriteInfo.getWriteMajor2());
		} else {
			setWriteMinor2();
		}
	}

	private void setWriteMinor() {
		if (GlobalVariables.isWriteMinor) {
			writeValue(2, GlobalVariables.beaconWriteInfo.getWriteMinor());
		} else {
			setWriteAvd();
		}
	}

	private void setWriteMinor2() {
		if (GlobalVariables.isWriteMinor2) {
			writeValue(16, GlobalVariables.beaconWriteInfo.getWriteMinor2());
		} else {
			// setWritePassCode();
			setWriteSecretKey();
		}
	}

	private void setWriteAvd() {
		if (GlobalVariables.isWriteAdvertisingInterval) {
			writeAvd(GlobalVariables.beaconWriteInfo.getWriteAdv());
		} else {
			setWriteMeasuredPower();
		}
	}

	private void setWriteMeasuredPower() {
		if (GlobalVariables.isWriteMeasuredPower) {
			writeValue(5,
					GlobalVariables.beaconWriteInfo.getWriteMeasuredPower());
		} else {
			setWriteTxPower();
		}
	}

	private void setWriteTxPower() {
		if (GlobalVariables.isWriteTxPower) {
			writeValue(6, GlobalVariables.beaconWriteInfo.getWriteTxPower());
		} else {
			setWriteMajor2();
		}
	}

	private void setWritePassCode() {
		if (GlobalVariables.isWritePassword) {
			setPassword(GlobalVariables.beaconWriteInfo.getWritePassword());
		} else {
			callBack.onWriteFinished();
		}
	}

	/**
	 * 向设备写入值
	 * 
	 * @param what
	 *            5和6的话 数据转换格式与其他不同
	 * @param value
	 */
	private void writeValue(int what, int value) {
		if (what == 5 || what == 6) {
			byte[] a = { (byte) (what & 0xff), (byte) (1 & 0xff),
					(byte) (value & 0xff) };
			writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
					AprilBeaconUUID.ABEACON_WRITE_UUID);
		} else {
			byte[] a = { (byte) (what & 0xff), (byte) (2 & 0xff),
					(byte) ((value >> 8) & 0xff), (byte) (value & 0xff) };
			writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
					AprilBeaconUUID.ABEACON_WRITE_UUID);
		}
	}

	/**
	 * 写入广播频率
	 * 
	 * @param i
	 *            需要写入的广播频率值
	 */
	private void writeAvd(int i) {
		int value = (int) i * 160;
		byte[] a = { (byte) (4 & 0xff), (byte) (2 & 0xff),
				(byte) (value & 0xff), (byte) ((value >> 8) & 0xff) };
		writeCharacteristic(a, AprilBeaconUUID.ABEACON_SERVICE_UUID,
				AprilBeaconUUID.ABEACON_WRITE_UUID);
	}

	private void writeCharacteristic(byte[] a, UUID abeaconServiceUuid,
			UUID abeaconWriteUuid) {
		mService.writeRXCharacteristic(abeaconServiceUuid, abeaconWriteUuid, a);
	}

	private void readABeaconInfo(int intExtra, String stringValue) {
		if (intExtra == 0) {// 读取uuid信息返回值
			String uuid = stringValue.substring(6);
			detailInfo.setUuid(uuid);
			readCharactic(1);
		} else if (intExtra == 1) {// 读取major信息返回值
			String hexString = stringValue.substring(stringValue.length() - 4);
			int major = Integer.valueOf(hexString, 16);
			detailInfo.setMajor(major);
			readCharactic(2);
		} else if (intExtra == 2) {// 读取minor信息返回值
			String hexString = stringValue.substring(stringValue.length() - 4);
			int minor = Integer.valueOf(hexString, 16);
			detailInfo.setMinor(minor);
			readCharactic(3);
		} else if (intExtra == 3) {// 读取company id信息返回值
			String hexString = stringValue.substring(stringValue.length() - 4);
			int company_id = Integer.valueOf(hexString, 16);
			readCharactic(4);
		} else if (intExtra == 4) {// 读取广播频率信息返回值
			String hexString = stringValue.substring(stringValue.length() - 4);
			String second = hexString.substring(0, 2);
			String first = hexString.substring(2);
			String newHexString = first + second;
			int advParseBefore = Integer.valueOf(newHexString, 16);
			int adv = (int) (advParseBefore * 0.625 / 100);
			detailInfo.setAdv(adv);
			readCharactic(5);
		} else if (intExtra == 5) {// 读取校准值信息返回值
			String hexString = stringValue.substring(stringValue.length() - 4);
			int measuredPower = Integer.valueOf(hexString, 16) - 256;
			detailInfo.setMeasuredPower(measuredPower);
			readCharactic(6);
		} else if (intExtra == 6) {// 读取发射功率信息返回值
			String hexString = stringValue.substring(stringValue.length() - 4);
			int txPower = Integer.valueOf(hexString, 16);
			detailInfo.setTxPower(txPower);
			readCharactic(8);
		} else if (intExtra == 8) {// 读取广播模式信息返回值
			String hexString = stringValue.substring(stringValue.length() - 4);
			int model = Integer.valueOf(hexString, 16);
			detailInfo.setModel(model);
			if (isSecondUUID) {
				readCharactic(14);
			} else {
				// callBack.onReadFinished(detailInfo);
				readDeviceInfo();
			}
		} else if (intExtra == 14) {
			String uuid = stringValue.substring(6);
			detailInfo.setUuid2(uuid);
			readCharactic(15);
		} else if (intExtra == 15) {
			String hexString = stringValue.substring(stringValue.length() - 4);
			int major2 = Integer.valueOf(hexString, 16);
			detailInfo.setMajor2(major2);
			readCharactic(16);
		} else if (intExtra == 16) {
			String hexString = stringValue.substring(stringValue.length() - 4);
			int minor2 = Integer.valueOf(hexString, 16);
			detailInfo.setMinor2(minor2);
			// readCharactic(16);
			// callBack.onReadFinished(detailInfo);
			readDeviceInfo();
		}
	}

	private void readDeviceInfo() {
		if (isReadBattery) {
			mService.readCharacteristic(AprilBeaconUUID.BATTERY_UUID,
					AprilBeaconUUID.BATTERY_LEVEL);
		} else {
			mService.readCharacteristic(AprilBeaconUUID.DEVICE_INFORMATION,
					AprilBeaconUUID.FW);
		}
	}

	/**
	 * 读取设备信息
	 * 
	 * @param i
	 *            0--uuid 1--major 2--minor 3--company_id 4--广播频率 5--校准值 6--发射功率
	 *            asensor 有第二组uuid 14--uuid2 15--major2 16--minor2
	 */
	private void readCharactic(int i) {
		try {
			Thread.sleep(50);
			byte[] a = { (byte) (i & 0xff) };
			mService.writeRXCharacteristic(
					AprilBeaconUUID.ABEACON_SERVICE_UUID,
					AprilBeaconUUID.ABEACON_READ_UUID, a);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static class MyABeaconCallBack {
		public void onReadFinished(BeaconDetailInfo detailInfo) {
		};

		public void onWritePassCodeSuccess() {

		};

		public void onNotificationOpen() {
		};

		public void onPasswordError() {
		};

		public void onWriteFinished() {
		};

		public void onConnError(String errorMessage) {
		};

		public void onWriteUUIDSuccess() {
		};

		public void onWriteMajorSuccess() {
		};

		public void onWriteMinorSuccess() {
		};

		public void onWriteUUID2Success() {
		};

		public void onWriteMajor2Success() {
		};

		public void onWriteMinor2Success() {
		};

		public void onWriteAdvSuccess() {
		};

		public void onWriteMeasuredPowerSuccess() {
		};

		public void onWriteTxPowerSuccess() {
		};
	}
}
