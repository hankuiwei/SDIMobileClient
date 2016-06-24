package com.lenovo.sdimobileclient.api;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskHistory extends AbsApiData implements Parcelable {

	public String ID;
	public String Name;
	public String TaskBack;

	public TaskHistory() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ID);
		dest.writeString(Name);
		dest.writeString(TaskBack);

	}

	public static final Parcelable.Creator<TaskHistory> CREATOR = new Parcelable.Creator<TaskHistory>() {
		public TaskHistory createFromParcel(Parcel in) {
			return new TaskHistory(in);
		}

		public TaskHistory[] newArray(int size) {
			return new TaskHistory[size];
		}
	};

	private TaskHistory(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		ID = in.readString();
		Name = in.readString();
		TaskBack = in.readString();
	}
}
