package com.lenovo.sdimobileclient.ui.dialog;

import java.util.ArrayList;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.ui.adapter.DialogAdapter;
import com.lenovo.sdimobileclient.ui.adapter.DialogBean;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class Check1Dialog extends Activity implements OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check1dialog);
		setFinishOnTouchOutside(true);
		TextView title = (TextView) findViewById(R.id.tv_title);
		ListView mListView = (ListView) findViewById(android.R.id.list);
		String[] data = (String[]) getIntent().getCharSequenceArrayExtra("data");
		int intExtra = getIntent().getIntExtra("checkposition", -1);
		String stringExtra = getIntent().getStringExtra("title");

		title.setText(TextUtils.isEmpty(stringExtra) ? "" : stringExtra);
		title.setVisibility(TextUtils.isEmpty(stringExtra) ? View.GONE : View.VISIBLE);

		ArrayList<DialogBean> arrayList = new ArrayList<DialogBean>();
		for (String s : data) {
			DialogBean dialogBean = new DialogBean();
			dialogBean.ischeck = false;
			dialogBean.select = s;
			arrayList.add(dialogBean);
		}

		arrayList.get(intExtra == -1 ? 0 : intExtra).ischeck = true;
		mListView.setAdapter(new DialogAdapter(arrayList, LayoutInflater.from(this)));
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mListView.setOnItemClickListener(this);
		getWindow().setGravity(Gravity.BOTTOM);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		setResult(position);
		finish();

	}

}
