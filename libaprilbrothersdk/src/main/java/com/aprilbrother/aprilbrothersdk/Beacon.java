package com.aprilbrother.aprilbrothersdk;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.aprilbrother.aprilbrothersdk.internal.Objects;

public class Beacon implements Parcelable {

	/**
	 * < 0.5m immediate
	 */
	public static final int PROXIMITY_IMMEDIATE = 1;
	/**
	 * 0.5~3m near
	 */
	public static final int PROXIMITY_NEAR = 2;
	/**
	 * >3m far
	 */
	public static final int PROXIMITY_FAR = 3;
	/**
	 * No distance Unknown
	 */
	public static final int PROXIMITY_UNKNOWN = 0;

	public static final String MODEL_IBEACON = "iBeacon";
	public static final String MODEL_URL = "url";
	public static final String MODEL_UID = "uid";

	protected static double calculateAccuracy(int measuredPower, double rssi) {
		if (rssi == 0) {
			return -1.0;
		}
		if (measuredPower == 0) {
			return -1.0;
		}
		if (rssi > 0) {
			rssi = rssi - 128;
		}
		double ratio = rssi * 1.0 / measuredPower;
		if (ratio < 1.0) {
			return Math.pow(ratio, 10);
		} else {
			 double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
//			double accuracy = (0.42093) * Math.pow(ratio, 6.9476) + 0.54992;
			return accuracy;
		}
	}

	protected static int calculateProximity(double accuracy) {
		if (accuracy < 0) {
			return Beacon.PROXIMITY_UNKNOWN;
		}
		if (accuracy >= 0 && accuracy < 0.5) {
			return Beacon.PROXIMITY_IMMEDIATE;
		}
		if (accuracy >= 0.5 && accuracy < 3.0) {
			return Beacon.PROXIMITY_NEAR;
		}
		return Beacon.PROXIMITY_FAR;

	}

	/**
	 * @see #PROXIMITY_IMMEDIATE
	 * @see #PROXIMITY_NEAR
	 * @see #PROXIMITY_FAR
	 * @see #PROXIMITY_UNKNOWN
	 */
	protected Integer proximity;

	private final String proximityUUID;
	private final String name;
	private final String macAddress;
	private final int major;
	private final int minor;

	private final int measuredPower;

	private final int rssi;

	protected Double accuracy;

//	protected Double runningAverageRssi = null;

	public static final Creator<Beacon> CREATOR = new Creator<Beacon>() {
		public Beacon createFromParcel(Parcel source) {
			return new Beacon(source);
		}

		public Beacon[] newArray(int size) {
			return new Beacon[size];
		}
	};

	private Beacon(Parcel parcel) {
		this.proximityUUID = parcel.readString();
		this.name = parcel.readString();
		this.macAddress = parcel.readString();
		this.major = parcel.readInt();
		this.minor = parcel.readInt();
		this.measuredPower = parcel.readInt();
		this.rssi = parcel.readInt();
		this.power = parcel.readInt();
	}

	public Beacon(String proximityUUID, String name, String macAddress,
			int major, int minor, int measuredPower, int rssi) {
		this.proximityUUID = Utils.normalizeProximityUUID(proximityUUID);
		if (TextUtils.isEmpty(name)) {
			this.name = "Unknown";
		} else {
			this.name = name;
		}
		this.macAddress = macAddress;
		this.major = major;
		this.minor = minor;
		this.measuredPower = measuredPower;
		this.rssi = rssi;
	}

	public Beacon(String proximityUUID, String name, String macAddress,
			int major, int minor, int measuredPower, int rssi, int power) {
		this.proximityUUID = Utils.normalizeProximityUUID(proximityUUID);
		if (TextUtils.isEmpty(name)) {
			this.name = "Unknown";
		} else {
			this.name = name;
		}
		this.macAddress = macAddress;
		this.major = major;
		this.minor = minor;
		this.measuredPower = measuredPower;
		this.rssi = rssi;
		this.power = power;
	}

	public int describeContents() {
		return 0;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		Beacon beacon = (Beacon) o;
		return this.macAddress.equals(beacon.macAddress);
	}

	/**
	 * 获取beacon的距离
	 * 
	 * @return beacon的距离
	 */
	public double getDistance() {
		if (accuracy == null) {
			// accuracy = calculateAccuracy(measuredPower,
			// runningAverageRssi != null ? runningAverageRssi : rssi);
			if (rssi > 0) {
				accuracy = calculateAccuracy(measuredPower, rssi - 128);
			} else {
				accuracy = calculateAccuracy(measuredPower, rssi);
			}
		}
		// 此格式转换可以保持俄语环境下.不被转成，
		BigDecimal bg = new BigDecimal(accuracy);
		double distanceFormatted = bg.setScale(2, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
		return distanceFormatted;
	}

	/**
	 * 获取beacon的mac地址
	 * 
	 * @return beacon的mac地址
	 */
	public String getMacAddress() {
		return this.macAddress;
	}

	/**
	 * 获取beacon的Major值
	 * 
	 * @return beacon的Major值
	 */
	public int getMajor() {
		return this.major;
	}

	/**
	 * 获取beacon的measurePower
	 * 
	 * @return beacon的measurePower
	 */
	public int getMeasuredPower() {
		return this.measuredPower;
	}

	/**
	 * 获取beacon的minor值
	 * 
	 * @return beacon的minor值
	 */
	public int getMinor() {
		return this.minor;
	}

	/**
	 * 获取beacon名称
	 * 
	 * @return beacon名称
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 获取beacon的远近情况
	 * 
	 * @return
	 */
	public int getProximity() {
		if (proximity == null) {
			proximity = calculateProximity(getDistance());
		}
		return proximity;
	}

	/**
	 * 获取beacon的ProximityUUID
	 * 
	 * @return beacon的ProximityUUID
	 */
	public String getProximityUUID() {
		return this.proximityUUID;
	}

	/**
	 * 获取beacon的RSSI
	 * 
	 * @return beacon的RSSI
	 */
	public int getRssi() {
		return this.rssi;
	}

	int power;

	/**
	 * 获取beacon的电量（固件版本2.1可以使用）
	 * 
	 * @return beacon的电量
	 */
	public int getPower() {
		return this.power;
	}

	public int hashCode() {
		int result = this.proximityUUID.hashCode();
		result = 31 * result + this.major;
		result = 31 * result + this.minor;
		return result;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("macAddress", this.macAddress)
				.add("proximityUUID", this.proximityUUID)
				.add("major", this.major).add("minor", this.minor)
				.add("measuredPower", this.measuredPower)
				.add("rssi", this.rssi).toString();
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.proximityUUID);
		dest.writeString(this.name);
		dest.writeString(this.macAddress);
		dest.writeInt(this.major);
		dest.writeInt(this.minor);
		dest.writeInt(this.measuredPower);
		dest.writeInt(this.rssi);
	}
}
