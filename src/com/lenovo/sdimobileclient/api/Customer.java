package com.lenovo.sdimobileclient.api;

public class Customer extends AbsApiData{

	public String phone;
	public String tel;
	public String address;
	public Customer(){}
	public Customer(String phone, String tel, String address) {
		this.phone = phone;
		this.tel = tel;
		this.address = address;
	}
	
}
