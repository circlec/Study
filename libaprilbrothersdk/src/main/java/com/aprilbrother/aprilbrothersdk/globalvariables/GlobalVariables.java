package com.aprilbrother.aprilbrothersdk.globalvariables;

import com.aprilbrother.aprilbrothersdk.bean.BeaconWriteInfo;

public class GlobalVariables {

	// 修改数据时存储的修改数据
	public static BeaconWriteInfo beaconWriteInfo = new BeaconWriteInfo();

	public static boolean shouldChangeModel = false;
	public static boolean shouldChangeUid = false;
	public static boolean shouldChangeUrl = false;
	// 给proxiot添加修改uid
	public static boolean shouldChangeUidNameSpace = false;
	public static boolean shouldChangeUidCustom = false;

	public static boolean isWriteUUID = false;
	public static boolean isWriteMajor = false;
	public static boolean isWriteMinor = false;
	public static boolean isWriteMeasuredPower = false;
	public static boolean isWriteAdvertisingInterval = false;
	public static boolean isWriteTxPower = false;
	public static boolean isWritePassword = false;
	public static boolean isWriteUUID2 = false;
	public static boolean isWriteMajor2 = false;
	public static boolean isWriteMinor2 = false;
	public static boolean isWriteSecretKey = false;

}
