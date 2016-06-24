
package com.lenovo.sdimobileclient.network;

/**
 * 服务器接口地址及参数名
 * 
 * @author zhangshaofang
 * 
 */
public interface UrlAttr {

	/**
	 * 基地址
	 */
	// String HOST = "http://219.142.122.168:84/SDIMobile/";
	// String HOST = "http://192.168.0.123:808/SDIMobile/";
	// String HOST = "http://10.118.6.112:3638/SDIMobile/";

	/**
	 * 
	 * 正式
	 */
	// String HOST = "http://esdmobile.lenovo.com.cn:83/SDIMobile/";

	/**
	 * 测试
	 */

	// String HOST = "http://sdiuat.lenovo.com.cn:105/SDIMobile/";
	String HOST = "http://esdmobile.lenovo.com.cn/SDIMobile/";
	// String HOST = "http://10.118.0.203:3836/Sdimobile/";

	/**
	 * 登录
	 */
	String URL_LOGIN = HOST + "User/Login.ashx";
	/**
	 * 登出
	 */
	String URL_LOGOUT = HOST + "User/Logout.ashx";
	/**
	 * 上传时间提醒间隔
	 */
	String URL_PUSHMSG_TIME = HOST + "Order/AddPushConfig.ashx";
	/**
	 * 获取系统参数
	 */
	String URL_SYSTEM_PARAMS = HOST + "System/GetSysParams.ashx";

	/**
	 * 工单列表
	 */
	String URL_ORDERLIST = HOST + "Order/GetOrderList.ashx";
	/**
	 * 工单撤销
	 */
	String URL_REVOKETASK = HOST + "Order/RevokeTask.ashx";
	/**
	 * 
	 * 备件查询
	 */
	String URL_SPARELIST = HOST + "Order/GetBorrowOrderInfoList.ashx";

	/**
	 * 根据主机号查询历史工单
	 */
	String URL_OLDORDERLIST = HOST + "Order/GetLSOrderList.ashx";

	/**
	 * 根据订单号 客户姓名 查询工单
	 */
	String URL_ALLORDERLIST = HOST + "Order/GetAlllOrderList.ashx";
	/**
	 * 查询押款价
	 */
	String URL_GETYAKUANPRICE = HOST + "Order/GetYakuanPrice.ashx";

	/**
	 * 获取微信QRcode
	 */
	String URL_GETQRCODE = HOST + "Order/GetQRCode.ashx";

	/**
	 * 非MA任务获取
	 */
	String URL_ORDERTASKSOURCES = HOST + "Order/OrderTaskSources.ashx";
	/**
	 * 非MA任务提交
	 */
	String URL_ORDERTASKPROCESS = HOST + "Order/OrderTaskProcess.ashx";
	/**
	 * 联系结果
	 */
	String URL_PUTCONTACTRESULT = HOST + "Order/PutContactResult.ashx";
	/**
	 * 补发工单
	 */
	String URL_SENDORDER = HOST + "Order/SendOrder.ashx";
	/**
	 * 验证主机
	 */
	String URL_VERIFYMACHINE = HOST + "Order/VerifyMachine.ashx";
	/**
	 * 到达现场签到
	 */
	String URL_SIGNARRIVED = HOST + "Order/SignArrived.ashx";

	/**
	 * 登记维修结果
	 */
	String URL_CONFIRMRESULT = HOST + "Order/ConfirmResult.ashx";
	/**
	 * 服务中
	 */
	String URL_CONFIRMSERVEING = HOST + "Order/PutMaintenance.ashx";
	/**
	 * 服务完
	 */
	String URL_COMPLETEORDER = HOST + "Order/CompleteOrder.ashx";
	/**
	 * 收费金额
	 */
	String URL_CONFIRMLEAVE = HOST + "Order/ConfirmLeave.ashx";
	/**
	 * 非换件信息读取
	 */
	String URL_UNCHANGE_SOURCES = HOST + "Order/Reservation/Unchange_Sources.ashx";
	/**
	 * 换件信息读取
	 */
	String URL_CHANGE_SOURCES = HOST + "Order/Reservation/Change_Sources.ashx";
	/**
	 * 历史换件信息读取
	 */
	String URL_CHANGE_HISTORIES = HOST + "Order/Reservation/Change_Histories.ashx";
	/**
	 * 换shang件信息读取
	 */

	String URL_CHANGE_MESSAGE = HOST + "Order/Reservation/Change_Message.ashx";
	/**
	 * 非换件信息添加
	 */
	String URL_UNCHANGE_ADD = HOST + "Order/Reservation/Unchange_Add.ashx";
	/**
	 * 换件信息添加
	 */
	String URL_CHANGE_ADD = HOST + "Order/Reservation/Change_Add.ashx";

