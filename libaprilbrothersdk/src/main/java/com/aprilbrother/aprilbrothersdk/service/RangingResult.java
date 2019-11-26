package com.aprilbrother.aprilbrothersdk.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.internal.Objects;
import com.aprilbrother.aprilbrothersdk.internal.Preconditions;
import com.aprilbrother.aprilbrothersdk.utils.AprilL;

public final class RangingResult implements Parcelable {
	public final Region region;
	public final List<Beacon> beacons;

	public RangingResult(Region region, Collection<Beacon> beacons) {
		this.region = ((Region) Preconditions.checkNotNull(region,
				"region cannot be null"));
		this.beacons = Collections.unmodifiableList(new ArrayList(
				(Collection) Preconditions.checkNotNull(beacons,
						"beacons cannot be null")));
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		RangingResult that = (RangingResult) o;

		if (!this.beacons.equals(that.beacons))
			return false;
		if (!this.region.equals(that.region)) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result = this.region.hashCode();
		result = 31 * result + this.beacons.hashCode();
		return result;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("region", this.region)
				.add("beacons", this.beacons).toString();
	}

	public static final Creator<RangingResult> CREATOR = new Creator<RangingResult>() {
		public RangingResult createFromParcel(Parcel source) {
			ClassLoader classLoader = getClass().getClassLoader();
			Region region = (Region) source.readParcelable(classLoader);
			List<Beacon> beacons = source.readArrayList(classLoader);
			return new RangingResult(region, beacons);
		}

		public RangingResult[] newArray(int size) {
			return new RangingResult[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.region, flags);
		dest.writeList(this.beacons);
	}
}
