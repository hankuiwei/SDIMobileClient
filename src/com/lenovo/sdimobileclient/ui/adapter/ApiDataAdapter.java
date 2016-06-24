
package com.lenovo.sdimobileclient.ui.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.foreveross.cache.network.FilePair;
import com.lenovo.sdimobileclient.Constants;
import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.R;
import com.lenovo.sdimobileclient.api.BorrowOrderInfo;
import com.lenovo.sdimobileclient.api.Change;
import com.lenovo.sdimobileclient.api.ChangeHistory;
import com.lenovo.sdimobileclient.api.KnowledgeInfo;
import com.lenovo.sdimobileclient.api.Material;
import com.lenovo.sdimobileclient.api.OrderInfo;
import com.lenovo.sdimobileclient.api.ProductBox;
import com.lenovo.sdimobileclient.api.SearchCategory;
import com.lenovo.sdimobileclient.api.SourceOption;
import com.lenovo.sdimobileclient.api.UserAccount;
import com.lenovo.sdimobileclient.data.Account;
import com.lenovo.sdimobileclient.data.AlarmAlert;
import com.lenovo.sdimobileclient.data.Attach;
import com.lenovo.sdimobileclient.network.OkHttpHelper;
import com.lenovo.sdimobileclient.network.OkHttpStringCallback;
import com.lenovo.sdimobileclient.ui.AttachUploadActivity;
import com.lenovo.sdimobileclient.ui.ImageActivity;
import com.lenovo.sdimobileclient.ui.OrderActivity;
import com.lenovo.sdimobileclient.ui.OrderActivity.DateListener;
import com.lenovo.sdimobileclient.ui.TabGroup;
import com.lenovo.sdimobileclient.ui.adapter.bean.ComeTypes;
import com.lenovo.sdimobileclient.ui.view.DateTimePickDialogUtil;
import com.lenovo.sdimobileclient.ui.view.DialogListener;
import com.lenovo.sdimobileclient.ui.view.WritePadDialog;
import com.lenovo.sdimobileclient.utility.AlarmTask;
import com.lenovo.sdimobileclient.utility.FileUtil;
import com.lenovo.sdimobileclient.utility.Utils;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 列表适配器
 * 
 * @author zhangshaofang
 * 
 * @param <AbsApiData>
 */
public class ApiDataAdapter<AbsApiData> extends BasicArrayAdapter<AbsApiData> implements Constants {
	/**
	 * 用户账号
	 */
	private static final int VIEWTYPE_USERACCOUNT = 1;
	/**
	 * 工单
	 */
	private static final int VIEWTYPE_ORDER = 2;

	/**
	 * 产品工单详情
	 */
	private static final int VIEWTYPE_PRODUCT_ORDER_DETAIL = 17;

	/**
	 * 客户工单详情
	 */
	private static final int VIEWTYPE_CUSTOMER_ORDER_DETAIL = 18;
	/**
	 * 提醒
	 */
	private static final int VIEWTYPE_ALERT = 3;
	/**
	 * 装箱单信息
	 */
	private static final int VIEWTYPE_HOST = 4;
	/**
	 * 附件
	 */
	private static final int VIEWTYPE_ATTACHMENT = 5;
	/**
	 * 技术通报，其他文章
	 */
	private static final int VIEWTYPE_KNOWLEDGEINFO = 6;
	/**
	 * 装箱单分类
	 */
	private static final int VIEWTYPE_HOST_EDIT = 7;
	private static final int VIEWTYPE_SEARCHCATEGORY = 8;
	private static final int VIEWTYPE_REPLACE_BOX = 9;

	/**
	 * 判断工单页面下面条目
	 */

	/**
	 * 文本
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_EDITTEXT = 10;

	/**
	 * 下拉单选
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_CHECKBOX = 11;

	/**
	 * 选择日期和原因
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_DATERESON = 12;
	/**
	 * 客户签字
	 * 
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_WRITENAME = 13;

	/**
	 * 验证工单
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_TESTORDER = 14;
	/**
	 * 选择日期
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_DATE = 15;
	/**
	 * 选择日期
	 */
	private static final int VIEWTYPE_ORDER_RADCOMBOX2 = 16;
	/**
	 * 处理工单标题
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_TITLE = 501;
	/**
	 * 工单备注
	 */
	private static final int VIEWTYPE_ORDER_SOURSE_REMARK = 502;
	/**
	 * 备件查询
	 */
	private static final int VIWETYPE_BRROWSPARE = 503;
	/**
	 * 历史工单
	 */
	private static final int VIWETYPE_CHANGEHISTORY = 504;

	public ApiDataAdapter(Context context) {
		super(context);
	}

	/**
	 * 编辑状态
	 */
	private boolean mEdit;
	private EditText mAmount;
	private View mBtnCheckHost;
	private TextView mContactedBarCodeTv;
	private EditText mFaultResult;
	private EditText mHostNum;
	private ComeTypes mFaultdata;
	private ComeTypes mHostNumdata;
	private ComeTypes mAmountdata;
	private TextView mTaskdesc;
	private OnLongClickListener mOnlongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {

			TextView textView = (TextView) v;

			ClipboardManager cm = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);

			cm.setText(textView.getText().toString());

			Utils.showToast(mContext, "内容\"" + textView.getText().toString() + "\"已复制到剪贴板");

