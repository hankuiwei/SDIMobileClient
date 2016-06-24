
package com.lenovo.sdimobileclient.network;

import android.content.Context;

import com.foreveross.cache.AbsConfiguration;
import com.foreveross.cache.AbsDownloader;
import com.foreveross.cache.CacheInfoGenerator;
/**
 * 缓存配置
 * @author zhangshaofang
 *
 */
public class SimpleConfiguration extends AbsConfiguration {
	private AbsDownloader downloader;
	public SimpleConfiguration(Context context) {
		super(context);
		downloader = new SimpleDownloader(context,this);
	}
	@Override
	public CacheInfoGenerator getCacheInfoGenerator() {
		return new SimpleCacheInfoGenerator();
	}

	@Override
	public AbsDownloader getDownloader() {
		return downloader;
	}

}
