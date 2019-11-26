package com.aprilbrother.aprilbrothersdk.bean;

import java.io.Serializable;

public class BeaconDetailInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3354789544719204219L;

	private String uuid;
	private int major;
	private int minor;
	private int txPower;
	private int measuredPower;
	private int adv;
	private int battery;
	private String systemId;
	private String modelNumber;
	private String serialNumber;
	private String fw;
	private String hw;
	private String sw;
	private String manufacturer;
	private String ieee;
	private String pnpId;
	private boolean haveBatteryService;
	private String uuid2;
	private int major2;
	private int minor2;
	private int model;//(0 ibeacon) (1 uid)(2 url)(3 sensor)
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public int getTxPower() {
		return txPower;
	}
	public void setTxPower(int txPower) {
		this.txPower = txPower;
	}
	public int getMeasuredPower() {
		return measuredPower;
	}
	public void setMeasuredPower(int measuredPower) {
		this.measuredPower = measuredPower;
	}
	public int getAdv() {
		return adv;
	}
	public void setAdv(int adv) {
		this.adv = adv;
	}
	public int getBattery() {
		return battery;
	}
	public void setBattery(int battery) {
		this.battery = battery;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getModelNumber() {
		return modelNumber;
	}
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getFw() {
		return fw;
	}
	public void setFw(String fw) {
		this.fw = fw;
	}
	public String getHw() {
		return hw;
	}
	public void setHw(String hw) {
		this.hw = hw;
	}
	public String getSw() {
		return sw;
	}
	public void setSw(String sw) {
		this.sw = sw;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getIeee() {
		return ieee;
	}
	public void setIeee(String ieee) {
		this.ieee = ieee;
	}
	public String getPnpId() {
		return pnpId;
	}
	public void setPnpId(String pnpId) {
		this.pnpId = pnpId;
	}
	public boolean isHaveBatteryService() {
		return haveBatteryService;
	}
	public void setHaveBatteryService(boolean haveBatteryService) {
		this.haveBatteryService = haveBatteryService;
	}
	public String getUuid2() {
		return uuid2;
	}
	public void setUuid2(String uuid2) {
		this.uuid2 = uuid2;
	}
	public int getMajor2() {
		return major2;
	}
	public void setMajor2(int major2) {
		this.major2 = major2;
	}
	public int getMinor2() {
		return minor2;
	}
	public void setMinor2(int minor2) {
		this.minor2 = minor2;
	}
	
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	
}
