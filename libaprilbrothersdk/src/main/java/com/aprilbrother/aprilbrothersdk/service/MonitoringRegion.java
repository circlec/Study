package com.aprilbrother.aprilbrothersdk.service;

import com.aprilbrother.aprilbrothersdk.Region;

import android.os.Messenger;

/** {@hide} */
class MonitoringRegion extends RangingRegion {
	private static final int NOT_SEEN = -1;
	private long lastSeenTimeMillis = -1L;

	private boolean wasInside;

	public MonitoringRegion(Region region, Messenger replyTo) {
		super(region, replyTo);
	}

	public boolean markAsSeen(long currentTimeMillis) {
		this.lastSeenTimeMillis = currentTimeMillis;
		if (!this.wasInside) {
			this.wasInside = true;
			return true;
		}
		return false;
	}

	public boolean isInside(long currentTimeMillis) {
		return (this.lastSeenTimeMillis != -1L)
//				&& (currentTimeMillis - this.lastSeenTimeMillis < BeaconService.EXPIRATION_MILLIS);
				&& (currentTimeMillis - this.lastSeenTimeMillis < BeaconService.expiration_millis_monitoring);
	}
	
	public boolean didJustExit(long currentTimeMillis) {
		if ((this.wasInside) && (!isInside(currentTimeMillis))) {
			this.lastSeenTimeMillis = -1L;
			this.wasInside = false;
			return true;
		}
		return false;
	}
}
