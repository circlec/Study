package com.aprilbrother.aprilbrothersdk.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.aprilbrother.aprilbrothersdk.Beacon;
import com.aprilbrother.aprilbrothersdk.Region;
import com.aprilbrother.aprilbrothersdk.Region.State;
import com.aprilbrother.aprilbrothersdk.internal.Objects;
import com.aprilbrother.aprilbrothersdk.internal.Preconditions;

/** {@hide} */
public class MonitoringResult implements Parcelable {
	public final Region region;
	public final State state;
	public final List<Beacon> beacons;

	public MonitoringResult(Region region, State state,
			Collection<Beacon> collection) {
		this.region = ((Region) Preconditions.checkNotNull(region,
				"region cannot be null"));
		this.state = ((State) Preconditions.checkNotNull(state,
				"state cannot be null"));
		this.beacons = new ArrayList(collection);
	}

	public boolean equals(Object object) {
		if (this == object)
			return true;
		if ((object == null) || (getClass() != object.getClass())) {
			return false;
		}
		MonitoringResult that = (MonitoringResult) object;

		if (this.state != that.state)
			return false;
		if (this.region != null ? !this.region.equals(that.region)
				: that.region != null) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result = this.region != null ? this.region.hashCode() : 0;
		result = 31 * result + (this.state != null ? this.state.hashCode() : 0);
		return result;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("region", this.region)
				.add("state", this.state.name()).add("beacons", this.beacons)
				.toString();
	}

	public static final Creator<MonitoringResult> CREATOR = new Creator<MonitoringResult>() {
		public MonitoringResult createFromParcel(Parcel source) {
			ClassLoader classLoader = getClass().getClassLoader();
			Region region = (Region) source.readParcelable(classLoader);
			Region.State event = Region.State.values()[source.readInt()];
			Collection<Beacon> beacons = source.readArrayList(classLoader);
			return new MonitoringResult(region, event, beacons);
		}

		public MonitoringResult[] newArray(int size) {
			return new MonitoringResult[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.region, flags);
		dest.writeInt(this.state.ordinal());
		dest.writeList(this.beacons);
	}
}
