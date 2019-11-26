package com.aprilbrother.aprilbrothersdk;

import java.io.Serializable;

public class EddyStone implements Serializable {

	private static final long serialVersionUID = -2331149579655215520L;

	public static final String MODEL_URL = "url";
	public static final String MODEL_UID = "uid";
	public static final String MODEL_IBEACON = "iBeacon";
	public static final String MODEL_IBEACON_UID_URL = "iBeacon_uid_url";

	private String model;
	private String url;
	private String uid;

	private String uid_namespace;
	private String uid_custom;

	private String name;
	private String macAddress;
	private int rssi;

	private String uuid;
	private int major;
	private int minor;

	public EddyStone(String model, String data, String name, String macAddress,
			int rssi) {
		this.model = model;
		if (model.equals(MODEL_URL)) {
			this.url = data;
		} else if (model.equals(MODEL_UID)) {
			this.uid = data;
		} else if (model.equals(MODEL_IBEACON)) {
			String[] strings = data.split(",");
			if (strings != null && strings.length == 3) {
				uuid = strings[0];
				major = Integer.valueOf(strings[1]);
				minor = Integer.valueOf(strings[2]);
			}
		}
		this.name = name;
		this.macAddress = macAddress;
		this.rssi = rssi;
	}

	public String getModel() {
		return model;
	}

	public String getUrl() {
		return url;
	}

	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public int getRssi() {
		return rssi;
	}

	public String getUuid() {
		return uuid;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EddyStone))
			return false;
		final EddyStone eddystone = (EddyStone) other;
		if (!getMacAddress().equals(eddystone.getMacAddress()))
			return false;
		return true;
	}
}
