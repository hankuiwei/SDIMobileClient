package com.lenovo.sdimobileclient.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.Attachment;
import com.lenovo.sdimobileclient.data.Attach;
import com.lenovo.sdimobileclient.ui.adapter.ApiDataAdapter;

public class AttachmentListActivity extends RootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.action_view).setVisibility(View.GONE);
		initBackBtn();
		List<Attachment> list = getIntent().getParcelableArrayListExtra("attachs");
		String orderId = getIntent().getStringExtra("orderId");
		List<Attach> attachments = new ArrayList<Attach>();
		for (Attachment attachment : list) {
			attachments.add(new Attach(attachment.Name, orderId, attachment.Type, 1));
		}
		ListView listView = (ListView) findViewById(android.R.id.list);
		ApiDataAdapter<Attach> mAttachAdapter = new ApiDataAdapter<Attach>(this);
		mAttachAdapter.add(attachments);
		listView.setAdapter(mAttachAdapter);
	}

	@Override
	protected int getContentViewId() {
		return R.layout.attachmentlist;
	}
}
