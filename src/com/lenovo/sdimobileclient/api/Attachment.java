package com.lenovo.sdimobileclient.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Attachment extends AbsApiData implements Parcelable {

	public String Type;
	public String Name;

	public Attachment() {

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(Type);
		dest.writeString(Name);
	}

	public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>() {
		public Attachment createFromParcel(Parcel in) {
			return new Attachment(in);
		}

		public Attachment[] newArray(int size) {
			return new Attachment[size];
		}
	};

	private Attachment(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		Type = in.readString();
		Name = in.readString();
	}
}
