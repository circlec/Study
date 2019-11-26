package com.aprilbrother.aprilbrothersdk;

import android.os.Parcel;
import android.os.Parcelable;

import com.aprilbrother.aprilbrothersdk.internal.Objects;
import com.aprilbrother.aprilbrothersdk.internal.Preconditions;

public class Region implements Parcelable {
	private final String identifier;
	private final String proximityUUID;
	private final Integer major;
	private final Integer minor;

	public static enum State {
		INSIDE,

		OUTSIDE;

		private State() {
		}
	}

	/**
	 * identifier - A unique identifier for a region. Cannot be null.
	 * proximityUUID - Proximity UUID of beacons. Can be null. Null indicates all proximity UUIDs.
	 * major - Major version of the beacons. Can be null.Null indicates all major versions. 
	 * minor - Minor version of the beacons.Can be null. Null indicates all minor versions.
	 */
	public Region(String identifier, String proximityUUID, Integer major,
			Integer minor) {
		this.identifier = ((String) Preconditions.checkNotNull(identifier));
		this.proximityUUID = (proximityUUID != null ? Utils
				.normalizeProximityUUID(proximityUUID) : proximityUUID);
		this.major = major;
		this.minor = minor;
	}

	/**
	 * 获取region对应的Identifier
	 * 
	 * @return region对应的Identifier
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * 获取region对应的ProximityUUID
	 * 
	 * @return region对应的ProximityUUID
	 */
	public String getProximityUUID() {
		return this.proximityUUID;
	}

	/**
	 * 获取region对应的Major
	 * 
	 * @return region对应的Major
	 */
	public Integer getMajor() {
		return this.major;
	}

	/**
	 * 获取region对应的Minor
	 * 
	 * @return region对应的Minor
	 */
	public Integer getMinor() {
		return this.minor;
	}

	public String toString() {
		return Objects.toStringHelper(this).add("identifier", this.identifier)
				.add("proximityUUID", this.proximityUUID)
				.add("major", this.major).add("minor", this.minor).toString();
	}

	public boolean equals(Object object) {
		if (this == object)
			return true;
		if ((object == null) || (getClass() != object.getClass())) {
			return false;
		}
		Region region = (Region) object;

		if (this.major != null ? !this.major.equals(region.major)
				: region.major != null)
			return false;
		if (this.minor != null ? !this.minor.equals(region.minor)
				: region.minor != null)
			return false;
		if (this.proximityUUID != null ? !this.proximityUUID
				.equals(region.proximityUUID) : region.proximityUUID != null) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int result = this.proximityUUID != null ? this.proximityUUID.hashCode()
				: 0;
		result = 31 * result + (this.major != null ? this.major.hashCode() : 0);
		result = 31 * result + (this.minor != null ? this.minor.hashCode() : 0);
		return result;
	}

	public static final Creator<Region> CREATOR = new Creator() {
		public Region createFromParcel(Parcel source) {
			return new Region(source);
		}

		public Region[] newArray(int size) {
			return new Region[size];
		}
	};

	private Region(Parcel parcel) {
		this.identifier = parcel.readString();
		this.proximityUUID = parcel.readString();
		Integer majorTemp = Integer.valueOf(parcel.readInt());
		if (majorTemp.intValue() == -1) {
			majorTemp = null;
		}
		this.major = majorTemp;
		Integer minorTemp = Integer.valueOf(parcel.readInt());
		if (minorTemp.intValue() == -1) {
			minorTemp = null;
		}
		this.minor = minorTemp;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.identifier);
		dest.writeString(this.proximityUUID);
		dest.writeInt(this.major == null ? -1 : this.major.intValue());
		dest.writeInt(this.minor == null ? -1 : this.minor.intValue());
	}
}
