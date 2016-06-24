package com.lenovo.sdimobileclient.api;

import java.io.File;
import java.io.FileInputStream;

import android.graphics.drawable.Drawable;

import com.foreveross.cache.CacheParams;
import com.foreveross.cache.ICacheInfo;
import com.foreveross.cache.network.FileInfo;

public abstract class CacheResult implements ICacheInfo
{
	  /**
     * 缓存时间
     */
    private long cache_time = 24 * 60 * 60 * 1000;
    
    public long getExpiry() {
        return cache_time;
    }

    public int getHandleMode() {
        return CacheParams.MODE_FLAG_FORCE_REFRESH;
    }
/**
 * 二级制流解析
 * @author zhangshaofang
 *
 */
    public static class BinaryResult extends CacheResult
    {
        protected final File mFile;
        protected Object     mData;
        protected boolean    mCorrect;

        public BinaryResult(File file) {
            mFile = file;
            if (isCorrect()) {
                mData = file;
                mCorrect = true;
            }
        }

        protected boolean isCorrect() {
            return mFile != null || mFile.exists();
        }

        public boolean isCorrectData() {
            return mCorrect;
        }

        public boolean isErrorData() {
            return false;
        }

        public Object getData() {
            return mData;
        }

        public boolean isCache() {
            return false;
        }

        @Override
        public FileInfo getFileInfo() {
            return null;
        }

		@Override
		public boolean isSuccessData() {
			return true;
		}
    }
/**
 * 图片解析
 * @author zhangshaofang
 *
 */
    public static class ImageResult extends BinaryResult
    {

        public ImageResult(File file) {
            super(file);
        }

        @Override
        public boolean isCorrect() {
            boolean result = super.isCorrect();
            if (!result) {
                return result;
            }
            FileInputStream in = null;
            try {
                in = new FileInputStream(mFile);
                Drawable.createFromStream(in, null);
            } catch (Exception e) {
                result = false;
            } finally {
                try {
                    in.close();
                } catch (Exception e2) {
                    // TODO: handle exception
                }
            }
            return result;
        }

        @Override
        public long getExpiry() {
            return 1000 * 60 * 60 * 24 * 14;
        }

        @Override
        public int getHandleMode() {
            return CacheParams.MODE_FLAG_EXPIRED_FIRST | CacheParams.MODE_FLAG_FORCE_CALLBACK;
        }
    }

/**
 * webview 网页解析
 * @author zhangshaofang
 *
 */
    public static class WebViewResult extends ImageResult
    {

        public WebViewResult(File file) {
            super(file);
        }
    }
}
