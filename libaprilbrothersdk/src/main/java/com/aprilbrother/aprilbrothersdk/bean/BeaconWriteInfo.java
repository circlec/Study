package com.aprilbrother.aprilbrothersdk.bean;

import java.io.Serializable;

public class BeaconWriteInfo implements Serializable {

	private static final long serialVersionUID = 2375020373299525821L;

	public BeaconWriteInfo() {
	}

	private String writeUuid;
	private int writeMajor;
	private int writeMinor;
	private int writeMeasuredPower;
	private int writeTxPower;
	private int writeAdv;
	private String writePassword;
	private int model;
	private String url;
	private String uid;

	//给proxiot添加修改uid
	private String uid_namespace;
	private String uid_custom;

	private String writeUuid2;
	private int writeMajor2;
	private int writeMinor2;
	
    private String secretKey;
	
	public String getWriteUuid() {
		return writeUuid;
	}

	public void setWriteUuid(String writeUuid) {
		this.writeUuid = writeUuid;
	}

	public int getWriteMajor() {
		return writeMajor;
	}

	public void setWriteMajor(int writeMajor) {
		this.writeMajor = writeMajor;
	}

	public int getWriteMinor() {
		return writeMinor;
	}

	public void setWriteMinor(int writeMinor) {
		this.writeMinor = writeMinor;
	}

	public int getWriteMeasuredPower() {
		return writeMeasuredPower;
	}

	public void setWriteMeasuredPower(int writeMeasuredPower) {
		this.writeMeasuredPower = writeMeasuredPower;
	}

	public int getWriteTxPower() {
		return writeTxPower;
	}

	public void setWriteTxPower(int writeTxPower) {
		this.writeTxPower = writeTxPower;
	}

	public int getWriteAdv() {
		return writeAdv;
	}

	public void setWriteAdv(int writeAdv) {
		this.writeAdv = writeAdv;
	}

	public String getWritePassword() {
		return writePassword;
	}

	public void setWritePassword(String writePassword) {
		this.writePassword = writePassword;
	}

	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUid_namespace() {
		return uid_namespace;
	}

	public void setUid_namespace(String uid_namespace) {
		this.uid_namespace = uid_namespace;
	}

	public String getUid_custom() {
		return uid_custom;
	}

	public void setUid_custom(String uid_custom) {
		this.uid_custom = uid_custom;
	}

	public String getWriteUuid2() {
		return writeUuid2;
	}

	public void setWriteUuid2(String writeUuid2) {
		this.writeUuid2 = writeUuid2;
	}

	public int getWriteMajor2() {
		return writeMajor2;
	}

	public void setWriteMajor2(int writeMajor2) {
		this.writeMajor2 = writeMajor2;
	}

	public int getWriteMinor2() {
		return writeMinor2;
	}

	public void setWriteMinor2(int writeMinor2) {
		this.writeMinor2 = writeMinor2;
	}
	
	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public String toString() {
		return "BeaconWriteInfo [writeUuid=" + writeUuid + ", writeMajor="
				+ writeMajor + ", writeMinor=" + writeMinor
				+ ", writeMeasuredPower=" + writeMeasuredPower
				+ ", writeTxPower=" + writeTxPower + ", writeAdv=" + writeAdv
				+ ", writePassword=" + writePassword + ", model=" + model
				+ ", url=" + url + ", uid=" + uid + ", uid_namespace="
				+ uid_namespace + ", uid_custom=" + uid_custom
				+ ", writeUuid2=" + writeUuid2 + ", writeMajor2=" + writeMajor2
				+ ", writeMinor2=" + writeMinor2 + ", secretKey=" + secretKey
				+ "]";
	}

}