	String URL_UNCHANGE_DELETE = HOST + "Order/Reservation/Unchange_Delete.ashx";
	String URL_CHANGE_DELETE = HOST + "Order/Reservation/Change_Delete.ashx";
	/**
	 * 工单详情
	 */
	String URL_ORDERINFO = HOST + "Order/GetOrderDetail.ashx";
	/**
	 * 查询技术通报和查询其他文章
	 */
	String URL_SEARCHOTHER = HOST + "Knowledge/GetQualityIssuesInfo.ashx";
	/**
	 * 技术通报和其他文章香型
	 */
	String URL_KNOWINFODETAIL = HOST + "Knowledge/View/index.html";
	/**
	 * 查询主机及保修信息
	 * 
	 */
	String URL_SEARCHHOST = HOST + "Product/GetProductInfo.ashx";
	/**
	 * 查询物料（装箱单）信息
	 */
	String URL_SEARCHBOX = HOST + "Product/GetMaterialInfo.ashx";
	/**
	 * 坐标上传
	 */
	String URL_UPLOADLOCATION = HOST + "LBS/UploadLocation.ashx";
	/**
	 * 附件上传
	 * 
	 */
	String URL_UPLOADATTACH = HOST + "Attach/UploadAttachment.ashx";

	// ------------------分割线--------------------------------

	/**
	 * 用户名
	 */
	String PARAM_USERNAME = "UserName";
	/**
	 * 密码
	 */
	String PARAM_PASSWORD = "Password";
	/**
	 * 客户端来源
	 */
	String PARAM_SOURCEFLAG = "SourceFlag";
	/**
	 * 验证信息
	 */
	String PARAM_TOKEN = "Token";
	/**
	 * 技术通报Id
	 */
	String PARAM_KEYCODE = "KeyCode";
	/**
	 * 设备名称
	 */
	String PARAM_DRIVERNAME = "DriverName";
	/**
	 * 设备唯一标示
	 */
	String PARAM_IMEI = "IMEI";
	/**
	 * 屏幕宽度
	 */
	String PARAM_SCREEN_WIDTH = "ScreenWidth";
	/**
	 * 屏幕高度
	 */
	String PARAM_SCREEN_HEIGHT = "ScreenHeight";
	/**
	 * 像素密度
	 */
	String PARAM_SCREEN_DPI = "Dpi";
	/**
	 * 搜索类型
	 */
	String PARAM_SEARCHTYPE = "SearchType";
	/**
	 * 每页返回数据数量
	 */
	String PARAM_PAGECOUNT = "PageCount";
	/**
	 * 操作系统版本
	 */
	String PARAM_SDK = "OSVersion";
	/**
	 * 应用版本号
	 */
	String PARAM_APK_VERSION = "APKVersion";
	/**
	 * 工单状态
	 */
	String PARAM_ORDERSTATE = "OrderState";
	/**
	 * 产品序列号
	 */
	String PARAM_PRODUCTNO = "ProductNO";
	/**
	 * 知识 查询内容
	 */
	String PARAM_QUERYCONTENT = "QueryContent";
	/**
	 * 工单 查询内容
	 */
	String PARAM_QUERYCONDITION = "condition";
	/**
	 * 页码
	 */
	String PARAM_PAGEINDEX = "PageIndex";
	/**
	 * 查询类型
	 */
	String PARAM_QUERYTYPE = "QueryType";
	/**
	 * 工单id
	 */
	String PARAM_ORDERID = "OrderID";
	String PARAM_UNCHANGEINFOID = "UnchangeInfoID";
	String PARAM_CHANGEINFOID = "ChangeRecID";
	/**
	 * 附件名称
	 */
	String PARAM_FILENAME = "FileName";
	/**
	 * 坐标
	 */
	String PARAM_LOCATION = "Location";
	/**
	 * 位置
	 */
	String PARAM_ONSITEPOSITION = "OnsitePosition";
	/**
	 * 位置
	 */
	String PARAM_ADDRESS = "OnsiteAddress";
	/**
	 * 偏差
	 */
	String PARAM_DISTENCE = "Instance";
	/**
	 * 工程师Id
	 *
	 */
	String PARAM_ENGINEER = "Engineer";
	String PARAM_SPTYPES = "SPTypes";
	String PARAM_ACTIONS = "Actions";
	String PARAM_REPAIRRESULT = "RepairResult";
	String PARAM_PRODUCTSN = "ProductSN";
	String PARAM_CONTACTRESULT = "ContactResult";
	String PARAM_TASKID = "TaskID";
	String PARAM_PRETIME = "PreTime";
	String PARAM_REASON = "Reason";
	String PARAM_REMARK = "Remark";
	String PARAM_TYPES = "Types";
	String PARAM_TYPE = "Type";
	String PARAM_CATEGORY = "Category";
	String PARAM_VALUES = "Values";
	/**
	 * 图片
	 */
	String PARAM_IMAGEDATA = "ImageData";

}
