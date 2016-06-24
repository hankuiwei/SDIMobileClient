package com.lenovo.sdimobileclient.api;

public class Material extends AbsApiData{

	public String MaterialCategory;
	public String MaterialID;
	public String MaterialCode;
	public String MaterialName;
	public boolean spitvis;
	
	public Material() {
	}

	public Material(String materialCategory) {
		MaterialCategory = materialCategory;
	}
	
}
