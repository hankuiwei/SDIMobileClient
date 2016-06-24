package com.lenovo.sdimobileclient.api;

import java.io.Serializable;

public class SourceOption extends AbsApiData {

	public String ID;
	public Object TabControls;
	public String Name;

	@Override
	public String toString() {
		return "SourceOption [ID=" + ID + ", Name=" + Name + "]";
	}

}
