package com.lenovo.sdimobileclient.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.foreveross.cache.AbsConfiguration;
import com.foreveross.cache.AbsDownloader;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.FileInfo;
import com.foreveross.cache.network.FilePair;
import com.foreveross.cache.network.Netpath;
import com.foreveross.cache.network.upload.FilePart;
import com.foreveross.cache.network.upload.MultipartEntity;
import com.foreveross.cache.network.upload.Part;
import com.foreveross.cache.network.upload.StringPart;
import com.foreveross.cache.utility.NetworkHelpers;
import com.foreveross.cache.utility.Utility;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.data.Config;
import com.lenovo.sdimobileclient.utility.Utils;

/**
 * 缓存之网络模块
 * 
 * @author zhangshaofang
 * 
 */
public class SimpleDownloader extends AbsDownloader implements Constants {
	private static final String LOG_TAG = "Cache.SimpleDownloader";
	private static final int TIME_OUT = 30000;
	private Context mContext;

	public SimpleDownloader(Context context, AbsConfiguration config) {
		super(context, config);
		mContext = context;
	}

	@Override
	public FileInfo downloadFile(NetworkPath path, File outputFile) {
		if (!NetworkHelpers.isNetworkAvailable(mContext)) {
			if (Constants.DEBUG) {
				Log.d(LOG_TAG, "Network is not available. not start download " + path);
			}
			FileInfo fileInfo = new FileInfo(outputFile, path.url, path.postValues, "Network is not available");
			return fileInfo;
		}
		if (Constants.DEBUG) {
			Log.d(LOG_TAG, "Download file from " + path);
		}
		Netpath netpath = (Netpath) path;
		Config config = Utils.getSystemConfig(mContext);
		Uri uri = Uri.parse(netpath.url);
		String sourceFlag = uri.getQueryParameter(PARAM_SOURCEFLAG);
		String token = uri.getQueryParameter(PARAM_TOKEN);
		String url = netpath.url;
		if (TextUtils.isEmpty(sourceFlag)) {
			uri = uri.buildUpon().appendQueryParameter(PARAM_SOURCEFLAG, SOURCEFLAG).build();
			url = uri.toString();
		}

		if (TextUtils.isEmpty(token) && config != null) {
			token = Utils.getToken(mContext);

			System.out.println("token" + token);
			uri = uri.buildUpon().appendQueryParameter(PARAM_TOKEN, token).build();
			url = uri.toString();
		}
		return simpleDownload(url, netpath.postValues, netpath.type, netpath.extraData, outputFile);
	}

	private FileInfo simpleDownload(String urlStr, List<NameValuePair> postValues, int type, Object extraData, File outputFile) {
		FileInfo fileInfo = new FileInfo(outputFile, urlStr, postValues, null);
		if (Constants.DEBUG) {
			Log.i(LOG_TAG, urlStr);
		}
		InputStream in = null;
		OutputStream out = null;
		FilePair extraFile = null;
		ArrayList<FilePair> mFileList = null;
		File file = null;

		HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIME_OUT);
		try {
			HttpUriRequest request = null;
			if (extraData != null && extraData instanceof FilePair) {
				extraFile = (FilePair) extraData;
			}
			if (extraData != null && extraData instanceof ArrayList) {
				mFileList = (ArrayList<FilePair>) extraData;
			}
			if (postValues == null && extraFile == null && mFileList == null) {
				HttpGet getter = new HttpGet(urlStr);
				request = getter;
			} else {
				HttpEntity entity = null;
				if (extraFile == null && mFileList == null) {
					entity = new UrlEncodedFormEntity(postValues, HTTP.UTF_8);
				} else {
					ArrayList<Part> list = new ArrayList<Part>();
					String fileType = "image/jpg";
					if (type == 110) {
						fileType = "vedio/*";
					}
					if (mFileList != null) {
						for (int i = 0; i < mFileList.size(); i++) {
							FilePair f = mFileList.get(i);
							list.add(new FilePart(f.key, f.file, fileType, HTTP.UTF_8));
						}
					} else {
						list.add(new FilePart(extraFile.key, extraFile.file, fileType, HTTP.UTF_8));
					}
					if (postValues != null) {
						for (NameValuePair value : postValues) {
							list.add(new StringPart(value.getName(), value.getValue(), HTTP.UTF_8));
						}
					}

					Part[] parts = new Part[list.size()];
					entity = new MultipartEntity(list.toArray(parts));
				}

				HttpPost poster = new HttpPost(urlStr);
				poster.addHeader(entity.getContentType());
				poster.setEntity(entity);
				request = poster;
			}
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = null;
			if (response != null) {
				entity = response.getEntity();
			}
			if (entity != null) {
				in = entity.getContent();
			}
			if (in != null) {
				file = outputFile;
				out = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			}
			InputStream is = new FileInputStream(file);
			String json = new String(Utility.read(is));
			Log.e("json", json);
			try {
				JSONObject jsonObject = new JSONObject(json);
			} catch (Exception e) {
				fileInfo.exception = json;
			}
		} catch (Throwable e) {
			fileInfo.exception = e.toString();
			Log.e(LOG_TAG, "Failed download file from URL=" + urlStr, e);
			if (file != null) {
				file.delete();
			}
		} finally {
			try {
				out.close();
			} catch (Throwable t) {
				// ignore
			}
			try {
				in.close();
			} catch (Throwable t) {
				// ignore
			}
		}
		return fileInfo;
	}

}