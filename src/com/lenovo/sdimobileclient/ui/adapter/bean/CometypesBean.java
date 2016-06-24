package com.lenovo.sdimobileclient.ui.adapter.bean;

import java.util.List;

import com.lenovo.sdimobileclient.api.OrderInfo;

public class CometypesBean extends OrderInfo{

	public Object SourceName;
	public String SourceType;
	public List<SourceOptions> SourceOptions;

	public class SourceOptions {

		public String Name;
		public String ID;
	}

	public List<ComeTypes> ComeTypes;




	

}