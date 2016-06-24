
package com.lenovo.sdimobileclient.data;

/**
 *event实体
 * 
 * @author zhangshaofang
 *
 */
public class MyEvent {

	private String msg ;

	public MyEvent(String mString) {
		super();
		this.msg = mString;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}


}
