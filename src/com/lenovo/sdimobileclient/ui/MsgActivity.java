package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lenovo.sdimobileclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MsgActivity extends Activity {
	private static List<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msglayout);

		ListView list = (ListView) findViewById(R.id.listView1);
		// setListItem("test","testmsg");
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItem, R.layout.msg_listview, new String[] { "ItemImage", "ItemTitle", "ItemText" },
				new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });

		list.setAdapter(simpleAdapter);
	}

	public static List<HashMap<String, Object>> getListItem() {
		return listItem;
	}

	public static void addListItem(String title, String msg) {
		HashMap<String, Object> map = new HashMap<String, Object>(3);
		map.put("ItemImage", R.drawable.ic_launcher);
		map.put("ItemTitle", title);
		map.put("ItemText", msg);
		listItem.add(map);
	}

}