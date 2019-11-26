package com.aprilbrother.aprilbrothersdk.utils;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

import com.aprilbrother.aprilbrothersdk.EddyStone;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.URLUtil;

public class BLEScanRecordUtil {

	private static final String TAG = BLEScanRecordUtil.class.getSimpleName();
	private static final char[] HEX = "0123456789ABCDEF".toCharArray();
	private static final SparseArray<String> URI_SCHEMES = new SparseArray<String>() {
		{
			put((byte) 0, "http://www.");
			put((byte) 1, "https://www.");
			put((byte) 2, "http://");
			put((byte) 3, "https://");
			put((byte) 4, "urn:uuid:");
		}
	};

	private static final SparseArray<String> URL_CODES = new SparseArray<String>() {
		{
			put((byte) 0, ".com/");
			put((byte) 1, ".org/");
			put((byte) 2, ".edu/");
			put((byte) 3, ".net/");
			put((byte) 4, ".info/");
			put((byte) 5, ".biz/");
			put((byte) 6, ".gov/");
			put((byte) 7, ".com");
			put((byte) 8, ".org");
			put((byte) 9, ".edu");
			put((byte) 10, ".net");
			put((byte) 11, ".info");
			put((byte) 12, ".biz");
			put((byte) 13, ".gov");
		}
	};

