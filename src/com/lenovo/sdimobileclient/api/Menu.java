package com.lenovo.sdimobileclient.api;

public class Menu extends AbsApiData {

	public String label;
	public int action;

	public Menu(String label, int action) {
		this.label = label;
		this.action = action;
	}

}
