package com.aprilbrother.aprilbrothersdk.connection;

import java.util.UUID;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.aprilbrother.aprilbrothersdk.constants.Constants;
import com.aprilbrother.aprilbrothersdk.receiver.ABeaconUARTStatusChangeReceiver;
import com.aprilbrother.aprilbrothersdk.receiver.ABeaconUARTStatusChangeReceiver.MyABeaconCallBack;
import com.aprilbrother.aprilbrothersdk.receiver.AprilBeaconUARTStatusChangeReceiver;
import com.aprilbrother.aprilbrothersdk.receiver.AprilBeaconUARTStatusChangeReceiver.MyAprBeaconCallBack;
import com.aprilbrother.aprilbrothersdk.services.ABeaconUartService;
import com.aprilbrother.aprilbrothersdk.services.UartService;

public class BeaconConnection {

	protected static final String TAG = BeaconConnection.class.getSimpleName();
	private static UartService mAprilBeaconService = null;
	private static ABeaconUartService mABeaconService = null;
	private static String address;
	private static Context context;
	private static MyAprBeaconCallBack aprCallBack;
	private static MyABeaconCallBack abCallBack;
	private static boolean isAprilBeaconBind;
	private static boolean isABeaconBind;

	private static boolean shouldABeaconRead;
	private static boolean shouldAprilBeaconRead;
	private static boolean isReadABBattery;
	private static boolean isSecondUUID;

	public static void connAprilBeacon(Context myContext, String myAddress,
			boolean shouldRead, MyAprBeaconCallBack myAprCallBack) {
		shouldAprilBeaconRead = shouldRead;
		context = myContext;
		address = myAddress;
		aprCallBack = myAprCallBack;
		Intent bindIntent = new Intent(context, UartService.class);
		context.bindService(bindIntent, mAprilBeaconServiceConnection,
				Context.BIND_AUTO_CREATE);
		isAprilBeaconBind = true;
	}

	public static void connABeacon(Context myContext, String myAddress,
			boolean shouldRead, MyABeaconCallBack myABCallBack) {
		shouldABeaconRead = shouldRead;
		abCallBack = myABCallBack;
		context = myContext;
		address = myAddress;
		Intent bindIntent = new Intent(context, ABeaconUartService.class);
		context.bindService(bindIntent, mABeaconServiceConnection,
				Context.BIND_AUTO_CREATE);
		isABeaconBind = true;
	}

	public static void connABeacon(Context myContext, String myAddress,
			boolean shouldRead, boolean isReadBattery,
			MyABeaconCallBack myABCallBack) {
		isReadABBattery = isReadBattery;
		shouldABeaconRead = shouldRead;
		abCallBack = myABCallBack;
		context = myContext;
		address = myAddress;
		Intent bindIntent = new Intent(context, ABeaconUartService.class);
		context.bindService(bindIntent, mABeaconServiceConnection,
				Context.BIND_AUTO_CREATE);
		isABeaconBind = true;
	}

	public static void connABeacon(Context myContext, String myAddress,
			boolean shouldRead, boolean isReadBattery, boolean hasSecondUUID,
			MyABeaconCallBack myABCallBack) {
		isReadABBattery = isReadBattery;
		shouldABeaconRead = shouldRead;
		isSecondUUID = hasSecondUUID;
		abCallBack = myABCallBack;
		context = myContext;
		address = myAddress;
		Intent bindIntent = new Intent(context, ABeaconUartService.class);
		context.bindService(bindIntent, mABeaconServiceConnection,
				Context.BIND_AUTO_CREATE);
		isABeaconBind = true;
	}

	private static ABeaconUARTStatusChangeReceiver aBeaconreceiver;
	private static ServiceConnection mABeaconServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {

			mABeaconService = ((ABeaconUartService.LocalBinder) rawBinder)
					.getService();
			mABeaconService.connect(address);
			aBeaconreceiver = new ABeaconUARTStatusChangeReceiver(
					mABeaconService, shouldABeaconRead, isReadABBattery,isSecondUUID,
					abCallBack);
			context.registerReceiver(aBeaconreceiver,
					makeGattUpdateIntentFilter());
		}