	public static String toHexString(byte[] bytes) {
		char[] chars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			int c = bytes[i] & 0xFF;
			chars[i * 2] = HEX[c >>> 4];
			chars[i * 2 + 1] = HEX[c & 0x0F];
		}
		return new String(chars).toLowerCase();
	}

	public static boolean isZeroed(byte[] bytes) {
		for (byte b : bytes) {
			if (b != 0x00) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断 eddystone uribeacon 的广播模式
	 * 
	 * @param scanRecord
	 * @return
	 */
	public static String getType(byte[] scanRecord) {
		if (((int) scanRecord[5] & 0xff) == 0x4c
				&& ((int) scanRecord[6] & 0xff) == 0x00
				&& ((int) scanRecord[7] & 0xff) == 0x02
				&& ((int) scanRecord[8] & 0xff) == 0x15) {
			return EddyStone.MODEL_IBEACON;
		} else if (((int) scanRecord[9] & 0xff) == 0xaa
				&& ((int) scanRecord[10] & 0xff) == 0xfe
				&& ((int) scanRecord[11] & 0xff) == 0x10) {
			return EddyStone.MODEL_URL;
		} else if (((int) scanRecord[9] & 0xff) == 0xaa
				&& ((int) scanRecord[10] & 0xff) == 0xfe
				&& ((int) scanRecord[11] & 0xff) == 0x00) {
			return EddyStone.MODEL_UID;
		}
		// else if (((int) scanRecord[9] & 0xff) == 0xaa
		// && ((int) scanRecord[10] & 0xff) == 0xfe
		// && ((int) scanRecord[11] & 0xff) == 0x20) {
		// return "uid";
		// }
		return "";
	}

	/**
	 * 符合ibeacon协议的话 取出广播中的UUID
	 * 
	 * @param scanRecord
	 * @return
	 */
	public static String getBeaconUUID(byte[] scanRecord) {
		byte[] proximityUuidBytes = new byte[16];
		System.arraycopy(scanRecord, 9, proximityUuidBytes, 0, 16);
		String hexString = toHexString(proximityUuidBytes);
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
		return sb.toString();
	}

	public static String getBeaconUUIDMajorMinor(byte[] scanRecord) {
		byte[] proximityUuidBytes = new byte[16];
		System.arraycopy(scanRecord, 9, proximityUuidBytes, 0, 16);
		String hexString = toHexString(proximityUuidBytes);
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
		int major = (scanRecord[25] & 0xff) * 0x100 + (scanRecord[26] & 0xff);
		int minor = (scanRecord[27] & 0xff) * 0x100 + (scanRecord[28] & 0xff);
		String uuidMajorMinor = sb.toString() + "," + major + "," + minor;
		return uuidMajorMinor;
	}

	/**
	 * uid模式下 取出uid
	 * 
	 * @param scanRecord
	 * @return
	 */
	public static String getUID(byte[] scanRecord) {
		byte[] proximityUuidBytes = new byte[16];
		System.arraycopy(scanRecord, 13, proximityUuidBytes, 0, 16);
		String hexString = toHexString(proximityUuidBytes);
		StringBuilder sb = new StringBuilder();
		sb.append(hexString.substring(0, 8));
		// sb.append("-");
		sb.append(hexString.substring(8, 12));
		// sb.append("-");
		sb.append(hexString.substring(12, 16));
		// sb.append("-");
		sb.append(hexString.substring(16, 20));
		// sb.append("-");
		sb.append(hexString.substring(20, 32));
		return sb.toString();
	}

	public static String decodeUrl(byte[] scanRecord) {
		StringBuilder url = new StringBuilder();
		int urllength = scanRecord[7];
		if (urllength >= 5) {
			byte[] serviceData = new byte[urllength - 3];
			System.arraycopy(scanRecord, 11, serviceData, 0, urllength - 3);
			int offset = 2;
			byte b = serviceData[offset++];
			String scheme = URI_SCHEMES.get(b);
			if (!TextUtils.isEmpty(scheme)) {
				url.append(scheme);
				if (URLUtil.isNetworkUrl(scheme)) {
					return decodeUrl(serviceData, offset, url);
				} else if ("urn:uuid:".equals(scheme)) {
					return decodeUrnUuid(serviceData, offset, url);
				}
			} else {
				return decodeUrl(serviceData, offset, url);
			}
		}
		return url.toString();
	}

	private static String decodeUrl(byte[] serviceData, int offset,
			StringBuilder urlBuilder) {
		while (offset < serviceData.length) {
			byte b = serviceData[offset++];
			String code = URL_CODES.get(b);
			if (code != null) {
				urlBuilder.append(code);
				// return urlBuilder.toString();
			} else {
				urlBuilder.append((char) b);
			}
		}
		return urlBuilder.toString();
	}

	public static String decodeUrnUuid(byte[] serviceData, int offset,
			StringBuilder urnBuilder) {
		ByteBuffer bb = ByteBuffer.wrap(serviceData);
		// UUIDs are ordered as byte array, which means most significant first
		bb.order(ByteOrder.BIG_ENDIAN);
		long mostSignificantBytes, leastSignificantBytes;
		try {
			bb.position(offset);
			mostSignificantBytes = bb.getLong();
			leastSignificantBytes = bb.getLong();
		} catch (BufferUnderflowException e) {
			Log.w(TAG, "decodeUrnUuid BufferUnderflowException!");
			return null;
		}
		UUID uuid = new UUID(mostSignificantBytes, leastSignificantBytes);
		urnBuilder.append(uuid.toString());
		return urnBuilder.toString();
	}

	/**
	 * 读取BLE设备的UUID
	 * 
	 * @param scanRecord
	 * @return
	 */
	public static String serviceFromScanRecord(byte[] scanRecord) {
		final int serviceOffset = 9;
		final int serviceLimit = 16;
		try {
			byte[] service = Arrays.copyOfRange(scanRecord, serviceOffset,
					serviceOffset + serviceLimit);

			StringBuilder builder = new StringBuilder();
			for (byte b : service) {
				builder.append(String.format("%02x ", b));
			}
			return builder.toString();

		} catch (Exception e) {
			return null;
		}
	}

	public static String uid2uuid(String uid) {
		StringBuilder sb = new StringBuilder();
		sb.append(uid.substring(0, 8));
		sb.append("-");
		sb.append(uid.substring(8, 12));
		sb.append("-");
		sb.append(uid.substring(12, 16));
		sb.append("-");
		sb.append(uid.substring(16, 20));
		sb.append("-");
		sb.append(uid.substring(20, 32));
		return sb.toString();
	}

	public static byte[] setWriteURLValue(String url_value, byte first,
			byte last) {
		byte[] write_value = new byte[5 + url_value.length()];
		byte[] head = { 0x41, 0x54, 0x55, first };
		for (int i = 0; i < head.length; i++) {
			write_value[i] = head[i];
		}
		byte[] value = url_value.getBytes();
		for (int i = 0; i < url_value.length(); i++) {
			write_value[i + head.length] = value[i];
		}
		write_value[head.length + value.length] = last;
		return write_value;
	}

	public static byte[] setWriteURLValue(String url_value, byte first) {
		// 如果内容中包含URL_CODES 替换为指定的字节
		byte replaceByte = 0;
		for (int i = 0; i < URL_CODES.size(); i++) {
			String value = URL_CODES.valueAt(i);
			if (url_value.contains(value)) {
				url_value = url_value.replaceAll(value, "*");
				replaceByte = (byte) i;
			}
		}
		if (url_value.length() > 16) {
			return null;
		}
		byte[] write_value = new byte[4 + url_value.length()];
		// ATU+url
		byte[] head = { 0x41, 0x54, 0x55, first };
		for (int i = 0; i < head.length; i++) {
			write_value[i] = head[i];
		}
		byte[] value = url_value.getBytes();
		for (int i = 0; i < url_value.length(); i++) {
			if (value[i] == 42) {
				write_value[i + head.length] = replaceByte;
			} else {
				write_value[i + head.length] = value[i];
			}
		}
		return write_value;
	}

	public static byte[] setWriteABURLValue(String url_value, byte first) {
		byte replaceByte = 0;
		for (int i = 0; i < URL_CODES.size(); i++) {
			String value = URL_CODES.valueAt(i);
			if (url_value.contains(value)) {
				url_value = url_value.replaceAll(value, "*");
				replaceByte = (byte) i;
			}
		}
		if (url_value.length() > 16) {
			return null;
		}
		byte[] write_value = new byte[3 + url_value.length()];
		// 0a + length + url
		byte[] head = { 0x0a, (byte) (url_value.length() + 1), first };
		for (int i = 0; i < head.length; i++) {
			write_value[i] = head[i];
		}
		byte[] value = url_value.getBytes();
		for (int i = 0; i < url_value.length(); i++) {
			if (value[i] == 42) {
				write_value[i + head.length] = replaceByte;
			} else {
				write_value[i + head.length] = value[i];
			}
		}
		return write_value;
	}
}
