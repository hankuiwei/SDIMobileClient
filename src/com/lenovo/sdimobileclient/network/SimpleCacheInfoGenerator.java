package com.lenovo.sdimobileclient.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.foreveross.cache.CacheInfoGenerator;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.NetworkPath;
import com.foreveross.cache.network.Netpath;
import com.foreveross.cache.utility.Utility;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.api.CacheResult.BinaryResult;
import com.lenovo.sdimobileclient.api.CacheResult.ImageResult;
import com.lenovo.sdimobileclient.api.CacheResult.WebViewResult;
import com.lenovo.sdimobileclient.api.JsonDataFactory;
import com.lenovo.sdimobileclient.api.RootData;
/**
 * 缓存之数据解析配置
 * @author zhangshaofang
 *
 */
public class SimpleCacheInfoGenerator implements CacheInfoGenerator
{
    public static final String LOG_TAG = "SimpleCacheInfoGenerator";
    public ICacheInfo getCacheInfo(NetworkPath netPath, File file) {
        ICacheInfo result = null;
        try {
            Netpath path = (Netpath) netPath;
            switch (path.type) {
                case Netpath.TYPE_WEBVIEW:
                case Netpath.TYPE_IMAGE:
                case Netpath.TYPE_BINARY:
                    try {
                        result = parserAPIData(file, true);
                    } catch (Exception e) {
                        // ignore
                    }
                    if (result == null) {
                        if (path.type == Netpath.TYPE_IMAGE) {
                            result = new ImageResult(file);
                        } else if (path.type == Netpath.TYPE_BINARY) {
                            result = new BinaryResult(file);
                        } else if (path.type == Netpath.TYPE_WEBVIEW) {
                            result = new WebViewResult(file);
                        }
                    }

                    break;
                default:
                    result = parserAPIData(file, false);
                    break;
            }

        } catch (Throwable e) {
            if(Constants.DEBUG)
            {
                Log.w(LOG_TAG, "Meet exception when generate CacheInfo.", e);
                Log.w(LOG_TAG, "Return Unknow Error.");
            }
            result = null;
        }
        return result;
    }

    private RootData parserAPIData(File file, boolean inTry) throws JSONException, IllegalArgumentException, InstantiationException, IllegalAccessException, IOException {
        InputStream is = new FileInputStream(file);
        String json = new String(Utility.read(is));
       Log.e("json", json);
            if (inTry) {
                JSONObject jsonObject = new JSONObject(json);
                return JsonDataFactory.getDataOrThrow(jsonObject);
            } else {
                JSONObject jsonObject = new JSONObject(json);

                return JsonDataFactory.getData(jsonObject);
            }
    }
}