		public void onServiceDisconnected(ComponentName classname) {
			mABeaconService = null;
		}
	};
	private static AprilBeaconUARTStatusChangeReceiver aprilBeaconReceiver;
	private static ServiceConnection mAprilBeaconServiceConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {

			mAprilBeaconService = ((UartService.LocalBinder) rawBinder)
					.getService();
			mAprilBeaconService.connect(address);
			aprilBeaconReceiver = new AprilBeaconUARTStatusChangeReceiver(
					mAprilBeaconService, shouldAprilBeaconRead, aprCallBack);
			context.registerReceiver(aprilBeaconReceiver,
					makeGattUpdateIntentFilter());
		}

		public void onServiceDisconnected(ComponentName classname) {
			mAprilBeaconService = null;
		}
	};

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.AB_ACTION_GATT_CONNECTED);
		intentFilter.addAction(Constants.AB_ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(Constants.AB_ACTION_GATT_SERVICES_DISCOVERED_NEW);
		intentFilter.addAction(Constants.AB_ACTION_DATA_AVAILABLE);
		intentFilter.addAction(Constants.AB_DEVICE_DOES_NOT_SUPPORT_UART);
		intentFilter.addAction(Constants.AB_ACTION_DATA_CHANGE);
		intentFilter.addAction(Constants.AB_ACTION_DATA_WRITE);
		intentFilter.addAction(Constants.AB_ACTION_DATA_RESET);
		intentFilter.addAction(Constants.AB_ACTION_DATA_DESCRIPTORWRITE);
		intentFilter.addAction(Constants.AB_ACTION_DATA_WRITE_ERROR);
		intentFilter
				.addAction(Constants.AB_ACTION_GATT_SERVICES_DISCOVERED_ERROR);

		intentFilter.addAction(Constants.ACTION_GATT_CONNECTED);
		intentFilter.addAction(Constants.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(Constants.ACTION_GATT_SERVICES_DISCOVERED_NEW);
		intentFilter.addAction(Constants.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(Constants.DEVICE_DOES_NOT_SUPPORT_UART);
		intentFilter.addAction(Constants.ACTION_DATA_CHANGE);
		intentFilter.addAction(Constants.ACTION_DATA_WRITE);
		intentFilter.addAction(Constants.ACTION_DATA_RESET);

		return intentFilter;
	}

	public static void close() {
		if (isAprilBeaconBind) {
			context.unbindService(mAprilBeaconServiceConnection);
			isAprilBeaconBind = false;
		}

		if (mAprilBeaconService != null) {
			mAprilBeaconService.close();
			mAprilBeaconService.stopSelf();
			mAprilBeaconService = null;
		}
		if (isABeaconBind) {
			context.unbindService(mABeaconServiceConnection);
			isABeaconBind = false;
		}

		if (mABeaconService != null) {
			mABeaconService.close();
			mABeaconService.stopSelf();
			mABeaconService = null;
		}
		if (aprilBeaconReceiver != null) {
			context.unregisterReceiver(aprilBeaconReceiver);
			aprilBeaconReceiver = null;
		}
		if (aBeaconreceiver != null) {
			context.unregisterReceiver(aBeaconreceiver);
			aBeaconreceiver = null;
		}
	}

	public static void writeValue(UUID serviceUUID, UUID characteristicUUID,
			byte[] value, String className) {
		if (className.equals(UartService.class.getSimpleName())
				&& mAprilBeaconService != null) {
			mAprilBeaconService.writeRXCharacteristic(serviceUUID,
					characteristicUUID, value);
		} else if (className.equals(ABeaconUartService.class.getSimpleName())
				&& mABeaconService != null) {
			mABeaconService.writeRXCharacteristic(serviceUUID,
					characteristicUUID, value);
		}
	}

}
