package com.lenovo.sdimobileclient.api;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 工单信息
 */
public class OrderInfo extends AbsApiData implements Serializable {

	/**
	 * 工单编号
	 */
	public String OrderID;
	/**
	 * 订单类型名称
	 */

	public String OrderTypeName;
	public String OrderTypeID;
	public String ContactPhone;
	public String P_OrderID;
	public String N_OrderID;
	/**
	 * 是否是列表里面的数据 默认是true
	 */
	public boolean IsOrderList = true;

	public List<CustomerPhone> CustomerPhones;

	/**
	 * 工单子类型
	 */
	public String ServiceName;

	/**
	 * 故障描述
	 */
	public String FailureDescription;

	/**
	 * 订单状态名称
	 */
	public String OrderStateName;
	public String OrderStateID;
	public String FaultDescription;
	public String Solution;
	public String SOType;
	/**
	 * 产品序列号/主机号
	 */
	public String ProductSN;
	/**
	 * 产品型号
	 */
	public String ProductType;
	/**
	 * 客户姓名
	 */
	public String CustomerName;
	/**
	 * 客户企业
	 */
	public String CompanyName;

	public String CurrentTaskCode;
	public String LastSOTaskID;

	/**
	 * 撤销需要的工单变好
	 */
	public String SOTaskID;

	/**
	 * 填写说明
	 */
	public String TaskDescriptions;

	/**
	 * 是否callback
	 */
	public String CallBack;
	/**
	 * T1时间
	 */
	public String Time1;
	/**
	 * T4时间
	 */
	public String Time4;
	/**
	 * 备件整体预约状态
	 */
	public String PartsStatus;
	/**
	 * 历史工单
	 */
	public List SOListHist;

	/**
	 * 是否ＶＩＰ
	 */
	public String IsVIP;
	/**
	 * 预约上门时间
	 */
	public String PreTime;
	/**
	 * 客户工单备注
	 */
	public String Remark;
	/**
	 * 附件数量
	 */
	public int AttachCount;

	/**
	 * 本次派单数量
	 */
	public String CurrentNum;
	/**
	 * 历史维修次数
	 */
	public int RepairTimes;
	/**
	 * 客户级别
	 */
	public String CustomerLevel;
	/**
	 * 客户Ｅｍａｉｌ
	 */
	public String CustomerEmail;
	/**
	 * 客户地址
	 */
	public String CustomerAddress;

	public OrderInfo() {

	}

	public OrderInfo(JSONObject jsonObject) {
		try {
			parser(jsonObject);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
