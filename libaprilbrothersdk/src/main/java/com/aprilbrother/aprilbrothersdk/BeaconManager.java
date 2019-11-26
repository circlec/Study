package com.aprilbrother.aprilbrothersdk;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.aprilbrother.aprilbrothersdk.BleManager.MyScanCallBack;
import com.aprilbrother.aprilbrothersdk.internal.Preconditions;
import com.aprilbrother.aprilbrothersdk.service.BeaconService;
import com.aprilbrother.aprilbrothersdk.service.MonitoringResult;
import com.aprilbrother.aprilbrothersdk.service.RangingResult;
import com.aprilbrother.aprilbrothersdk.service.ScanPeriodData;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;
import com.aprilbrother.aprilbrothersdk.utils.BLEScanRecordUtil;

@SuppressLint("HandlerLeak")
public class BeaconManager {
	@SuppressWarnings("unused")
	private static final String ANDROID_MANIFEST_CONDITIONS_MSG = "AndroidManifest.xml does not contain android.permission.BLUETOOTH or android.permission.BLUETOOTH_ADMIN permissions. BeaconService may be also not declared in AndroidManifest.xml.";
	private final Context context;
	private final InternalServiceConnection serviceConnection;
	private final Messenger incomingMessenger;
	private final Set<String> rangedRegionIds;
	private final Set<String> monitoredRegionIds;
	private Messenger serviceMessenger;
	private RangingListener rangingListener;
	private MonitoringListener monitoringListener;
	private ErrorListener errorListener;
	private ServiceReadyCallback callback;
	private ScanPeriodData foregroundScanPeriod;
	private ScanPeriodData backgroundScanPeriod;
	private EddyStoneListener eddyStoneListener;
	private BleManager manager;

	public BeaconManager(Context context) {
		this.context = ((Context) Preconditions.checkNotNull(context));
		this.serviceConnection = new InternalServiceConnection();
		this.incomingMessenger = new Messenger(new IncomingHandler());
		this.rangedRegionIds = new HashSet<String>();
		this.monitoredRegionIds = new HashSet<String>();
	}

