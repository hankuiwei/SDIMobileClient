package com.lenovo.sdimobileclient.api;

import java.io.Serializable;

public class SOListHist extends AbsApiData implements Serializable {
	public Long SOID;
	public String ServiceTypeName;
	
	public OrderInfo  orderInfo ;
}