/**
 * 
 */
package com.lenovo.sdimobileclient;

import com.lenovo.sdimobileclient.network.UrlAttr;

/**
 * @ClassName: Constants
 * @author ZhangShaofang
 * @Description: TODO
 */
public interface Constants extends UrlAttr {
	/**
	 * debug 调试开关 上线需改为false
	 */
	boolean DEBUG = false;
	/**
	 * source flag 上线需改为正式
	 */
	String SOURCEFLAG = "MOBILE";
	/**
	 * 最小密码长度
	 */
	int PWD_MIN_LENGTH = 8;
	/**
	 * 百度地图key
	 */
	String MAP_KEY = "A7820271d5365789691ba991db2afc37";
	String MIMETYPE_APK = "application/vnd.android.package-archive";
	/**
	 * 包名
	 */
	String PACKAGE_NAME = "com.lenovo.sdimobileclient";
	/**
	 * 默认图片 key
	 */
	String DEFAULT_ITEM = "default_item";
	/**
	 * log tag
	 */
	String LOG_TAG = "LOG_TAG";
	/**
	 * 网络错误 dialog Id
	 */
	int DLG_NETWORK_ERROR = 10001;
	/**
	 * 数据加载dialog Id
	 */
	int DLG_DATA_LOADING = 10002;
	/**
	 * 数据同步dialog Id
	 */
	int DLG_SYS_LOADING = 10003;
	/**
	 * 等待dialog Id
	 */
	int DLG_LOADING = 10004;
	/**
	 * 数据发送dialog Id
	 */
	/**
	 * 二级复选框一级弹出按钮
	 */
	int DLG_SOURCECHECK1 = 7001;
	/**
	 * 二级复选框二级级弹出按钮
	 */
	int DLG_SOURCECHECK2 = 7002;
	int DLG_SENDING = 10005;
	int DLG_LOGOUT = 10006;
	int DLG_CHECKHOST = 10007;
	int DLG_UNSAVE = 10008;
	String UNKNOW_DEVICE_ID = "66666666666666666666";
	String EXTRA_TAG = "tag";
	String EXTRA_IMAGE_DATA = "data";
	String EXTRA_INTENT = "intent";
	int REQUEST_CODE_GET_IMAGE = 1001;
	int PAGE_COUNT = 10;
	String PREF_ENGINEERINFO = "engineerinfo";
	String PREF_SYSTEM_KEY = "system_key";
	String PREF_SYSTEM_TIME = "system_time";
	String PREF_CLIENT_TIME = "client_time";
	String PREF_TIP_SPIT = "PREF_TIP_SPIT";
	String PREF_TIP_BELL = "PREF_TIP_BELL";
	String PREF_TIP_SHOCK = "PREF_TIP_SHOCK";
	/**
	 * image file max size
	 */
	int MAX_WIDTH = 1280;
	int MAX_HEIGHT = 1280;
	int FILE_SIZE = 1024 * 1024 * 5;

	/**
	 * 工单--待联系 60
	 */
	int ORDER_STATE_UNCONTACT = 60;
	/**
	 * 工单--处理中 0
	 */
	int ORDER_STATE_UNFINISH = 0;
	/**
	 * 工单--已联系 65
	 */
	int ORDER_STATE_CONTACTED = 65;
	/**
	 * 工单--上门中 70
	 */
	int ORDER_STATE_DOORING = 70;
	/**
	 * 服务中 80
	 */
	int ORDER_STATE_SERVEING = 80;
	/**
	 * 工单--待完成 190
	 */
	int ORDER_STATE_TOBEFINISHED = 190;
	/**
	 * 工单--已完成 200
	 */
	int ORDER_STATE_FINISH = 200;
	/**
	 * 工单--已完成 1
	 */
	int ORDER_STATE_FINISHED = 1;

}
