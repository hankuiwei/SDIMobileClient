package com.lenovo.sdimobileclient.ui;

import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.ChangeHistory;
import com.lenovo.sdimobileclient.api.ProductBox;
import com.lenovo.sdimobileclient.api.RootData;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;

public class ReplaceBoxActivity extends RootActivity implements OnItemClickListener {

	private ListView mListView;
	private ApiDataAdapter<ProductBox> mProductAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBackBtn();
		String str = getIntent().getStringExtra("replace");
		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
		RootData rootData = new RootData(str);
		List<ChangeHistory> changeHistories = rootData.getArrayData(ChangeHistory.class);
		if (changeHistories != null && !changeHistories.isEmpty()) {
			for (int i = 0; i < changeHistories.size(); i++) {
				View view = getLayoutInflater().inflate(R.layout.replace_history_item, null);
				if (i == 0) {
					view.findViewById(R.id.title_view).setVisibility(View.VISIBLE);
				} else {
					view.findViewById(R.id.title_view).setVisibility(View.GONE);
				}
				initHistoryView(changeHistories.get(i), view);
			}
		}
		List<ProductBox> productBoxs = rootData.getArrayData(ProductBox.class);
		mProductAdapter = new ApiDataAdapter<ProductBox>(this);
		mListView.setAdapter(mProductAdapter);
		if (productBoxs == null || productBoxs.isEmpty() || productBoxs.size() == 0) {

			mListView.setVisibility(View.GONE);
			findViewById(R.id.tv_desc_box).setVisibility(View.VISIBLE);

		}

		mProductAdapter.add(productBoxs);
	}

	private void initHistoryView(final ChangeHistory change, final View view) {
		TextView tv1 = (TextView) view.findViewById(R.id.tv_up1);
		TextView tv2 = (TextView) view.findViewById(R.id.tv_up2);
		TextView tv3 = (TextView) view.findViewById(R.id.tv_up3);
		TextView tv4 = (TextView) view.findViewById(R.id.tv_up4);
		TextView tv5 = (TextView) view.findViewById(R.id.tv_up5);
		TextView tv6 = (TextView) view.findViewById(R.id.tv_up6);
		tv5.setText(change.DownMaterialNo);
		tv6.setText(change.DownMaterialNoDesc);
		tv4.setText(change.DownPartsSN);
		tv2.setText(change.UpMaterialNo);
		tv3.setText(change.UpMaterialNoDesc);
		tv1.setText(change.UpPartsSN);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) view.findViewById(android.R.id.checkbox);
				checkBox.setChecked(true);
				Intent data = new Intent();
				data.putExtra("ChangeHistory", change.getJson().toString());
				setResult(RESULT_OK, data);
				finish();
			}
		});
		mListView.addHeaderView(view);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.replace_box;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if (obj instanceof ProductBox) {
			CheckBox cb = (CheckBox) view.findViewById(android.R.id.checkbox);
			if (cb != null)
				cb.setChecked(true);
			ProductBox box = (ProductBox) obj;
			Intent data = new Intent();
			data.putExtra("ProductBox", box.getJson().toString());
			setResult(RESULT_OK, data);
			finish();
		}
	}
}
