package com.aprilbrother.aprilbrothersdk.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.aprilbrother.aprilbrothersdk.Beacon;

import com.aprilbrother.aprilbrothersdk.bean.BeaconDetailInfo;
import com.aprilbrother.aprilbrothersdk.receiver.ABeaconUARTStatusChangeReceiver.MyABeaconCallBack;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;

import java.util.ArrayList;
import java.util.UUID;

public class AprilBeaconCharacteristics {
	protected static final String TAG = "AprilBeaconCharacteristics";
	private Context context;
	private Beacon beacon;
	private BluetoothGatt mBluetoothGatt;
	private MyReadCallBack myReadCallBack;
	private ArrayList<BluetoothGattCharacteristic> characteristics = new ArrayList<BluetoothGattCharacteristic>();

	private int readWhat = -1;

	private Integer battery;

	private Integer txPower;

	private Integer advinterval;

	private String firmwareRevision;

	private String manufacturerName;

	public static final int READ_BATTERY = 0;
	public static final int READ_TXPOWER = 1;
	public static final int READ_ADVINTERVAL = 2;
	public static final int READ_FW_REVISON = 3;
	public static final int READ_MANUFACTURER = 4;

	public AprilBeaconCharacteristics(Context context, Beacon beacon) {
		this.context = context;
		this.beacon = beacon;
	}

	/**
	 * 获取电池电量
	 * 
	 * @return 电池电量 1-100
	 */
	public Integer getBattery() {
		return this.battery;
	}

	/**
	 * 获取发射功率
	 * 
	 * @return 发射功率 0是0dbm 1是4dbm 2是-6dbm 3是-23dbm
	 */
	public Integer getTxPower() {
		return this.txPower;
	}

	/**
	 * 获取广播频率
	 * 
	 * @return 广播频率 1-100 单位100ms
	 */
	public Integer getAdvinterval() {
		return this.advinterval;
	}

	/**
	 * 获取固件版本
	 * 
	 * @return 固件的版本号
	 */
	public String getFWRevision() {
		return this.firmwareRevision;
	}

	/**
	 * 获取制造商的信息
	 * 
	 * @return 制造商名
	 */
	public String getManufacturer() {
		return this.manufacturerName;
	}

	/**
	 * 连接设备读取信息
	 * 
	 * @param myReadCallBack
	 *            读取的回调
	 * @param readWhat
	 *            需要读的值
	 */
	public void connectGattToRead(MyReadCallBack myReadCallBack, int readWhat) {
		begainConnectGattToRead(myReadCallBack, readWhat);
	}

	private void begainConnectGattToRead(final MyReadCallBack myReadCallBack,
			final int readWhat) {
		this.readWhat = readWhat;
		this.myReadCallBack = myReadCallBack;
		BluetoothDevice device = deviceFromBeacon(this.beacon);
		if (device.getName() != null && !device.getName().contains("abeacon")) {
			this.mBluetoothGatt = device.connectGatt(this.context, false,
					this.mGattCallback);
		} else {
			boolean isReadBattery = (readWhat == READ_BATTERY);
			BeaconConnection.connABeacon(context, device.getAddress(), true,
					isReadBattery, new MyABeaconCallBack() {

						@Override
						public void onReadFinished(BeaconDetailInfo detailInfo) {
							switch (readWhat) {
							case READ_TXPOWER:
								txPower = detailInfo.getTxPower();
								myReadCallBack.readyToGetTxPower();
								break;
							case READ_ADVINTERVAL:
								advinterval = detailInfo.getAdv();
								myReadCallBack.readyToGetAdvinterval();
								break;
							case READ_BATTERY:
								battery = detailInfo.getBattery();
								myReadCallBack.readyToGetBattery();
								break;
							default:
								break;
							}
							super.onReadFinished(detailInfo);
						}
					});
		}

	}

	private BluetoothDevice deviceFromBeacon(Beacon beacon) {
		BluetoothManager bluetoothManager = (BluetoothManager) this.context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
		return bluetoothAdapter.getRemoteDevice(beacon.getMacAddress());
	}

	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			super.onConnectionStateChange(gatt, status, newState);

