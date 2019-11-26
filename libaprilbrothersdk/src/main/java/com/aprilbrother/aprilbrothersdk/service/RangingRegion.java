package com.aprilbrother.aprilbrothersdk.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Messenger;
import android.util.Log;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.Utils;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;

/** {@hide} */
class RangingRegion {
	private static final Comparator<Beacon> BEACON_ACCURACY_COMPARATOR = new Comparator<Beacon>() {

		@Override
		public int compare(Beacon lhs, Beacon rhs) {
			// TODO Auto-generated method stub
			return Double.compare(Utils.computeAccuracy((Beacon) lhs),
					Utils.computeAccuracy((Beacon) rhs));
		}
	};

	private final ConcurrentHashMap<Beacon, Long> beacons;

	final Region region;

	final Messenger replyTo;

	RangingRegion(Region region, Messenger replyTo) {
		this.region = region;
		this.replyTo = replyTo;
		this.beacons = new ConcurrentHashMap();
	}

	public final Collection<Beacon> getSortedBeacons() {
		ArrayList<Beacon> sortedBeacons = new ArrayList(this.beacons.keySet());
		Collections.sort(sortedBeacons, BEACON_ACCURACY_COMPARATOR);
		return sortedBeacons;
	}

	public final void processFoundBeacons(
			Map<Beacon, Long> beaconsFoundInScanCycle) {
		try {
			for (Map.Entry<Beacon, Long> entry : beaconsFoundInScanCycle.entrySet()) {
				if (Utils.isBeaconInRegion((Beacon) entry.getKey(), this.region)) {
					if (entry.getKey() != null && entry.getValue() != null) {
						this.beacons.remove(entry.getKey());
						this.beacons.put(entry.getKey(), entry.getValue());
					}
				}
			}
		} catch (Exception e) {
			AprilL.e("Exception = "+e.toString());
			e.printStackTrace();
		}
	}

	public final void removeNotSeenBeacons(long currentTimeMillis) {
		try {
			Iterator<Map.Entry<Beacon, Long>> iterator = this.beacons.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Map.Entry<Beacon, Long> entry = (Map.Entry) iterator.next();
				// if (currentTimeMillis - ((Long) entry.getValue()).longValue() >
				// BeaconService.EXPIRATION_MILLIS) {
				if (entry.getKey() != null && entry.getValue() != null) {
					if (currentTimeMillis - ((Long) entry.getValue()).longValue() > BeaconService.expiration_millis_ranging) {
						AprilL.v("Not seen lately: " + entry.getKey());
						iterator.remove();
					}
				}
			}
		} catch (Exception e) {
			AprilL.e("Exception = "+e.toString());
			e.printStackTrace();
		}
	}
}