	/**
	 * 检测是否支持蓝牙4.0BLE
	 * 
	 * @return true为设备支持BLE
	 */
	public boolean hasBluetooth() {
		return this.context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE);
	}

	/**
	 * 服务开启回调
	 */
	public static abstract interface ServiceReadyCallback {
		/**
		 * 服务已经开启
		 */
		public abstract void onServiceReady();
	}

	/**
	 * Ranging监听
	 * 
	 */
	public static abstract interface RangingListener {
		/**
		 * beacons被发现
		 * 
		 * @param paramRegion
		 *            Ranging的范围region
		 * @param paramList
		 *            范围内扫描到的beacon
		 */
		public abstract void onBeaconsDiscovered(Region paramRegion,
                                                 List<Beacon> paramList);
	}

	/**
	 * EddyStone监听
	 * 
	 */
	public static abstract interface EddyStoneListener {

		public abstract void onEddyStoneDiscovered(EddyStone eddyStone);
	}

	/**
	 * 检测蓝牙是否可用
	 * 
	 * @return true为可用
	 */
	public boolean isBluetoothEnabled() {
		if (!checkPermissionsAndService()) {
			AprilL.e("AndroidManifest.xml does not contain android.permission.BLUETOOTH or android.permission.BLUETOOTH_ADMIN permissions. BeaconService may be also not declared in AndroidManifest.xml.");
			return false;
		}
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		return (adapter != null) && (adapter.isEnabled());
	}

	/**
	 * 检查权限及服务是否配置进清单文件中
	 * 
	 * @return true为已经配置
	 */
	public boolean checkPermissionsAndService() {
		PackageManager pm = this.context.getPackageManager();
		int bluetoothPermission = pm.checkPermission(
				"android.permission.BLUETOOTH", this.context.getPackageName());
		int bluetoothAdminPermission = pm.checkPermission(
				"android.permission.BLUETOOTH_ADMIN",
				this.context.getPackageName());

		Intent intent = new Intent(this.context, BeaconService.class);
		List<?> resolveInfo = pm.queryIntentServices(intent, PackageManager.MATCH_DEFAULT_ONLY);

		return (bluetoothPermission == 0) && (bluetoothAdminPermission == 0)
				&& (resolveInfo.size() > 0);
	}

	/**
	 * 连接BeaconService
	 * 
	 * @param callback
	 *            服务是否准备好的回调
	 */
	public void connect(ServiceReadyCallback callback) {
		if (!checkPermissionsAndService()) {
			String a = "AndroidManifest.xml does not contain android.permission.BLUETOOTH or android.permission.BLUETOOTH_ADMIN permissions. BeaconService may be also not declared in AndroidManifest.xml.";
			AprilL.e(a);
		}
		this.callback = ((ServiceReadyCallback) Preconditions.checkNotNull(
				callback, "callback cannot be null"));
		if (isConnectedToService()) {
			callback.onServiceReady();
		}
		boolean bound = this.context.bindService(new Intent(this.context,
				BeaconService.class), this.serviceConnection, Context.BIND_AUTO_CREATE);

		if (!bound) {
			String a = "Could not bind service: make sure that com.aprilbrother.aprilbrothersdk.service.BeaconService is declared in AndroidManifest.xml";
			AprilL.w(a);
		}
	}

	/**
	 * 断开连接
	 */
	public void disconnect() {
		if (!isConnectedToService()) {
			AprilL.i("Not disconnecting because was not connected to service");
			return;
		}
		CopyOnWriteArraySet<String> tempRangedRegionIds = new CopyOnWriteArraySet<String>(
				this.rangedRegionIds);
		for (String regionId : tempRangedRegionIds) {
			try {
				internalStopRanging(regionId);
			} catch (RemoteException e) {
				AprilL.e("Swallowing error while disconnect/stopRanging", e);
				e.printStackTrace();
			}
		}
		CopyOnWriteArraySet<String> tempMonitoredRegionIds = new CopyOnWriteArraySet<String>(
				this.monitoredRegionIds);
		for (String regionId : tempMonitoredRegionIds) {
			try {
				internalStopMonitoring(regionId);
			} catch (RemoteException e) {
				AprilL.e("Swallowing error while disconnect/stopMonitoring", e);
				e.printStackTrace();
			}
		}
		this.context.unbindService(this.serviceConnection);
		this.serviceMessenger = null;
	}

	/**
	 * 设置Ranging监听
	 * 
	 * @param listener
	 *            Ranging监听
	 */
	public void setRangingListener(RangingListener listener) {
		this.rangingListener = ((RangingListener) Preconditions.checkNotNull(
				listener, "listener cannot be null"));
	}

	/**
	 * 设置Monitoring监听
	 * 
	 * @param listener
	 *            Monitoring监听
	 */
	public void setMonitoringListener(MonitoringListener listener) {
		this.monitoringListener = ((MonitoringListener) Preconditions
				.checkNotNull(listener, "listener cannot be null"));
	}

	/**
	 * 设置EddyStone监听
	 * 
	 * @param listener
	 *            EddyStone监听
	 */
	public void setEddyStoneListener(EddyStoneListener listener) {
		this.eddyStoneListener = ((EddyStoneListener) Preconditions
				.checkNotNull(listener, "listener cannot be null"));
	}

	/**
	 * 设置错误监听
	 * 
	 * @param listener
	 */
	public void setErrorListener(ErrorListener listener) {
		this.errorListener = listener;
		if ((isConnectedToService()) && (listener != null)) {
			registerErrorListenerInService();
		}
	}

	/**
	 * 设置ranging扫描周期
	 * 
	 * @param scanPeriodMillis
	 *            扫描时间
	 * @param waitTimeMillis
	 *            等待时间
	 */
	public void setForegroundScanPeriod(long scanPeriodMillis,
			long waitTimeMillis) {
		if (isConnectedToService()) {
			setScanPeriod(new ScanPeriodData(scanPeriodMillis, waitTimeMillis),
					10);
		} else {
			this.foregroundScanPeriod = new ScanPeriodData(scanPeriodMillis,
					waitTimeMillis);
		}
	}

	/**
	 * 设置Monitoring扫描周期
	 * 
	 * @param scanPeriodMillis
	 *            扫描时间
	 * @param waitTimeMillis
	 *            等待时间
	 */
	public void setBackgroundScanPeriod(long scanPeriodMillis,
			long waitTimeMillis) {
		if (isConnectedToService()) {
			setScanPeriod(new ScanPeriodData(scanPeriodMillis, waitTimeMillis),
					9);
		} else {
			this.backgroundScanPeriod = new ScanPeriodData(scanPeriodMillis,
					waitTimeMillis);
		}
	}

	private void setScanPeriod(ScanPeriodData scanPeriodData, int msgId) {
		Message scanPeriodMsg = Message.obtain(null, msgId);
		scanPeriodMsg.obj = scanPeriodData;
		try {
			this.serviceMessenger.send(scanPeriodMsg);
		} catch (RemoteException e) {
			AprilL.e("Error while setting scan periods: " + msgId);
			e.printStackTrace();
		}
	}

	private void registerErrorListenerInService() {
		Message registerMsg = Message.obtain(null, 7);
		registerMsg.replyTo = this.incomingMessenger;
		try {
			this.serviceMessenger.send(registerMsg);
		} catch (RemoteException e) {
			AprilL.e("Error while registering error listener");
			e.printStackTrace();
		}
	}

	/**
	 * Monitoring监听
	 */
	public static abstract interface MonitoringListener {
		/**
		 * 进入region内的回调
		 * 
		 * @param paramRegion
		 *            进入的region
		 * @param paramList
		 *            region内的beacons
		 */
		public abstract void onEnteredRegion(Region paramRegion,
                                             List<Beacon> paramList);

		/**
		 * 离开region的回调
		 * 
		 * @param paramRegion
		 *            离开的region
		 */
		public abstract void onExitedRegion(Region paramRegion);
	}

	/**
	 * 开始扫描刷新
	 * 
	 * @param region
	 *            扫描的region
	 * @throws RemoteException
	 */
	public void startRanging(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			AprilL.i("Not starting ranging, not connected to service");
			return;
		}
		Preconditions.checkNotNull(region, "region cannot be null");

		if (this.rangedRegionIds.contains(region.getIdentifier())) {
			String a = "Region already ranged but that's OK: " + region;
			AprilL.i(a);
		}
		this.rangedRegionIds.add(region.getIdentifier());

		Message startRangingMsg = Message.obtain(null, 1);
		startRangingMsg.obj = region;
		startRangingMsg.replyTo = this.incomingMessenger;
		try {
			this.serviceMessenger.send(startRangingMsg);
		} catch (RemoteException e) {
			AprilL.e("Error while starting ranging", e);
			throw e;
		}
	}

	/**
	 * 停止刷新扫描
	 * 
	 * @param region
	 *            扫描的region
	 * @throws RemoteException
	 */
	public void stopRanging(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			AprilL.i("Not stopping ranging, not connected to service");
			return;
		}
		Preconditions.checkNotNull(region, "region cannot be null");
		internalStopRanging(region.getIdentifier());
	}

	private void internalStopRanging(String regionId) throws RemoteException {
		this.rangedRegionIds.remove(regionId);
		Message stopRangingMsg = Message.obtain(null, 2);
		stopRangingMsg.obj = regionId;
		try {
			this.serviceMessenger.send(stopRangingMsg);
		} catch (RemoteException e) {
			AprilL.e("Error while stopping ranging", e);
			throw e;
		}
	}

	/**
	 * 开启监控
	 * 
	 * @param region
	 *            监控的region
	 * @throws RemoteException
	 */
	public void startMonitoring(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			AprilL.i("Not starting monitoring, not connected to service");
			return;
		}
		Preconditions.checkNotNull(region, "region cannot be null");

		if (this.monitoredRegionIds.contains(region.getIdentifier())) {
			String a = "Region already monitored but that's OK: " + region;
			AprilL.i(a);
		}
		this.monitoredRegionIds.add(region.getIdentifier());

		Message startMonitoringMsg = Message.obtain(null, 4);
		startMonitoringMsg.obj = region;
		startMonitoringMsg.replyTo = this.incomingMessenger;
		try {
			this.serviceMessenger.send(startMonitoringMsg);
		} catch (RemoteException e) {
			AprilL.e("Error while starting monitoring", e);
			throw e;
		}
	}

	/**
	 * 停止监控
	 * 
	 * @param region
	 *            监控的region
	 * @throws RemoteException
	 */
	public void stopMonitoring(Region region) throws RemoteException {
		if (!isConnectedToService()) {
			AprilL.i("Not stopping monitoring, not connected to service");
			return;
		}
		Preconditions.checkNotNull(region, "region cannot be null");
		internalStopMonitoring(region.getIdentifier());
	}

	private void internalStopMonitoring(String regionId) throws RemoteException {
		this.monitoredRegionIds.remove(regionId);
		Message stopMonitoringMsg = Message.obtain(null, 5);
		stopMonitoringMsg.obj = regionId;
		try {
			this.serviceMessenger.send(stopMonitoringMsg);
		} catch (RemoteException e) {
			AprilL.e("Error while stopping ranging");
			throw e;
		}
	}

	private boolean isConnectedToService() {
		return this.serviceMessenger != null;
	}

	/**
	 * 错误监听
	 */
	public static abstract interface ErrorListener {
		public abstract void onError(Integer paramInteger);
	}

	private class InternalServiceConnection implements ServiceConnection {
		private InternalServiceConnection() {
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			BeaconManager.this.serviceMessenger = new Messenger(service);
			if (BeaconManager.this.errorListener != null) {
				BeaconManager.this.registerErrorListenerInService();
			}
			if (BeaconManager.this.foregroundScanPeriod != null) {
				BeaconManager.this.setScanPeriod(
						BeaconManager.this.foregroundScanPeriod, 9);
				BeaconManager.this.foregroundScanPeriod = null;
			}
			if (BeaconManager.this.backgroundScanPeriod != null) {
				BeaconManager.this.setScanPeriod(
						BeaconManager.this.backgroundScanPeriod, 10);
				BeaconManager.this.backgroundScanPeriod = null;
			}
			if (BeaconManager.this.callback != null) {
				BeaconManager.this.callback.onServiceReady();
				BeaconManager.this.callback = null;
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			AprilL.e("Service disconnected, crashed? " + name);
			BeaconManager.this.serviceMessenger = null;
		}
	}

	private class IncomingHandler extends Handler {
		private IncomingHandler() {
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 3:
				if (BeaconManager.this.rangingListener != null) {
					RangingResult rangingResult = (RangingResult) msg.obj;
					BeaconManager.this.rangingListener.onBeaconsDiscovered(
							rangingResult.region, rangingResult.beacons);
				}
				break;

			case 6:
				if (BeaconManager.this.monitoringListener != null) {
					MonitoringResult monitoringResult = (MonitoringResult) msg.obj;
					if (monitoringResult.state == Region.State.INSIDE) {
						BeaconManager.this.monitoringListener.onEnteredRegion(
								monitoringResult.region,
								monitoringResult.beacons);
					} else
						BeaconManager.this.monitoringListener
								.onExitedRegion(monitoringResult.region);
				}
				break;
			case 8:
				if (BeaconManager.this.errorListener != null) {
					Integer errorId = (Integer) msg.obj;
					BeaconManager.this.errorListener.onError(errorId);
				}
				break;
			// default:
			// AprilL.d("Unknown message: " + msg);
			}
		}
	}

	/**
	 * 设置monitoring的离开判定时间 eg:setMonitoringExpirationMill(20L)
	 * 20s内未发现beacon判定beacon离开region
	 * 
	 * @param time
	 *            设置的时间 单位为s
	 */
	public void setMonitoringExpirationMill(long time) {
		BeaconService.expiration_millis_monitoring = TimeUnit.SECONDS
				.toMillis(time);
	}

	/**
	 * 设置ranging中beacon的是否在范围内的判定时间 eg:setRangingExpirationMill(2L) 2s内未发现beacon
	 * 扫描到的结果中将移除之前发现的beacon
	 * 
	 * @param time
	 *            设置的时间 单位为s
	 */
	public void setRangingExpirationMill(long time) {
		BeaconService.expiration_millis_ranging = TimeUnit.SECONDS
				.toMillis(time);
	}

	public void startEddyStoneScan() {
		manager = new BleManager(context);
		manager.startBleScan(new MyScanCallBack() {

			@Override
			public void onScanCallBack(BluetoothDevice device, int rssi,
					byte[] scanRecord) {
				String type = BLEScanRecordUtil.getType(scanRecord);
				if (type.equals(EddyStone.MODEL_URL)) {
					String url = BLEScanRecordUtil.decodeUrl(scanRecord);
					EddyStone eddyStone = new EddyStone(EddyStone.MODEL_URL,
							url, device.getName(), device.getAddress(), rssi);
					if (eddyStoneListener != null) {
						eddyStoneListener.onEddyStoneDiscovered(eddyStone);
					}
				} else if (type.equals(EddyStone.MODEL_UID)) {
					String uid = BLEScanRecordUtil.getUID(scanRecord);
					EddyStone eddyStone = new EddyStone(EddyStone.MODEL_UID,
							uid, device.getName(), device.getAddress(), rssi);
					if (eddyStoneListener != null) {
						eddyStoneListener.onEddyStoneDiscovered(eddyStone);
					}
				} else if (type.equals(EddyStone.MODEL_IBEACON)) {
					String uuidMajorMinor = BLEScanRecordUtil
							.getBeaconUUIDMajorMinor(scanRecord);
					EddyStone eddyStone = new EddyStone(
							EddyStone.MODEL_IBEACON, uuidMajorMinor, device
									.getName(), device.getAddress(), rssi);
					if (eddyStoneListener != null) {
						eddyStoneListener.onEddyStoneDiscovered(eddyStone);
					}
				}
			}
		});
	}

	public void stopEddyStoneScan() {
		if (manager != null) {
			manager.stopBleScan();
		}
	}
}