			AprilL.d("Bluetooth status: " + status);
			if (newState == 2) {
				// String a = "Connected to GATT server.";
				// AprilL.i(a);
				// AprilL.i("Attempting to start service discovery:"
				// + gatt.discoverServices());
				gatt.discoverServices();
			} else if (newState == 0) {
				String a = "Disconnected from GATT server.";
				AprilL.i(a);
			}
		}

		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			super.onServicesDiscovered(gatt, status);
			begainOnServicesDiscovered(gatt);
		}

		private void begainOnServicesDiscovered(BluetoothGatt gatt) {
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_BATTERY) {
				startReadCharacteristics(gatt,
						AprilBeaconUUID.BEACON_BATTERY_SERVICE,
						AprilBeaconUUID.BEACON_BATTERY_LEVEL);
			}
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_TXPOWER) {
				startReadCharacteristics(gatt,
						AprilBeaconUUID.BEACON_SERVICE_UUID,
						AprilBeaconUUID.BEACON_TXPOWER_UUID);
			}
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_ADVINTERVAL) {
				startReadCharacteristics(gatt,
						AprilBeaconUUID.BEACON_SERVICE_UUID,
						AprilBeaconUUID.BEACON_ADVINTERVAL_UUID);
			}
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_FW_REVISON) {
				startReadCharacteristics(gatt,
						AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.BEACON_FIRMWARE_REVISION);
			}
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_MANUFACTURER) {
				startReadCharacteristics(gatt,
						AprilBeaconUUID.DEVICE_INFORMATION,
						AprilBeaconUUID.BEACON_MANUFACTURER_NAME);
			}
		}

		private void startReadCharacteristics(BluetoothGatt gatt,
				UUID servieUUID, UUID characteristicUUID) {
			for (BluetoothGattService service : gatt.getServices()) {
				if (service.getUuid().equals(servieUUID)) {// AprilBeaconUUID.BEACON_BATTERY_SERVICE
					BluetoothGattCharacteristic characteristic = service
							.getCharacteristic(characteristicUUID);// AprilBeaconUUID.BEACON_BATTERY_LEVEL
					if (characteristic != null) {
						gatt.readCharacteristic(characteristic);
					}
				}
			}
		}

		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
		}

		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicRead(gatt, characteristic, status);
			begainOnRead(characteristic);
		}

		private void begainOnRead(BluetoothGattCharacteristic characteristic) {
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_BATTERY) {
				try {
					Integer batteryLevel = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 0);
					AprilBeaconCharacteristics.this.battery = batteryLevel;
					AprilBeaconCharacteristics.this.myReadCallBack
							.readyToGetBattery();
					AprilL.i("batteryLevel = " + batteryLevel);
				} catch (Exception e) {
					AprilL.e("read Battery have something wrong");
					e.printStackTrace();
				}
			}
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_TXPOWER) {
				try {
					Integer txPower = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 0);
					AprilBeaconCharacteristics.this.txPower = txPower;
					AprilBeaconCharacteristics.this.myReadCallBack
							.readyToGetTxPower();
					AprilL.i("txPower = "
							+ AprilBeaconCharacteristics.this.txPower);
				} catch (Exception e) {
					AprilL.e("read txPower have something wrong");
					e.printStackTrace();
				}
			}
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_ADVINTERVAL) {
				try {
					Integer advinterval = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 0);
					AprilBeaconCharacteristics.this.advinterval = advinterval;
					AprilBeaconCharacteristics.this.myReadCallBack
							.readyToGetAdvinterval();
					AprilL.i("mAdvinterval = "
							+ AprilBeaconCharacteristics.this.advinterval);
				} catch (Exception e) {
					AprilL.e("read Advinterval have something wrong");
					e.printStackTrace();
				}
			}
			if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_FW_REVISON) {
				try {
					String firmwareRevision = characteristic.getStringValue(0);
					AprilBeaconCharacteristics.this.firmwareRevision = firmwareRevision;
					AprilBeaconCharacteristics.this.myReadCallBack
							.readyToGetFWRevision();
					AprilL.i("firmwareRevision = "
							+ AprilBeaconCharacteristics.this.firmwareRevision);
				} catch (Exception e) {
					AprilL.e("read firmwareRevision have something wrong");
					e.printStackTrace();
				}
			}
			try {
				if (AprilBeaconCharacteristics.this.readWhat == AprilBeaconCharacteristics.READ_MANUFACTURER) {
					String manufacturerName = characteristic.getStringValue(0);
					AprilBeaconCharacteristics.this.manufacturerName = manufacturerName;
					AprilBeaconCharacteristics.this.myReadCallBack
							.readyToGetManufacturer();
					AprilL.i("manufacturerName = "
							+ AprilBeaconCharacteristics.this.manufacturerName);
				}
			} catch (Exception e) {
				AprilL.e("read manufacturerName have something wrong");
				e.printStackTrace();
			}
			AprilBeaconCharacteristics.this.characteristics.add(characteristic);
		}
	};

	public void close() {
		myClose();
	}

	private void myClose() {
		if (this.mBluetoothGatt != null) {
			this.mBluetoothGatt.disconnect();
			this.mBluetoothGatt.close();
			this.mBluetoothGatt = null;
		}
		BeaconConnection.close();
	}

	public static class MyReadCallBack implements
			ReadCallback {
		public void readyToGetBattery() {
		}

		public void readyToGetAdvinterval() {
		}

		public void readyToGetTxPower() {
		}

		public void readyToGetFWRevision() {
		}

		public void readyToGetManufacturer() {
		}
	}

	/**
	 * 读取设备信息的回调
	 * 
	 */
	public static abstract interface ReadCallback {
		/**
		 * 获取电池电量
		 */
		public abstract void readyToGetBattery();

		/**
		 * 获取发射功率
		 */
		public abstract void readyToGetTxPower();

		/**
		 * 获取广播频率
		 */
		public abstract void readyToGetAdvinterval();

		/**
		 * 获取固件版本
		 */
		public abstract void readyToGetFWRevision();

		/**
		 * 获取制造商信息
		 */
		public abstract void readyToGetManufacturer();
	}
}
