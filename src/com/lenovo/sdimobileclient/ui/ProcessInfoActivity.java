package com.lenovo.sdimobileclient.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.lenovo.sdimobileclient.R;

/**
 * 技术通报详情
 * 
 * @author zhangshaofang
 * 
 */
public class ProcessInfoActivity extends RootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBackBtn();
		initView();
	}

	private void initView() {
		WebView webView = (WebView) findViewById(R.id.webview);
		String url = getIntent().getStringExtra("url");
		String title = getIntent().getStringExtra("title");
		setTitle(title);
		webView.loadUrl(url);
		WebSettings webSettings = webView.getSettings();
		final View waitView = findViewById(R.id.progress);
		final TextView mProgressTv = (TextView) findViewById(R.id.progress_tv);

		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setLoadsImagesAutomatically(true);
		// webSettings.setBlockNetworkImage(false);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				int visible = progress != 0 ? ViewGroup.GONE : ViewGroup.VISIBLE;
				mProgressTv.setText(getString(R.string.loadingweb, progress));
				waitView.setVisibility(visible);
			}
		});
	}

	@Override
	protected int getContentViewId() {
		return R.layout.process_info;
	}
}
