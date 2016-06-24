package com.lenovo.sdimobileclient.api;

public class TextItem extends AbsApiData {

	public String title;
	public String text;
	public String author;
	public long timestamp;

	public TextItem() {

	}

	public TextItem(String title, String text, String author, long timestamp) {
		this.title = title;
		this.text = text;
		this.author = author;
		this.timestamp = timestamp;
	}

}
