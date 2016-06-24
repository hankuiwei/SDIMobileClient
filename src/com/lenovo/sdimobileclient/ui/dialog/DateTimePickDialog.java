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

public class DateTimePickDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
	}

}
