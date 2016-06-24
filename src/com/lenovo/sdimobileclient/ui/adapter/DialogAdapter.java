package com.lenovo.sdimobileclient.ui.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.lenovo.sdimobileclient.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class DialogAdapter extends BaseAdapter {

	private List mList;
	private LayoutInflater minflater;

	public DialogAdapter(List list, LayoutInflater inflater) {
		this.minflater = inflater;
		this.mList = list;
	}

	public void setNewData(List list) {
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int paramInt) {
		return mList.get(paramInt);
	}

	@Override
	public long getItemId(int paramInt) {
		return 0;
	}

	@Override
	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

		if (paramView == null) {
			paramView = minflater.inflate(R.layout.item_check1_diaog, null);
		}
		TextView tv_desc = (TextView) paramView.findViewById(R.id.tv_desc);
		CheckBox cb_checkbox = (CheckBox) paramView.findViewById(R.id.checkbox);

		DialogBean item = (DialogBean) getItem(paramInt);
		tv_desc.setText(item.select);
		cb_checkbox.setChecked(item.ischeck);
		return paramView;
	}

}
