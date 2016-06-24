package com.lenovo.sdimobileclient.api;

import android.os.Parcel;
import android.os.Parcelable;

public class AttachmentTypes extends AbsApiData implements Parcelable {

	public String ID;
	public String Name;

	public AttachmentTypes() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ID);
		dest.writeString(Name);
	}

	public static final Parcelable.Creator<AttachmentTypes> CREATOR = new Parcelable.Creator<AttachmentTypes>() {
		public AttachmentTypes createFromParcel(Parcel in) {
			return new AttachmentTypes(in);
		}

		public AttachmentTypes[] newArray(int size) {
			return new AttachmentTypes[size];
		}
	};

	private AttachmentTypes(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		ID = in.readString();
		Name = in.readString();
	}
}
