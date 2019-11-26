package com.aprilbrother.aprilbrothersdk.utils;

import java.util.UUID;

/** {@hide} */
public class UUID2bytesUtils {

	private final static char[] HEX = "0123456789abcdef".toCharArray();

	public static void main(String[] args) {
		String uuid = "2c4dbc35-7985-42b1-a391-019e3acfea05";
		byte[] bys = uuid2Bytes(uuid);
		// 验证
		System.out.println(hex(bys).equals(uuid.replace("-", "")));
	}

	public static byte[] uuid2Bytes(String uuid) {
		if (uuid == null) {
			throw new NullPointerException("uuid is null");
		}
		UUID uid = UUID.fromString(uuid);
		long lsb = uid.getLeastSignificantBits();
		long msg = uid.getMostSignificantBits();
		byte[] bys = new byte[16];
		for (int i = 0, j = 8; i < 8; i++, j++) {
			bys[i] = (byte) ((msg >>> ((7 - i) << 3)) & 0xff);
			bys[j] = (byte) ((lsb >>> ((7 - i) << 3)) & 0xff);
		}
		return bys;
	}

	public static String hex(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		if (bytes.length == 0) {
			return "";
		}
		char[] chs = new char[bytes.length << 1];
		for (int i = 0, k = 0; i < bytes.length; i++) {
			chs[k++] = HEX[(bytes[i] & 0xf0) >> 4];
			chs[k++] = HEX[(bytes[i] & 0xf)];
		}
		return new String(chs);
	}

	/**
	 * 写入ABeacon时 转换uuid的为所需字节数组
	 * 
	 * @param uuid
	 *            uuid格式为 8-4-4-4-12
	 * @return 格式为uuid十六进制表现形式前加0010 如0010aaaaaaaabbbbccccddddeeeeeeeeeeee
	 */
	public static byte[] uuid2Bytes_ABeacon(String uuid) {
		if (uuid == null) {
			throw new NullPointerException("uuid is null");
		}
		UUID uid = UUID.fromString(uuid);
		long lsb = uid.getLeastSignificantBits();
		long msg = uid.getMostSignificantBits();
		byte[] bys = new byte[16];
		for (int i = 0, j = 8; i < 8; i++, j++) {
			bys[i] = (byte) ((msg >>> ((7 - i) << 3)) & 0xff);
			bys[j] = (byte) ((lsb >>> ((7 - i) << 3)) & 0xff);
		}
		byte[] newBytes = new byte[18];
		newBytes[0] = 0 & 0xff;
		newBytes[1] = 16 & 0xff;
		for (int i = 0; i < bys.length; i++) {
			newBytes[i + 2] = bys[i];
		}
		return newBytes;
	}

	/**
	 * 写入ABeacon时 转换uuid2的为所需字节数组
	 * 
	 * @param uuid
	 *            uuid2格式为 8-4-4-4-12
	 * @return 格式为uuid2十六进制表现形式前加0d10 如0d10aaaaaaaabbbbccccddddeeeeeeeeeeee
	 */
	public static byte[] uuid22Bytes_ABeacon(String uuid) {
		if (uuid == null) {
			throw new NullPointerException("uuid is null");
		}
		UUID uid = UUID.fromString(uuid);
		long lsb = uid.getLeastSignificantBits();
		long msg = uid.getMostSignificantBits();
		byte[] bys = new byte[16];
		for (int i = 0, j = 8; i < 8; i++, j++) {
			bys[i] = (byte) ((msg >>> ((7 - i) << 3)) & 0xff);
			bys[j] = (byte) ((lsb >>> ((7 - i) << 3)) & 0xff);
		}
		byte[] newBytes = new byte[18];
		newBytes[0] = 14 & 0xff;
		newBytes[1] = 16 & 0xff;
		for (int i = 0; i < bys.length; i++) {
			newBytes[i + 2] = bys[i];
		}
		return newBytes;
	}

	public static byte[] secret_ABeacon(String secret) {
		if (secret == null) {
			throw new NullPointerException("secret is null");
		} else {
			// byte[] secretBytes = secret.getBytes();
			byte[] secretBytes = hexStringToByteArray(secret);

			byte[] newBytes = new byte[18];
			newBytes[0] = 17;
			newBytes[1] = 16;

			for (int i = 0; i < secretBytes.length; ++i) {
				newBytes[i + 2] = secretBytes[i];
			}

			return newBytes;
		}
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] b = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
			b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return b;
	}
}
