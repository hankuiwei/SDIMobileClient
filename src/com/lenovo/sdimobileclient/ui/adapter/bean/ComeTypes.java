package com.lenovo.sdimobileclient.ui.adapter.bean;

import java.util.List;

import com.lenovo.sdimobileclient.api.OrderInfo;
import com.lenovo.sdimobileclient.api.SourceOption;

public class ComeTypes extends OrderInfo{

	public String Come;
	public int rule;
	public String  type;
	public int position;
	
	public List<SourceOption> TabControls;
}