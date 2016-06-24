package com.lenovo.sdimobileclient.api;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderOperations extends AbsApiData {

	// 审批清单（发票）上传
	public boolean Attachment_Invoice_Upload;
	// 附件上传
	public boolean Attachment_Normal_Upload;
	// 换件信息添加
	public boolean Reservation_Change_Add;
	// 非换件信息添加
	public boolean Reservation_Unchange_Add;

	// 附件删除
	public boolean Attachment_Normal_Delete;
	// 附件查看
	public boolean Attachment_Normal_View;
	// 换件非换件查看
	public boolean ReservationRecord_View;
	// 备件信息添加
	public boolean Reservation_Subscribe_Add;
	// 工单任务审批
	public boolean Task_Approve;
	// 工单服务完
	public boolean Task_ServiceComplete;

	public OrderOperations() {
		super();
	}

	public OrderOperations(String json) {
		try {
			parser(new JSONObject(json));
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
