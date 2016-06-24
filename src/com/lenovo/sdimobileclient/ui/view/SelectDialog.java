package com.lenovo.sdimobileclient.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.ui.adapter.DialogAdapter;
import com.lenovo.sdimobileclient.utility.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SelectDialog extends AlertDialog implements OnItemClickListener {

	private TextView mTitle;
	private LayoutInflater inflater;
	private DialogAdapter dialogAdapter;
	private Context context;
	private ListView mListView;

	public SelectDialog(Context context, LayoutInflater inflater) {
		this(context, R.style.dialog, inflater);
	}

	public SelectDialog(Context context, int theme, LayoutInflater inflater) {
		super(context, theme);
		this.context = context;
		this.inflater = inflater;

		dialogAdapter = new DialogAdapter(new ArrayList(), inflater);
	}

	@Override
	public void setTitle(CharSequence title) {
	}

	public void setData(List list) {

		dialogAdapter.setNewData(list);
		dialogAdapter.notifyDataSetChanged();
	}

	@Override
	public ListView getListView() {
		return mListView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View inflate = inflater.inflate(R.layout.dialog, null);
		setContentView(inflate);
		mListView = (ListView) inflate.findViewById(android.R.id.list);

		mListView.setAdapter(dialogAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Utils.showToast(context, position + "");
	}
}
