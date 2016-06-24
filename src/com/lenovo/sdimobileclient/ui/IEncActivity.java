package com.lenovo.sdimobileclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.foreveross.cache.CacheManager.Callback;
import com.foreveross.cache.CacheParams;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.api.Error;
/**
 * Activity 父类接口，封装常用方法
 * @author zhangshaofang
 *
 */
public interface IEncActivity extends View.OnClickListener, Constants
{

	/**
	 * 从map中获取对象
	 * @param key
	 * @param remove 是否删除
	 * @return
	 */
    <T> T getTempData(String key, boolean remove);
	/**
	 * map中添加对象
	 * @param key
	 * @param data
	 */
    void addTempData(String key, Object data);
    void finish();

    Context getApplicationContext();

    void showDialog(int id);

    void errorData(Error error);
    Dialog onCreateDialog(int id);

    void onPrepareDialog(int id, Dialog dialog);

    LenovoServicesApplication getAppliction();

    Activity getParent();
    /**
     * 错误时，数据返回通知
     * @param id
     * @param params
     * @param callback
     */
    void notifyReloadByErrDlg(int id, CacheParams params, Callback callback);

}
