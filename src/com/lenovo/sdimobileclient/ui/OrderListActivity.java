package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.OrderInfo;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;
/**
 * 工单列表
 * @author zhangshaofang
 *
 */
public class OrderListActivity extends RootActivity implements OnItemClickListener {

	private ApiDataAdapter<OrderInfo> mOrderAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		findViewById(R.id.btn_back).setVisibility(View.VISIBLE);
		String type = getIntent().getStringExtra("type");
		setTitle(type);
		ListView listView = (ListView) findViewById(android.R.id.list);
		mOrderAdapter = new ApiDataAdapter<OrderInfo>(this);
		List<OrderInfo> orders = new ArrayList<OrderInfo>();
//		for (int i = 0; i < 10; i++)
//			orders.add(new Order("188388273401"+i,type, "234892374985"+i, "IdeaCentre A32"+i, "安装","赵志康",System.currentTimeMillis()));
		mOrderAdapter.add(orders);
		listView.setAdapter(mOrderAdapter);
		listView.setOnItemClickListener(this);
		
//		Button bt_orderinfo_show = (Button) findViewById(R.id.bt_orderinfo_show);
//		bt_orderinfo_show.setOnClickListener(OrderListActivity.this);
//		hummer_news = (LinearLayout) findViewById(R.id.hummer_news);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.order_list;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object obj = parent.getItemAtPosition(position);
		if (obj instanceof OrderInfo) {
			Intent intent = new Intent(this, OrderActivity.class);
			intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
			startActivity(intent);
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
//		case R.id.bt_orderinfo_show:
//			hummer_news.setVisibility(View.VISIBLE);
//			break;

		}
	}
}
