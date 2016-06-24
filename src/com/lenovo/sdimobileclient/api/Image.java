package com.lenovo.sdimobileclient.api;

import java.io.File;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {
	public String url;
	public boolean addImage;
	public File file;
	public boolean local;

	public Image() {
	}

	public Image(String url) {
		this.url = url;
	}

	public Image(String filepath, boolean local) {
		this.url = filepath;
		this.local = local;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeInt(local ? 1 : 0);

	}

	public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
		public Image createFromParcel(Parcel in) {
			return new Image(in);
		}

		public Image[] newArray(int size) {
			return new Image[size];
		}
	};

	private Image(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		url = in.readString();
		local = in.readInt() == 1;
	}
}
