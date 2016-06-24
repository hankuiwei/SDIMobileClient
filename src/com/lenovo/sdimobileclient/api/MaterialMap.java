package com.lenovo.sdimobileclient.api;

import java.util.List;

public class MaterialMap extends AbsApiData{

	public String category;
	public List<Material> materials;
	public MaterialMap(String category, List<Material> materials) {
		this.category = category;
		this.materials = materials;
	}
	
}