			return false;
		}
	};

	public ApiDataAdapter(Context context, boolean edit) {
		super(context);
		mEdit = edit;

		mSelectData = new ArrayList<AbsApiData>();
	}

	public void setEdit(boolean edit) {
		mEdit = edit;
	}

	/**
	 * 初始化item页面
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		// 取出数据

		AbsApiData data = getItem(position);

		// 配置数据页面类型
		int viewType = getItemViewType(data);
		int viewResId = getViewResId(viewType);

		// 当convertview != null 并且 当前的布局适合现在要显示数据的布局
		if (convertView != null && viewResId == mLastviewResId) {
			// 配置数据页面类型
			view = convertView;
		} else {

			view = getItemView(parent, viewType, true);
		}
		// 配置数据页面类型
		fillItemView(view, viewType, data, position);
		mLastviewResId = viewResId;
		return view;
	}

	public View getItemView(AbsApiData data) {
		int viewType = getItemViewType(data);
		View view = getItemView(null, viewType, true);
		fillItemView(view, viewType, data, 0);
		return view;
	}

	/**
	 * 初始化数据界面
	 * 
	 * @param view
	 *            页面
	 * @param type
	 *            页面类型
	 * @param data
	 *            数据实体
	 * @param position
	 *            位置
	 */
	private void fillItemView(View view, int type, AbsApiData data, int position) {
		@SuppressWarnings("unchecked")
		SparseArray<View> viewMap = (SparseArray<View>) view.getTag();
		int id = getViewResId(type);
		switch (id) {
		case R.layout.user_account: {
			UserAccount account = (UserAccount) data;
			TextView nameTv = (TextView) viewMap.get(R.id.username);
			nameTv.setText(account.username);
			viewMap.get(R.id.btn_delete).setTag(account);
		}
			break;
		case R.layout.order_card_item: {
			OrderInfo order = (OrderInfo) data;
			TextView codeTv = (TextView) viewMap.get(R.id.order_code);
			// TextView hostTv = (TextView) viewMap.get(R.id.order_host);
			TextView typeTv = (TextView) viewMap.get(R.id.order_type);
			TextView hostCodeTv = (TextView) viewMap.get(R.id.order_host_code);
			TextView customerTv = (TextView) viewMap.get(R.id.order_customer);
			TextView orderStateTv = (TextView) viewMap.get(R.id.order_state);
			TextView orderPhoneTv = (TextView) viewMap.get(R.id.order_customer_phone);
			TextView orderCompanyTv = (TextView) viewMap.get(R.id.order_customer_company);
			TextView orderAddressTv = (TextView) viewMap.get(R.id.order_customer_address);

			orderPhoneTv.setText(order.ContactPhone);
			orderCompanyTv.setText(order.CompanyName);
			orderAddressTv.setText(order.CustomerAddress);

			orderCompanyTv.setVisibility(TextUtils.isEmpty(order.CompanyName) ? View.GONE : View.VISIBLE);
			mCallPhone = (TextView) viewMap.get(R.id.order_callphone);
			mCallPhone.setTag(order.ContactPhone);
			viewMap.get(R.id.order_routeplan).setTag(order.CustomerAddress);
			codeTv.setText(order.OrderID);

			// hostTv.setText(order.ProductSN);
			String stateName = order.OrderStateName;
			if (!TextUtils.isEmpty(order.OrderStateID)) {
				int typeId = Integer.parseInt(order.OrderStateID);
				switch (typeId) {
				case 60:
					stateName = "待联系";
					break;
				case 65:
					stateName = "已联系";

					break;
				case 70:
					stateName = "上门中";
					break;
				case 190:
					stateName = "待完成";
					break;
				case 200:
					stateName = "服务完";
					break;
				default:
					break;
				}
			}

			TextView dateTv = (TextView) viewMap.get(R.id.order_date);
			TextView timeTv = (TextView) viewMap.get(R.id.order_time);
			boolean f = (null != order.PreTime && !("".equals(order.PreTime)));
			if ((null != order.PreTime && !"".equals(order.PreTime)) && (TextUtils.equals("已联系", stateName) || TextUtils.equals("已分派", stateName))) {

				long dateFormatTimes = Utils.dateFormatTimes(order.PreTime);
				long currentTimeMillis = System.currentTimeMillis();

				long abs = dateFormatTimes - currentTimeMillis;

				if (abs < 1000 * 60 * 15) {
					codeTv.setTextColor(mContext.getResources().getColor(R.color.red));
					dateTv.setTextColor(mContext.getResources().getColor(R.color.red));
					timeTv.setTextColor(mContext.getResources().getColor(R.color.red));

				} else {

					codeTv.setTextColor(mContext.getResources().getColor(R.color.slidemenu_text));
					dateTv.setTextColor(mContext.getResources().getColor(R.color.slidemenu_text));
					timeTv.setTextColor(mContext.getResources().getColor(R.color.slidemenu_text));
				}

			} else {
				dateTv.setTextColor(mContext.getResources().getColor(R.color.slidemenu_text));
				timeTv.setTextColor(mContext.getResources().getColor(R.color.slidemenu_text));
				codeTv.setTextColor(mContext.getResources().getColor(R.color.slidemenu_text));

			}
			orderStateTv.setText(stateName);
			typeTv.setText(order.OrderTypeName);
			hostCodeTv.setText(order.ProductType);
			customerTv.setText(order.CustomerName);

			if (!TextUtils.isEmpty(order.PreTime)) {
				dateTv.setText(Utils.getDate(Utils.dateFormatTimes(order.PreTime)));
			} else {
				dateTv.setText("暂无");
			}

			if (!TextUtils.isEmpty(order.PreTime)) {
				timeTv.setText(Utils.getTime(Utils.dateFormatTimes(order.PreTime)));
			} else {
				timeTv.setText("");
			}
			// TextView countTv = (TextView) viewMap.get(R.id.order_count);
			// countTv.setText(String.valueOf(order.AttachCount));
			// TextView rapirTv = (TextView) viewMap.get(R.id.rapir_count);
			// rapirTv.setText(String.valueOf(order.RepairTimes));

		}
			break;
		case R.layout.order_detil_item: {
			OrderInfo order = (OrderInfo) data;
			// TextView codeTv = (TextView) viewMap.get(R.id.order_code);
			TextView hostTv = (TextView) viewMap.get(R.id.order_host);
			TextView typeTv = (TextView) viewMap.get(R.id.order_type);
			TextView hostfaulttv = (TextView) viewMap.get(R.id.order_host_fault);

			TextView subtypetv = (TextView) viewMap.get(R.id.order_subtype);
			TextView PartsStatus = (TextView) viewMap.get(R.id.order_PartsStatus);

			TextView hostCodeTv = (TextView) viewMap.get(R.id.order_host_code);
			// TextView customerTv = (TextView)
			// viewMap.get(R.id.order_customer);
			TextView orderStateTv = (TextView) viewMap.get(R.id.order_state);

			showAll = viewMap.get(R.id.btn_showall);

			// codeTv.setText(order.OrderID);
			hostfaulttv.setText(order.FailureDescription);
			subtypetv.setText(order.ServiceName);
			hostTv.setText(order.ProductSN);

			hostTv.setOnLongClickListener(mOnlongClickListener);
			PartsStatus.setText(order.PartsStatus);
			String stateName = order.OrderStateName;
			orderStateTv.setText(stateName);
			typeTv.setText(order.OrderTypeName);
			hostCodeTv.setText(order.ProductType);
			TextView dateTv = (TextView) viewMap.get(R.id.order_date);
			if (!TextUtils.isEmpty(order.PreTime)) {
				dateTv.setText(Utils.getDate(Utils.dateFormatTimes(order.PreTime)) + "  " + Utils.getTime(Utils.dateFormatTimes(order.PreTime)));
			} else {
				dateTv.setText("暂无");
			}
			if (order.AttachCount != 0) {
				TextView attchcount = (TextView) viewMap.get(R.id.order_count);
				attchcount.setText(order.AttachCount + "");
			}
			if (order.RepairTimes != 0) {
				TextView repair = (TextView) viewMap.get(R.id.rapir_count);
				repair.setText(order.RepairTimes + "");
			}

		}
			break;
		case R.layout.order_detil_customer_item: {
			OrderInfo order = (OrderInfo) data;

			TextView typeTv = (TextView) viewMap.get(R.id.order_type);
			TextView dateTv = (TextView) viewMap.get(R.id.order_date);
			TextView subtypetv = (TextView) viewMap.get(R.id.order_subtype);
			TextView paidancount = (TextView) viewMap.get(R.id.order_paidancount);
			// TextView hostCodeTv = (TextView)
			// viewMap.get(R.id.order_host_code);
			TextView order_remark = (TextView) viewMap.get(R.id.order_remark);
			TextView orderStateTv = (TextView) viewMap.get(R.id.order_state);
			order_remark.setText(order.Remark);
			subtypetv.setText(order.ServiceName);
			String stateName = order.OrderStateName;
			orderStateTv.setText(stateName);
			paidancount.setText(order.CurrentNum);
			typeTv.setText(order.OrderTypeName);
			if (!TextUtils.isEmpty(order.PreTime)) {
				dateTv.setText(Utils.getDate(Utils.dateFormatTimes(order.PreTime)) + "  " + Utils.getTime(Utils.dateFormatTimes(order.PreTime)));
			} else {
				dateTv.setText("暂无");
			}
			if (order.AttachCount != 0) {
				TextView attchcount = (TextView) viewMap.get(R.id.order_count);
				attchcount.setText(order.AttachCount + "");
			}
		}
			break;
		case R.layout.alert_item: {
			AlarmAlert alert = (AlarmAlert) data;
			TextView tipTypeTv = (TextView) viewMap.get(R.id.tip_type);
			String typeName = "系统通知";
			switch (alert.type) {
			case AlarmTask.ALARMTASK_TYPE_RESPONE_DUETO:
			case AlarmTask.ALARMTASK_TYPE_RESPONE_DUETO_NOW:
				typeName = "到期提醒";
				break;
			case AlarmTask.ALARMTASK_TYPE_RESPONE_BEYOND:
			case AlarmTask.ALARMTASK_TYPE_RESPONE_BEYOND_NOW:
				typeName = "超期提醒";
				break;
			default:
				typeName = "系统通知";
				break;
			}
			tipTypeTv.setText(typeName);
			viewMap.get(R.id.ic_bell).setVisibility(alert.bell == 0 ? View.GONE : View.VISIBLE);
			viewMap.get(R.id.ic_shock).setVisibility(alert.shock == 0 ? View.GONE : View.VISIBLE);
			TextView timeTv = (TextView) viewMap.get(R.id.tv_time);
			timeTv.setText(Utils.dateFormat(alert.timestamp));
			TextView msgTv = (TextView) viewMap.get(R.id.tip_text);
			msgTv.setText(alert.msg);
			View btnDelete = viewMap.get(R.id.btn_delete);
			btnDelete.setVisibility(mEdit ? View.VISIBLE : View.GONE);
			btnDelete.setTag(alert);

		}
			break;
		case R.layout.host_item: {
			Material host = (Material) data;
			TextView cateTv = (TextView) viewMap.get(R.id.cate_name);
			TextView numTv = (TextView) viewMap.get(R.id.part_num);
			TextView codeTv = (TextView) viewMap.get(R.id.part_code);
			TextView nameTv = (TextView) viewMap.get(R.id.part_name);
			// View spitView = viewMap.get(R.id.spit_view);
			// spitView.setBackgroundResource(host.spitvis ?
			// R.drawable.divider_list : R.drawable.line_spit);
			cateTv.setVisibility(host.spitvis ? View.VISIBLE : View.GONE);
			// spitView.setVisibility(host.spitvis ? View.VISIBLE : View.GONE);
			cateTv.setText(host.MaterialCategory);
			numTv.setText(host.MaterialID);
			codeTv.setText(host.MaterialCode);
			nameTv.setText(host.MaterialName);
		}
			break;
		case R.layout.attachment: {
			Attach attachMent = (Attach) data;
			ImageView iconIv = (ImageView) viewMap.get(android.R.id.icon);
			if (attachMent.itype != 1) {
				if (attachMent.fileType == Attach.FILETYPE_VIDEO) {
					ContentResolver cr = mContext.getContentResolver();
					Bitmap bitmap = AttachUploadActivity.getVideoThumbnail(cr, Uri.parse(attachMent.filepath));
					iconIv.setImageBitmap(bitmap);
					iconIv.setVisibility(View.VISIBLE);
				} else {
					File file = new File(attachMent.filepath);
					if (file.exists()) {
						Picasso.with(mContext).load(file).resize(200, 200).into(iconIv);
						iconIv.setTag(attachMent.filepath);
						iconIv.setVisibility(View.VISIBLE);
					}
				}
			} else {
				iconIv.setVisibility(View.GONE);
			}
			TextView nameTv = (TextView) viewMap.get(R.id.att_name);
			TextView cateTv = (TextView) viewMap.get(R.id.att_cate);
			String attachName = "附件名称: " + attachMent.name;
			String s = TextUtils.isEmpty(attachMent.categorydesc) ? attachMent.type : attachMent.categorydesc;

			String attachCate = "类型: " + s;
			if (attachMent.fileType == Attach.FILETYPE_INVOICE) {
				attachName = "发票号码: " + attachMent.name;
				attachCate = "购机日期: " + attachMent.description;
			}
			nameTv.setText(attachName);
			cateTv.setText(attachCate);

			TextView orderIdTv = (TextView) viewMap.get(R.id.att_order_id);
			orderIdTv.setText("工单编号: " + attachMent.orderId);
			TextView uploadView = (TextView) viewMap.get(R.id.btn_upload);
			uploadView.setVisibility(attachMent.success == 1 ? View.GONE : View.VISIBLE);
			View deleteView = viewMap.get(R.id.btn_att_delete);
			if (attachMent.itype != 1) {
				deleteView.setTag(attachMent);
			} else {
				deleteView.setVisibility(View.GONE);
			}
			uploadView.setTag(attachMent);
			if (attachMent.success == 2) {
				uploadView.setText("上传中...");
				uploadView.setEnabled(true);
			} else {
				uploadView.setEnabled(true);
				uploadView.setText("上传");
			}
			View playView = viewMap.get(R.id.ic_play);
			playView.setVisibility(attachMent.fileType == Attach.FILETYPE_VIDEO ? View.VISIBLE : View.GONE);
		}
			break;
		case R.layout.knowledge: {
			KnowledgeInfo knowledgeInfo = (KnowledgeInfo) data;
			TextView labelTv = (TextView) viewMap.get(R.id.label);
			TextView typeTv = (TextView) viewMap.get(R.id.type);
			TextView subTv = (TextView) viewMap.get(R.id.sublabel);
			TextView authorTv = (TextView) viewMap.get(R.id.author);
			TextView key_code = (TextView) viewMap.get(R.id.key_code);
			TextView timeTv = (TextView) viewMap.get(R.id.timestamp);
			labelTv.setText(knowledgeInfo.KnowledgeTitle);
			subTv.setText(knowledgeInfo.KnowledgeSummary);
			typeTv.setText(mContext.getString(R.string.label_type, knowledgeInfo.KnowledgeType));
			authorTv.setText(mContext.getString(R.string.label_author, knowledgeInfo.KnowledgeAuthor));
			key_code.setText(mContext.getString(R.string.label_keycode, knowledgeInfo.KnowledgeID));
			timeTv.setText(mContext.getString(R.string.label_timestamp, knowledgeInfo.KnowledgeDate));
		}
			break;
		case R.layout.box_category_item: {
			Material material = (Material) data;
			TextView nameTv = (TextView) viewMap.get(R.id.name);
			nameTv.setText(material.MaterialCategory);
			CheckBox checkBox = (CheckBox) viewMap.get(android.R.id.checkbox);
			checkBox.setChecked(mSelectData.contains(material));
		}
			break;
		case R.layout.search_category_item: {
			SearchCategory search = (SearchCategory) data;
			TextView nameTv = (TextView) viewMap.get(R.id.name);
			nameTv.setText(search.sName);
			CheckBox checkBox = (CheckBox) viewMap.get(android.R.id.checkbox);
			checkBox.setChecked(mSelectData.contains(search));
		}
			break;
		case R.layout.replace_box_item: {
			viewMap.get(R.id.title_view).setVisibility(position == 0 ? View.VISIBLE : View.GONE);
			ProductBox productBox = (ProductBox) data;
			TextView up1 = (TextView) viewMap.get(R.id.tv_up1);
			TextView up2 = (TextView) viewMap.get(R.id.tv_up2);
			TextView up3 = (TextView) viewMap.get(R.id.tv_up3);
			TextView up4 = (TextView) viewMap.get(R.id.tv_up4);
			TextView up5 = (TextView) viewMap.get(R.id.tv_up5);
			up1.setText(productBox.SPSN);
			up2.setText(productBox.MaterialNo);
			up3.setText(productBox.SPDesc);
			up4.setText(productBox.SPTypeDesc);
			up5.setText(productBox.YakuanPrice);
		}
			break;
		// TODO
		// 填充新添加的布局
		// 备注页面
		case R.layout.order_action_edittext:

			mEdittextData = (ComeTypes) data;
			if (data instanceof OrderInfo) {
				if (data instanceof ComeTypes) {
					String sourceType = ((ComeTypes) data).type;
					if (sourceType.equals("10")) {
						if (TextUtils.equals(((ComeTypes) data).Come, "备注")) {
							mNoteTv = (EditText) viewMap.get(R.id.tv_node);

						} else if (TextUtils.equals(((ComeTypes) data).Come, "回填主机号")) {

							mHostNumdata = (ComeTypes) data;
							mHostNum = (EditText) viewMap.get(R.id.tv_node);
							break;
						} else {
							mFaultdata = (ComeTypes) data;
							mFaultResult = (EditText) viewMap.get(R.id.tv_node);
							break;
						}

					} else if (sourceType.equals("120")) {
						mAmountdata = (ComeTypes) data;
						mAmount = (EditText) viewMap.get(R.id.tv_node);
						break;
					}
				}
			}
			mNoteTv.setText((mCheck2String != null && mCheck2String.length() > 0) ? mCheck2String : "");

			break;
		case R.layout.order_action_checkbox:
			ComeTypes sourceBean1 = (ComeTypes) data;
			TextView tv_select_sourceoption = (TextView) view.findViewById(R.id.tv_select_sourceoption);
			CharSequence text = tv_select_sourceoption.getText();
			mSourceOption = null;

			orderActivity = null;
			if (mContext instanceof OrderActivity) {
				orderActivity = (OrderActivity) mContext;
			}
			int slectposition = orderActivity.getmSlectPosition();
			mSourceOption = sourceBean1.TabControls.get(slectposition);
			tv_select_sourceoption.setText(mSourceOption.Name);
			TextView checkbox_tv_taskname = (TextView) viewMap.get(R.id.tv_taskname);
			checkbox_tv_taskname.setTag(sourceBean1);
			// checkbox_tv_taskname.setText(sourceBean1.Come);
			break;

		// 选择时间和选择原因
		case R.layout.reasion_date_view: {
			mResionDatedata = (ComeTypes) data;

			TextView dateView = (TextView) viewMap.get(R.id.btn_pre_date);
			long currentTimeMillis = System.currentTimeMillis();

			String now = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(currentTimeMillis));
			String tomrrow = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(currentTimeMillis + 60 * 1000 * 30));

			// CharSequence now = DateFormat.format("yyyy年MM月dd日 HH:mm", new
			// Date(currentTimeMillis));
			// CharSequence tomrrow = DateFormat.format("yyyy年MM月dd日 HH:mm", new
			// Date(currentTimeMillis + 60 * 1000 * 60 * 24));
			dateView.setText(mResionDatedata.rule == 4 ? tomrrow : now);
			mDate = dateView.getText().toString();

		}
			break;

		case R.layout.order_action_date_time: {

			mDatedata = (ComeTypes) data;

			TextView dateView = (TextView) viewMap.get(R.id.btn_pre_date);
			long currentTimeMillis = System.currentTimeMillis();

			String now = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(currentTimeMillis));
			String tomrrow = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(currentTimeMillis + 60 * 1000 * 30));
			dateView.setText(mDatedata.rule == 4 ? tomrrow : now);
			mDate = dateView.getText().toString();

		}

			break;
		// 点击弹出签字
		case R.layout.order_state_writename:

			// mWriteNamedata = (ComeTypes) data;
			// ComeTypes sourceBean2 = (ComeTypes) data;
			// ivSign = (ImageView) viewMap.get(R.id.iv_sign);
			// tvSign = (TextView) viewMap.get(R.id.tv_sign);
			// tvSign.setText(sourceBean2.TabControls.get(0).Name);

			break;
		case R.layout.order_state_contacted:

			mContacteddata = (ComeTypes) data;
			mBtnCheckHost = (View) viewMap.get(R.id.btn_check);
			mContactedBarCodeTv = (TextView) viewMap.get(R.id.tv_bar_code);

			break;
		case R.layout.order_action_checkbox2:
			mCheck2String = "";
			mCheck2data = (ComeTypes) data;
			ComeTypes comeTypes = (ComeTypes) data;
			mcheck1Result = (TextView) view.findViewById(R.id.tv_select_sourceoption);

			View chech2_ll = view.findViewById(R.id.ll_selelct_sourception);
			mcheck2Resrlt = (TextView) view.findViewById(R.id.tv_select_sourceoption2);
			TextView taskname = (TextView) view.findViewById(R.id.tv_taskname);
			mcheck1Result.setTag(comeTypes.TabControls);
			taskname.setText("故障分类");
			// mcheck2Resrlt.setTag(1, ((ComeTypes) data).rule);

			orderActivity = null;
			if (mContext instanceof OrderActivity) {
				orderActivity = (OrderActivity) mContext;
			}

			ArrayList listCheck2 = new ArrayList<Object>();
			boolean[] mCheckResult = orderActivity.getmCheck1Result();
			StringBuffer stringBuffer = new StringBuffer();

			int countcheck1 = 0;
			if (mCheckResult != null && mCheckResult.length > 0) {
				for (int i = 0; i < mCheckResult.length; i++) {
					if (mCheckResult[i]) {
						listCheck2.addAll((List<SourceOption>) comeTypes.TabControls.get(i).TabControls);
						stringBuffer.append(comeTypes.TabControls.get(i).Name);
						countcheck1++;
					}
				}
			}

			if (stringBuffer.length() > 0) {
				mcheck1Result.setText(countcheck1 == mCheckResult.length ? "all item checked" : countcheck1 + " item checked");
			}
			int countcheck2 = 0;
			boolean[] mCheck2Result = orderActivity.getmCheck2Result();
			StringBuffer stringBuffer2 = new StringBuffer();
			if (mCheck2Result != null && mCheck2Result.length > 0) {
				for (int i = 0; i < mCheck2Result.length; i++) {
					if (mCheck2Result[i]) {
						stringBuffer2.append(mCheck2.get(i));
						countcheck2++;
					}
				}
			}
			if (stringBuffer2.length() > 0) {
				mcheck2Resrlt.setText(countcheck2 + " item checked");
				mCheck2String = stringBuffer2.toString();
			}
			List<String> list = new ArrayList<String>();
			for (Object map : listCheck2) {
				list.add(((LinkedHashMap<String, String>) map).get("Name"));
			}

			chech2_ll.setTag(list);
			break;

		case R.layout.order_action_title:
			ComeTypes com = (ComeTypes) data;
			TextView view2 = (TextView) viewMap.get(R.id.tv_taskname);
			view2.setText(com.Come);
			break;
		case R.layout.order_action_remark:
			mEdittextData = (ComeTypes) data;
			mNoteTv = (EditText) viewMap.get(R.id.tv_node);
			mTaskdesc = (TextView) viewMap.get(R.id.tv_taskdesc);

			if (mContext instanceof OrderActivity) {
				OrderActivity orderActivity = (OrderActivity) mContext;
				String desc = (String) (TextUtils.isEmpty(orderActivity.getTaskDesc()) ? "请按照实际情况填写" : orderActivity.getTaskDesc());

				mTaskdesc.setText("任务说明: " + desc);
			}

			break;
		case R.layout.item_beijian:
			BorrowOrderInfo borrowOrderInfo = (BorrowOrderInfo) data;
			TextView tvMarkStatus = (TextView) viewMap.get(R.id.MarkStatus);
			TextView tvMaterialClassName = (TextView) viewMap.get(R.id.MaterialClassName);
			TextView tvMaterialName = (TextView) viewMap.get(R.id.MaterialName);
			TextView tvMaterialNo = (TextView) viewMap.get(R.id.MaterialNo);
			TextView tvSOID = (TextView) viewMap.get(R.id.SOID);
			TextView tvSPSN = (TextView) viewMap.get(R.id.SPSN);
			TextView tvcreatetime = (TextView) viewMap.get(R.id.createtime);

			tvMarkStatus.setText(borrowOrderInfo.MarkStatus);
			tvMaterialClassName.setText(borrowOrderInfo.MaterialClassName);
			tvMaterialName.setText(borrowOrderInfo.MaterialName);
			tvMaterialNo.setText(borrowOrderInfo.MaterialName);
			tvSOID.setText(borrowOrderInfo.SOID + "");
			tvSPSN.setText(borrowOrderInfo.SPSN);
			tvcreatetime.setText(borrowOrderInfo.CreateTime);
			break;
		case R.layout.change:

			ChangeHistory change = (ChangeHistory) data;

			TextView DownMaterialNo = (TextView) viewMap.get(R.id.DownMaterialNo);
			TextView DownMaterialNoDesc = (TextView) viewMap.get(R.id.DownMaterialNoDesc);
			TextView DownPartsSN = (TextView) viewMap.get(R.id.DownPartsSN);
			TextView SwapCategoryDesc = (TextView) viewMap.get(R.id.SwapCategoryDesc);
			TextView UpMaterialNo = (TextView) viewMap.get(R.id.UpMaterialNo);
			TextView UpMaterialNoDesc = (TextView) viewMap.get(R.id.UpMaterialNoDesc);
			TextView UpPartsSN = (TextView) viewMap.get(R.id.UpPartsSN);
			TextView tv1 = (TextView) viewMap.get(R.id.tv1);
			tv1.setText("换上件时间");
			DownMaterialNo.setText(change.DownMaterialNo);
			DownMaterialNoDesc.setText(change.DownMaterialNoDesc);
			DownPartsSN.setText(change.DownPartsSN);
			SwapCategoryDesc.setText(change.CreateTime);
			UpMaterialNo.setText(change.UpMaterialNo);
			UpMaterialNoDesc.setText(change.UpMaterialNoDesc);
			UpPartsSN.setText(change.UpPartsSN);
			viewMap.get(R.id.btn_delete).setVisibility(change.mReplaceEdit ? View.VISIBLE : View.GONE);

			break;
		}
	}

	/**
	 * 选中数据数组
	 */
	private List<AbsApiData> mSelectData;

	public List<AbsApiData> getSelectData() {
		return mSelectData;
	}

	/**
	 * 初始化页面
	 * 
	 * @param parent
	 * @param type
	 * @param visible
	 * @return
	 */
	private View getItemView(ViewGroup parent, int type, boolean visible) {
		int id = getViewResId(type);
		View view = mInflater.inflate(id, parent, false);
		SparseArray<View> viewMap = new SparseArray<View>();
		switch (id) {
		case R.layout.user_account:
			putViewMap(viewMap, view, R.id.username);
			putViewMap(viewMap, view, R.id.btn_delete).setOnClickListener(mClickListener);
			break;
		case R.layout.order_card_item:

			putViewMap(viewMap, view, R.id.order_code);
			putViewMap(viewMap, view, R.id.order_host);
			putViewMap(viewMap, view, R.id.order_type);
			putViewMap(viewMap, view, R.id.order_host_code);
			putViewMap(viewMap, view, R.id.order_customer);
			putViewMap(viewMap, view, R.id.order_customer_phone);
			putViewMap(viewMap, view, R.id.order_callphone).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.order_routeplan).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.order_customer_address);
			putViewMap(viewMap, view, R.id.order_customer_company);
			putViewMap(viewMap, view, R.id.order_state);
			putViewMap(viewMap, view, R.id.order_date);
			putViewMap(viewMap, view, R.id.order_time);
			break;
		case R.layout.order_detil_item:

			putViewMap(viewMap, view, R.id.order_code);
			putViewMap(viewMap, view, R.id.order_subtype);
			putViewMap(viewMap, view, R.id.order_host_fault);
			putViewMap(viewMap, view, R.id.order_host);
			putViewMap(viewMap, view, R.id.order_type);
			putViewMap(viewMap, view, R.id.order_host_code);
			putViewMap(viewMap, view, R.id.order_PartsStatus);
			putViewMap(viewMap, view, R.id.btn_showall).setOnClickListener(mClickListener);

			// putViewMap(viewMap, view, R.id.order_customer);
			// putViewMap(viewMap, view, R.id.order_customer_phone);
			// putViewMap(viewMap, view, R.id.order_customer_address);
			putViewMap(viewMap, view, R.id.order_state);
			putViewMap(viewMap, view, R.id.order_date);
			// putViewMap(viewMap, view, R.id.order_time);
			putViewMap(viewMap, view, R.id.order_count);
			putViewMap(viewMap, view, R.id.rapir_count);
			break;
		case R.layout.order_detil_customer_item:

			putViewMap(viewMap, view, R.id.order_type);
			putViewMap(viewMap, view, R.id.order_state);
			putViewMap(viewMap, view, R.id.order_subtype);
			putViewMap(viewMap, view, R.id.order_date);
			putViewMap(viewMap, view, R.id.order_paidancount);
			putViewMap(viewMap, view, R.id.order_remark).setOnClickListener(mClickListener);
			;
			putViewMap(viewMap, view, R.id.order_count);
			break;
		case R.layout.alert_item:
			putViewMap(viewMap, view, R.id.tip_type);
			putViewMap(viewMap, view, R.id.ic_bell);
			putViewMap(viewMap, view, R.id.ic_shock);
			putViewMap(viewMap, view, R.id.tv_time);
			putViewMap(viewMap, view, R.id.tip_text);
			putViewMap(viewMap, view, R.id.btn_delete).setOnClickListener(mClickListener);
			break;
		case R.layout.host_item:
			putViewMap(viewMap, view, R.id.cate_name);
			putViewMap(viewMap, view, R.id.part_num);
			putViewMap(viewMap, view, R.id.part_code);
			putViewMap(viewMap, view, R.id.part_name);
			// putViewMap(viewMap, view, R.id.spit_view);
			break;
		case R.layout.attachment:
			putViewMap(viewMap, view, android.R.id.icon).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.att_name);
			putViewMap(viewMap, view, R.id.att_cate);
			putViewMap(viewMap, view, R.id.att_order_id);
			putViewMap(viewMap, view, R.id.ic_play);
			putViewMap(viewMap, view, R.id.btn_upload).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.btn_att_delete).setOnClickListener(mClickListener);
			break;
		case R.layout.knowledge:
			putViewMap(viewMap, view, R.id.label);
			putViewMap(viewMap, view, R.id.type);
			putViewMap(viewMap, view, R.id.sublabel);
			putViewMap(viewMap, view, R.id.author);
			putViewMap(viewMap, view, R.id.key_code);
			putViewMap(viewMap, view, R.id.timestamp);
			break;
		case R.layout.search_category_item:
		case R.layout.box_category_item:
			putViewMap(viewMap, view, R.id.name);
			putViewMap(viewMap, view, android.R.id.checkbox);
			break;
		case R.layout.replace_box_item:
			putViewMap(viewMap, view, R.id.tv_up1);
			putViewMap(viewMap, view, R.id.tv_up2);
			putViewMap(viewMap, view, R.id.tv_up3);
			putViewMap(viewMap, view, R.id.tv_up4);
			putViewMap(viewMap, view, R.id.tv_up5);
			putViewMap(viewMap, view, R.id.title_view);
			break;
		// TODO

		// case VIEWTYPE_ORDER_SOURSE_EDITTEXT:
		//
		// result = R.layout.order_action_edittext;
		// break;
		// case VIEWTYPE_ORDER_SOURSE_CHECKBOX:
		//
		// result = R.layout.order_action_checkbox;
		// break;

		// 新添加的布局
		case R.layout.order_action_edittext:
			putViewMap(viewMap, view, R.id.tv_node);
			putViewMap(viewMap, view, R.id.tv_taskname);
			break;
		case R.layout.order_action_checkbox:
			putViewMap(viewMap, view, R.id.tv_taskname);
			putViewMap(viewMap, view, R.id.tv_select_sourceoption);
			break;
		case R.layout.reasion_date_view:

			putViewMap(viewMap, view, R.id.btn_pre_date).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.radio_one_r).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.radio_two_r).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.radio_three_r).setOnClickListener(mClickListener);

			break;
		case R.layout.order_action_date_time:

			putViewMap(viewMap, view, R.id.btn_pre_date).setOnClickListener(mClickListener);

			break;
		case R.layout.order_state_writename:

			// putViewMap(viewMap, view,
			// R.id.tv_sign).setOnClickListener(signListener);
			// putViewMap(viewMap, view,
			// R.id.iv_sign).setOnClickListener(signListener);

			break;
		case R.layout.order_state_contacted:

			// mBtnCheckHost = contactView.findViewById(R.id.btn_check);
			// mBtnCheckHost.setOnClickListener(this);
			// contactView.findViewById(R.id.btn_read_code).setOnClickListener(this);

			putViewMap(viewMap, view, R.id.btn_check).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.btn_read_code).setOnClickListener(mClickListener);
			putViewMap(viewMap, view, R.id.tv_bar_code);
			break;
		case R.layout.order_action_checkbox2:

			putViewMap(viewMap, view, R.id.tv_taskname);
			putViewMap(viewMap, view, R.id.tv_select_sourceoption).setOnClickListener(mClickListener);
			;
			putViewMap(viewMap, view, R.id.ll_selelct_sourception).setOnClickListener(mClickListener);
			;
		case R.layout.order_action_title:

			putViewMap(viewMap, view, R.id.tv_taskname);
			break;

		case R.layout.order_action_remark:
			putViewMap(viewMap, view, R.id.tv_node);
			putViewMap(viewMap, view, R.id.tv_taskdesc);
			break;
		case R.layout.item_beijian:
			putViewMap(viewMap, view, R.id.MarkStatus);
			putViewMap(viewMap, view, R.id.MaterialClassName);
			putViewMap(viewMap, view, R.id.MaterialName);
			putViewMap(viewMap, view, R.id.MaterialNo);
			putViewMap(viewMap, view, R.id.SOID);
			putViewMap(viewMap, view, R.id.SPSN);
			putViewMap(viewMap, view, R.id.createtime);
			break;
		case R.layout.change:
			putViewMap(viewMap, view, R.id.tv1);
			putViewMap(viewMap, view, R.id.DownMaterialNo);
			putViewMap(viewMap, view, R.id.DownMaterialNoDesc);
			putViewMap(viewMap, view, R.id.DownPartsSN);
			putViewMap(viewMap, view, R.id.SwapCategoryDesc);
			putViewMap(viewMap, view, R.id.UpMaterialNo);
			putViewMap(viewMap, view, R.id.UpMaterialNoDesc);
			putViewMap(viewMap, view, R.id.UpPartsSN);
			putViewMap(viewMap, view, R.id.btn_delete);
			break;
		}
		view.setTag(viewMap);
		return view;
	}

	private View putViewMap(SparseArray<View> viewMap, View view, int id) {
		View v = view.findViewById(id);
		viewMap.put(id, v);
		return v;
	}

	/**
	 * 配置数据页面类型
	 * 
	 * @param data
	 * @return
	 */
	private int getItemViewType(AbsApiData data) {
		int result = -1;
		if (data instanceof UserAccount) {
			result = VIEWTYPE_USERACCOUNT;
		} else if (data instanceof OrderInfo) {
			// 加上自己的判断
			// TODO
			result = VIEWTYPE_ORDER;

			if (!((OrderInfo) data).IsOrderList) {
				String soType = ((OrderInfo) data).SOType;

				if (TextUtils.equals(soType, "1")) {
					result = VIEWTYPE_PRODUCT_ORDER_DETAIL;
				} else if (TextUtils.equals(soType, "2")) {
					result = VIEWTYPE_CUSTOMER_ORDER_DETAIL;
				}
				// result = VIEWTYPE_PRODUCT_ORDER_DETAIL;
			}

			if (data instanceof ComeTypes) {
				// 判断下面的条目的类型,每次都是什么什么类型都要判断.
				String sourceType = ((ComeTypes) data).type;
				if (sourceType.equals("10")) {
					result = VIEWTYPE_ORDER_SOURSE_EDITTEXT;
					if (TextUtils.equals(((ComeTypes) data).Come, "备注")) {
						result = VIEWTYPE_ORDER_SOURSE_REMARK;
					}

				} else if (sourceType.equals("20")) {
					result = VIEWTYPE_ORDER_SOURSE_CHECKBOX;
				} else if (sourceType.equals("30")) {
					result = VIEWTYPE_ORDER_SOURSE_DATERESON;
				} else if (sourceType.equals("40")) {
					result = VIEWTYPE_ORDER_SOURSE_DATE;
				} else if (sourceType.equals("60")) {
					result = VIEWTYPE_ORDER_SOURSE_DATE;
				} else if (sourceType.equals("90")) {
					result = VIEWTYPE_ORDER_RADCOMBOX2;
				} else if (sourceType.equals("120")) {
					result = VIEWTYPE_ORDER_SOURSE_EDITTEXT;
				} else if (sourceType.equals("130")) {
					result = VIEWTYPE_ORDER_SOURSE_WRITENAME;

				} else if (sourceType.equals("140")) {
					result = VIEWTYPE_ORDER_SOURSE_TESTORDER;
				} else if (sourceType.equals("501")) {
					result = VIEWTYPE_ORDER_SOURSE_TITLE;
				}

				// result = VIEWTYPE_ORDER_SOURSE;
			}

		} else if (data instanceof AlarmAlert) {
			result = VIEWTYPE_ALERT;
		} else if (data instanceof Material) {
			result = mEdit ? VIEWTYPE_HOST_EDIT : VIEWTYPE_HOST;
		} else if (data instanceof Attach) {
			result = VIEWTYPE_ATTACHMENT;
		} else if (data instanceof KnowledgeInfo) {
			result = VIEWTYPE_KNOWLEDGEINFO;
		} else if (data instanceof SearchCategory) {
			result = VIEWTYPE_SEARCHCATEGORY;
		} else if (data instanceof ProductBox) {
			result = VIEWTYPE_REPLACE_BOX;
		} else if (data instanceof BorrowOrderInfo) {
			result = VIWETYPE_BRROWSPARE;
		} else if (data instanceof ChangeHistory) {
			result = VIWETYPE_CHANGEHISTORY;
		}
		return result;
	}

	/**
	 * 根据类型配置页面
	 * 
	 * @param type
	 * @return
	 */
	private int getViewResId(int type) {
		int result = R.layout.development_view;
		switch (type) {
		case VIEWTYPE_HOST_EDIT:
			result = R.layout.box_category_item;
			break;
		case VIEWTYPE_USERACCOUNT:
			result = R.layout.user_account;
			break;
		case VIEWTYPE_KNOWLEDGEINFO:
			result = R.layout.knowledge;
			break;
		case VIEWTYPE_SEARCHCATEGORY:
			result = R.layout.search_category_item;
			break;
		case VIEWTYPE_ORDER:
			result = R.layout.order_card_item;
			break;
		case VIEWTYPE_ALERT:
			result = R.layout.alert_item;
			break;
		case VIEWTYPE_HOST:
			result = R.layout.host_item;
			break;
		case VIEWTYPE_ATTACHMENT:
			result = R.layout.attachment;
			break;
		case VIEWTYPE_REPLACE_BOX:
			result = R.layout.replace_box_item;
			break;
		case VIEWTYPE_ORDER_SOURSE_EDITTEXT:

			result = R.layout.order_action_edittext;
			break;
		case VIEWTYPE_ORDER_SOURSE_CHECKBOX:

			result = R.layout.order_action_checkbox;
			break;
		case VIEWTYPE_ORDER_SOURSE_DATERESON:

			result = R.layout.reasion_date_view;
			break;
		case VIEWTYPE_ORDER_SOURSE_WRITENAME:

			result = R.layout.order_state_writename;
			break;
		case VIEWTYPE_ORDER_SOURSE_TESTORDER:

			result = R.layout.order_state_contacted;
			break;

		case VIEWTYPE_ORDER_SOURSE_DATE:

			result = R.layout.order_action_date_time;
			break;
		case VIEWTYPE_ORDER_RADCOMBOX2:

			result = R.layout.order_action_checkbox2;
			break;
		case VIEWTYPE_PRODUCT_ORDER_DETAIL:

			result = R.layout.order_detil_item;
			break;
		case VIEWTYPE_CUSTOMER_ORDER_DETAIL:

			result = R.layout.order_detil_customer_item;
			break;
		case VIEWTYPE_ORDER_SOURSE_TITLE:

			result = R.layout.order_action_title;
			break;
		case VIEWTYPE_ORDER_SOURSE_REMARK:

			result = R.layout.order_action_remark;
			break;
		case VIWETYPE_BRROWSPARE:

			result = R.layout.item_beijian;
			break;
		case VIWETYPE_CHANGEHISTORY:

			result = R.layout.change;
			break;
		}
		return result;
	}

	private OrderActivity orderActivity;
	/**
	 * 页面元素监听事件
	 */
	private Uri mlastUri;
	private List<SourceOption> mCheck1;
	private OnClickListener mClickListener = new OnClickListener() {

		private int i = 0;
		private boolean flag = false;

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.tv_select_sourceoption:

				mCheck1 = (List<SourceOption>) v.getTag();

				orderActivity.showDialog(DLG_SOURCECHECK1);
				break;
			case R.id.ll_selelct_sourception:
				mCheck2 = (ArrayList<String>) v.getTag();
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("check2", mCheck2);
				((Activity) mContext).showDialog(DLG_SOURCECHECK2, bundle);
				break;
			case R.id.btn_delete: {
				Object object = v.getTag();
				if (object instanceof UserAccount) {
					UserAccount userAccount = (UserAccount) v.getTag();
					Account a = new Account();
					a._id = userAccount.accountId;
					a.delete(mContext);
					getmObjects().remove(userAccount);
					notifyDataSetChanged();
				} else if (object instanceof AlarmAlert) {
					AlarmAlert alarmAlert = (AlarmAlert) object;
					alarmAlert.delete(mContext);
					getmObjects().remove(alarmAlert);
					notifyDataSetChanged();
				}

			}
				break;
			case android.R.id.icon:
				Object object = v.getTag();
				if (object != null && object instanceof String) {
					String file = (String) object;

					Intent intent = new Intent(mContext, ImageActivity.class);
					intent.putExtra("showtitle", false);
					intent.putExtra(ImageActivity.EXTRA_IMAGE, file);
					mContext.startActivity(intent);
				}
				break;
			case R.id.btn_att_delete: {
				// TODO delete
				Attach attachMent = (Attach) v.getTag();
				if (attachMent.itype != 1) {
					attachMent.delete(mContext);
					getmObjects().remove(attachMent);
					notifyDataSetChanged();
				}
			}
				break;
			case R.id.btn_upload: {
				Activity activity = (Activity) mContext;
				activity.showDialog(DLG_SENDING);
				Attach attachMent = (Attach) v.getTag();
				File file = null;
				if (attachMent.fileType == Attach.FILETYPE_VIDEO) {
					ContentResolver cr = mContext.getContentResolver();
					file = FileUtil.getTempFile(mContext);
					FileUtil.copy(cr, Uri.parse(attachMent.filepath), file);
				} else {
					file = new File(attachMent.filepath);
				}
				attachMent.success = 2;
				attachMent.update(mContext);
				notifyDataSetChanged();
				FilePair filePair = new FilePair(PARAM_IMAGEDATA, file);
				HashMap<String, String> hashMap = new HashMap<String, String>();
				// postValues.add(new ParamPair(PARAM_ORDERID,
				// attachMent.orderId));
				hashMap.put(PARAM_ORDERID, attachMent.orderId);
				hashMap.put(PARAM_ENGINEER, attachMent.engineerId);

				hashMap.put(PARAM_TYPE, attachMent.category);
				// postValues.add(new ParamPair(PARAM_ENGINEER,
				// attachMent.engineerId));
				// postValues.add(new ParamPair(PARAM_FILENAME,
				// attachMent.name));

				hashMap.put(PARAM_FILENAME, attachMent.name);
				hashMap.put(PARAM_CATEGORY, attachMent.type);
				mlastUri = Uri.parse(URL_UPLOADATTACH).buildUpon().appendQueryParameter("attachId", String.valueOf(attachMent._id)).build();
				int id = attachMent.fileType == Attach.FILETYPE_VIDEO ? 110 : 0;

				OkHttpHelper.getInstance(mContext).load(mlastUri.toString(), mCallback, hashMap, PARAM_IMAGEDATA, file.getName(), file, mContext);
				// CacheParams params = new
				// CacheParams(CacheParams.PRIORITY_NORMAL, path,
				// CacheParams.MODE_FLAG_FORCE_IGNORE_LOCAL);

			}
				break;
			/**
			 * 
			 * 添加日期和原因的点击事件
			 */
			case R.id.btn_pre_date: {

				orderActivity = null;
				if (mContext instanceof OrderActivity) {
					orderActivity = (OrderActivity) mContext;
				}
				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(orderActivity, mDate, mDateListener);
				dateTimePicKDialog.dateTimePicKDialog((TextView) v);
			}
				break;
			case R.id.radio_one_r: {
				conReasonView(v);
			}
				break;
			case R.id.radio_two_r: {
				conReasonView(v);
			}
				break;
			case R.id.radio_three_r: {
				conReasonView(v);
			}

				break;

			case R.id.btn_check: {

				orderActivity = null;
				if (mContext instanceof OrderActivity) {
					orderActivity = (OrderActivity) mContext;
				}
				orderActivity.checkHostCode();
			}
				break;
			case R.id.btn_read_code: {

				orderActivity = null;
				if (mContext instanceof OrderActivity) {
					orderActivity = (OrderActivity) mContext;
				}
				orderActivity.scanCode("扫描主机条码");

			}
				break;
			case R.id.order_callphone: {

				String Tele = (String) v.getTag();
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + (String) v.getTag()));
				intent.putExtra(TabGroup.EXTRA_GO_OUT, true);
				mContext.startActivity(intent);
			}
				break;

			case R.id.order_routeplan: {

				LenovoServicesApplication appliction = (LenovoServicesApplication) mContext.getApplicationContext();
				BDLocation coordinate = appliction.getCoordinate();
				if (appliction.getCoordinate() == null) {
					Utils.showToast(mContext, "暂时无法获取您的位置,请检查定位服务是否打开");
					break;

				}
				LatLng ptStart = new LatLng(appliction.getCoordinate().getLatitude(), appliction.getCoordinate().getLongitude());

				RouteParaOption para = new RouteParaOption().startPoint(ptStart).endName(v.getTag().toString());

				try {
					BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, mContext.getApplicationContext());
					// Utils.showToast(mContext, i++ + "");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
				break;

			case R.id.order_remark:

				TextView view = (TextView) v;

				view.setSingleLine(flag);
				flag = !flag;

				break;
			default:

				break;
			}

		}

	};

	public DateListener mDateListener = new DateListener() {

		@Override
		public void date(String date) {
			mDate = date;
		}
	};

	/**
	 * 从列表删除元素
	 * 
	 * @param id
	 */
	private void removeItem(int id) {
		AbsApiData absApiData = null;
		for (AbsApiData data : getmObjects()) {
			Attach attach = (Attach) data;
			if (attach._id == id) {
				absApiData = data;
			}
		}
		if (absApiData != null)
			remove(absApiData);
	}

	/**
	 * 文件上传回调
	 */
	private OkHttpStringCallback mCallback = new OkHttpStringCallback(mContext) {

		@Override
		public void onResponse(String result) {
			Activity activity = (Activity) mContext;
			try {
				activity.dismissDialog(DLG_SENDING);
			} catch (Exception e) {
			}
			boolean safe = OkHttpHelper.getInstance(mContext).isSuccessResult(result, mContext);

			if (!safe) {
				return;
			}
			String aId = mlastUri.getQueryParameter("attachId");
			int attachId = Integer.parseInt(aId);
			Attach attach = Attach.queryByID(mContext, attachId);
			attach.success = 1;
			attach.update(mContext);
			removeItem(attachId);
		}
	};

	private Bitmap mSignBitmap;
	private OnClickListener signListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			orderActivity = null;
			if (mContext instanceof OrderActivity) {

				orderActivity = (OrderActivity) mContext;
			}
			WritePadDialog writeTabletDialog = new WritePadDialog(orderActivity, new DialogListener() {
				@Override
				public void refreshActivity(Object object) {

					mSignBitmap = (Bitmap) object;
					/*
					 * BitmapFactory.Options options = new
					 * BitmapFactory.Options(); options.inSampleSize = 15;
					 * options.inTempStorage = new byte[5 * 1024]; Bitmap zoombm
					 * = BitmapFactory.decodeFile(signPath, options);
					 */
					ivSign.setImageBitmap(mSignBitmap);
					tvSign.setVisibility(View.GONE);
				}
			});
			writeTabletDialog.show();
		}
	};
	private View mLastRView;
	private String mContactReason;
	private String mDate;
	private EditText mNoteTv;
	private SourceOption mSourceOption;
	private ImageView ivSign;
	private TextView tvSign;
	private ArrayList<String> mCheck2;
	private String mCheck2String;
	private TextView mcheck1Result;
	private TextView mcheck2Resrlt;

	private void conReasonView(View v) {
		TextView tv = (TextView) v;
		mContactReason = tv.getText().toString();
		if (mLastRView != null)
			mLastRView.setSelected(false);
		v.setSelected(true);
		mLastRView = v;
	}

	/**
	 * My
	 * 
	 * @param comeTypes
	 */
	public void addData(List<ComeTypes> comeTypes) {
		super.add((Collection<AbsApiData>) comeTypes);
	}

	/**
	 * 获取要提交的值
	 * 
	 * @param mSourceOption
	 * @param mDate2
	 * @param mContactReason2
	 * @param mNoteTv
	 */

	public EditText getmNoteTv() {
		return mNoteTv;
	}

	public String getmDate() {
		return mDate;
	}

	public SourceOption getmSourceOption() {
		return mSourceOption;
	}

	public EditText getmAmount() {
		return mAmount;
	}

	public TextView getmContactedBarCodeTv() {
		return mContactedBarCodeTv;
	}

	public View getmBtnCheckHost() {
		return mBtnCheckHost;
	}

	public EditText getmFaultResult() {
		return mFaultResult;
	}

	public EditText getmHostNum() {
		return mHostNum;
	}

	public List<SourceOption> getmsoureCheck1() {
		return mCheck1;
	}

	public ArrayList<String> getmsoureCheck2() {
		return mCheck2;
	}

	private ComeTypes mResionDatedata;
	private ComeTypes mEdittextData;
	private ComeTypes mDatedata;
	private ComeTypes mWriteNamedata;
	private ComeTypes mContacteddata;
	private ComeTypes mCheck2data;
	private TextView mCallPhone;
	private int mLastviewResId;
	private View showAll;

	public ComeTypes getmResionDatedata() {
		return mResionDatedata;
	}

	public ComeTypes getmNoteTvdata() {
		return mEdittextData;
	}

	public ComeTypes getmAmountdata() {
		return mAmountdata;
	}

	public ComeTypes getmHostNumdata() {
		return mHostNumdata;
	}

	public ComeTypes getmFaultResultdata() {
		return mFaultdata;
	}

	public ComeTypes getmDatedata() {
		return mDatedata;
	}

	public ComeTypes getmCheck2data() {
		return mCheck2data;
	}

	public String getmCheck2() {
		return mCheck2String;
	}

	public Bitmap getmSignBitmap() {
		return mSignBitmap;
	}

	public ComeTypes getmWriteNamedata() {
		return mWriteNamedata;
	}

	public View getmLstRView() {
		// TODO Auto-generated method stub
		return mLastRView;
	}

	public String getmContactReason() {
		return mContactReason;
	}

	public void clearmContactReason() {

		mDate = null;
		mContactReason = null;

	}

	public void clearall() {

		mLastRView = null;
		mContactReason = null;
		mDate = null;
		mNoteTv = null;
		mSourceOption = null;

		if (mFaultResult != null) {
			mFaultResult.setText("");
		}

		mFaultResult = null;
		ivSign = null;
		tvSign = null;
		mCheck2 = null;
		mCheck2String = null;
		mcheck1Result = null;
		mcheck2Resrlt = null;
		mResionDatedata = null;
		mEdittextData = null;
		mDatedata = null;
		mWriteNamedata = null;
		mContacteddata = null;
		mCheck2data = null;
	}

}
