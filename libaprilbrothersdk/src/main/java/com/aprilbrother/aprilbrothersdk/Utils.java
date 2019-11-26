package com.aprilbrother.aprilbrothersdk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.aprilbrother.aprilbrothersdk.internal.HashCode;
import com.aprilbrother.aprilbrothersdk.internal.Preconditions;
import com.aprilbrother.aprilbrothersdk.utils.BLEScanRecordUtil;

public class Utils {
	private static final String TAG = Utils.class.getSimpleName();
	final private static char[] hexArray = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	@SuppressWarnings("unused")
	private static final int MANUFACTURER_SPECIFIC_DATA = 255;

	public static Beacon beaconFromLeScan(BluetoothDevice device, int rssi,
			byte[] scanRecord) {
		// return ParseBeacon(device, rssi, scanRecord);
		return fromScanData(device, rssi, scanRecord);
	}

	private static Beacon fromScanData(BluetoothDevice device, int rssi,
			byte[] scanData) {

		if (((int) scanData[5] & 0xff) == 0x4c
				&& ((int) scanData[6] & 0xff) == 0x00
				&& ((int) scanData[7] & 0xff) == 0x02
				&& ((int) scanData[8] & 0xff) == 0x15) {
			// yes! This is an iBeacon
		} else {
			// This is not an iBeacon
			// Log.d(TAG,
			// "This is not an iBeacon advertisment.  The bytes I see are: "
			// + bytesToHex(scanData));
			return null;
		}

		int major = (scanData[25] & 0xff) * 0x100 + (scanData[26] & 0xff);
		int minor = (scanData[27] & 0xff) * 0x100 + (scanData[28] & 0xff);
		int measuredPower = (int) scanData[29]; // this one is signed
		int power = (int) scanData[31];

		byte[] proximityUuidBytes = new byte[16];
		System.arraycopy(scanData, 9, proximityUuidBytes, 0, 16);
		String hexString = bytesToHex(proximityUuidBytes);
		StringBuilder sb = new StringBuilder();
		sb.append(hexString.substring(0, 8));
		sb.append("-");
		sb.append(hexString.substring(8, 12));
		sb.append("-");
		sb.append(hexString.substring(12, 16));
		sb.append("-");
		sb.append(hexString.substring(16, 20));
		sb.append("-");
		sb.append(hexString.substring(20, 32));
		String proximityUuid = sb.toString();

		return new Beacon(proximityUuid, device.getName(), device.getAddress(),
				major, minor, measuredPower, rssi, power);

	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	private static Beacon ParseBeacon(BluetoothDevice device, int rssi,
			byte[] scanRecord) {
		String scanRecordAsHex = HashCode.fromBytes(scanRecord).toString();
		for (int i = 0; i < scanRecord.length; i++) {
			int payloadLength = unsignedByteToInt(scanRecord[i]);
			if ((payloadLength == 0) || (i + 1 >= scanRecord.length)) {
				break;
			}
			if (unsignedByteToInt(scanRecord[(i + 1)]) != 255) {
				i += payloadLength;
			} else {
				// 添加payloadLength == 27 支持固件2.1
				if (payloadLength == 26 || payloadLength == 27) {
					if ((unsignedByteToInt(scanRecord[(i + 2)]) == 76)
							&& (unsignedByteToInt(scanRecord[(i + 3)]) == 0)
							&& (unsignedByteToInt(scanRecord[(i + 4)]) == 2)
							&& (unsignedByteToInt(scanRecord[(i + 5)]) == 21)) {

						String proximityUUID = String.format(
								"%s-%s-%s-%s-%s",
								new Object[] {
										scanRecordAsHex.substring(18, 26),
										scanRecordAsHex.substring(26, 30),
										scanRecordAsHex.substring(30, 34),
										scanRecordAsHex.substring(34, 38),
										scanRecordAsHex.substring(38, 50) });

						int major = unsignedByteToInt(scanRecord[(i + 22)])
								* 256 + unsignedByteToInt(scanRecord[(i + 23)]);
						int minor = unsignedByteToInt(scanRecord[(i + 24)])
								* 256 + unsignedByteToInt(scanRecord[(i + 25)]);
						int measuredPower = scanRecord[(i + 26)];

						int power = 0;
						if (payloadLength == 27) {
							power = scanRecord[(i + 27)];
						}
						return new Beacon(proximityUUID, device.getName(),
								device.getAddress(), major, minor,
								measuredPower, rssi, power);
					}
					return null;
				}
				return null;
			}
		}
		return null;
	}

	@SuppressLint("DefaultLocale")
	public static String normalizeProximityUUID(String proximityUUID) {
		String withoutDashes = proximityUUID.replace("-", "").toLowerCase();
		Preconditions.checkArgument(withoutDashes.length() == 32,
				"Proximity UUID must be 32 characters without dashes");

		return String.format(
				"%s-%s-%s-%s-%s",
				new Object[] { withoutDashes.substring(0, 8),
						withoutDashes.substring(8, 12),
						withoutDashes.substring(12, 16),
						withoutDashes.substring(16, 20),
						withoutDashes.substring(20, 32) });
	}

	public static boolean isBeaconInRegion(Beacon beacon, Region region) {
		return ((region.getProximityUUID() == null) || (beacon
				.getProximityUUID().equals(region.getProximityUUID())))
				&& ((region.getMajor() == null) || (beacon.getMajor() == region
						.getMajor().intValue()))
				&& ((region.getMinor() == null) || (beacon.getMinor() == region
						.getMinor().intValue()));
	}

	public static double computeAccuracy(Beacon beacon) {
		if (beacon.getRssi() == 0) {
			return -1.0D;
		}
		if (beacon.getMeasuredPower() == 0) {
			return -1.0D;
		}
		double ratio = beacon.getRssi() / beacon.getMeasuredPower();
		double rssiCorrection = 0.96D + Math.pow(Math.abs(beacon.getRssi()),
				3.0D) % 10.0D / 150.0D;

		if (ratio <= 1.0D) {
			return Math.pow(ratio, 9.98D) * rssiCorrection;
		}
		return (0.103D + 0.89978D * Math.pow(ratio, 7.71D)) * rssiCorrection;
	}

	public static enum Proximity {
		UNKNOWN,

		IMMEDIATE,

		NEAR,

		FAR;

		private Proximity() {
		}
	}

	private static Proximity proximityFromAccuracy(double accuracy) {
		if (accuracy < 0.0D) {
			return Proximity.UNKNOWN;
		}
		if (accuracy < 0.5D) {
			return Proximity.IMMEDIATE;
		}
		if (accuracy <= 3.0D) {
			return Proximity.NEAR;
		}
		return Proximity.FAR;
	}

	@SuppressWarnings("unused")
	private static Proximity computeProximity(Beacon beacon) {
		return proximityFromAccuracy(computeAccuracy(beacon));
	}

	public static int parseInt(String numberAsString) {
		try {
			return Integer.parseInt(numberAsString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int normalize16BitUnsignedInt(int value) {
		return Math.max(1, Math.min(value, 65535));
	}

	@SuppressLint("NewApi")
	public static void restartBluetooth(Context context,
			final RestartCompletedListener listener) {
		BluetoothManager bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter adapter = bluetoothManager.getAdapter();

		IntentFilter intentFilter = new IntentFilter(
				"android.bluetooth.adapter.action.STATE_CHANGED");
		context.registerReceiver(new BroadcastReceiver() {
			private Object val$adapter;

			public void onReceive(Context context, Intent intent) {
				if ("android.bluetooth.adapter.action.STATE_CHANGED"
						.equals(intent.getAction())) {
					int state = intent.getIntExtra(
							"android.bluetooth.adapter.extra.STATE", -1);
					if (state == 10) {
						((BluetoothAdapter) this.val$adapter).enable();
					} else if (state == 12) {
						context.unregisterReceiver(this);
						listener.onRestartCompleted();
					}
				}
			}
		}, intentFilter);

		adapter.disable();
	}

	private static int unsignedByteToInt(byte value) {
		return value & 0xFF;
	}

	public static abstract interface RestartCompletedListener {
		public abstract void onRestartCompleted();
	}

	public static byte[] password2byte(String password) {
		byte[] bytes = password.getBytes();
		byte[] a = new byte[password.length() + 2];
		a[0] = 7 & 0xff;
		a[1] = (byte) (password.length() & 0xff);
		for (int i = 0; i < bytes.length; i++) {
			a[i + 2] = bytes[i];
		}
		return a;
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
