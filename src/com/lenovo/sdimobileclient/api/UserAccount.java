package com.lenovo.sdimobileclient.api;

public class UserAccount extends AbsApiData {

	public int accountId;
	public String username;
	public String password;
	public boolean auto;
	public long timestamp;
	public UserAccount(int accountId, String username, String password, boolean auto, long timestamp) {
		this.accountId = accountId;
		this.username = username;
		this.password = password;
		this.auto = auto;
		this.timestamp = timestamp;
	}


}
