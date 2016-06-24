package com.lenovo.sdimobileclient.ui;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.MyLocationData.Builder;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.lenovo.sdimobileclient.LenovoServicesApplication;
import com.lenovo.sdimobileclient.R;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 百度地图路径导航
 * 
 * @author zhangshaofang
 *
 */
public class RoutePlanActivity extends RootActivity implements OnGetRoutePlanResultListener {

	OverlayManager routeOverlay = null;
	private Button mBtnDrive = null; // 驾车搜索
	private Button mBtnTransit = null; // 公交搜索
	private Button mBtnWalk = null; // 步行搜索
	private MapView mMapView = null; // 地图View
	// private MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private BaiduMap mMapController = null;
	RouteLine route = null;
	// 定位相关
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer = null;
	// private MyLocationOverlay myLocationOverlay = null;
	int index = 0;
	// private LocationData locData = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LenovoServicesApplication app = (LenovoServicesApplication) getApplication();
		// if (app.mBMapManager == null) {
		// app.mBMapManager = new BMapManager(this);
		// app.mBMapManager.init(LenovoServicesApplication.MAP_KEY, new
		// LenovoServicesApplication.MyGeneralListener());
		// }
		initBackBtn();
		String address = getIntent().getStringExtra("address");
		TextView endTv = (TextView) findViewById(R.id.end);
		endTv.setText(address);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapController = mMapView.getMap();
		// mSearch = new MKSearch();
		// mSearch.init(app.mBMapManager, mMkSearchListener);

		// 设定搜索按钮的响应
		mBtnDrive = (Button) findViewById(R.id.drive);
		mBtnTransit = (Button) findViewById(R.id.transit);
		mBtnWalk = (Button) findViewById(R.id.walk);
		mBtnDrive.setOnClickListener(clickListener);
		mBtnTransit.setOnClickListener(clickListener);
		mBtnWalk.setOnClickListener(clickListener);
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);

		mMapController.setMaxAndMinZoomLevel(3, 20);
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		// mMapView.setBuiltInZoomControls(true);
		// mMapView.setDoubleClickZooming(true);
		// mMapView.regMapViewListener(app.mBMapManager, mMkMapViewListener);
		// myLocationOverlay = new MyLocationOverlay(mMapView);

		// myLocationOverlay.setData(locData);
		// mMapView.getOverlays().add(myLocationOverlay);
		// myLocationOverlay.enableCompass();
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private OnClickListener clickListener = new OnClickListener() {
		public void onClick(View v) {
			SearchButtonProcess(v);
		}
	};

	// private MKMapViewListener mMkMapViewListener = new MKMapViewListener() {
	//
	// @Override
	// public void onClickMapPoi(MapPoi mapPoiInfo) {
	// String title = "";
	// if (mapPoiInfo != null) {
	// title = mapPoiInfo.strText;
	// Toast.makeText(RoutePlanActivity.this, title, Toast.LENGTH_SHORT).show();
	// }
	//
	// }
	//
	// @Override
	// public void onGetCurrentMap(Bitmap arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onMapAnimationFinish() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onMapLoadFinish() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onMapMoveFinish() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// };;
	// private MKSearchListener mMkSearchListener = new MKSearchListener() {
	//
	// @Override
	// public void onGetPoiDetailSearchResult(int type, int error) {
	// }
	//
	// public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error)
	// {
	// // 错误号可参考MKEvent中的定义
	// if (error != 0 || res == null) {
	// Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
	// Toast.LENGTH_SHORT).show();
	// return;
	// }
	// RouteOverlay routeOverlay = new RouteOverlay(RoutePlanActivity.this,
	// mMapView);
	// // 此处仅展示一个方案作为示例
	// routeOverlay.setData(res.getPlan(0).getRoute(0));
	// mMapView.getOverlays().clear();
	// mMapView.getOverlays().add(routeOverlay);
	// mMapView.refresh();
	// // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
	// mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
	// routeOverlay.getLonSpanE6());
	// mMapView.getController().animateTo(res.getStart().pt);
	// }
	//
	// public void onGetTransitRouteResult(MKTransitRouteResult res, int error)
	// {
	// if (error != 0 || res == null) {
	// Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
	// Toast.LENGTH_SHORT).show();
	// return;
	// }
	// TransitOverlay routeOverlay = new TransitOverlay(RoutePlanActivity.this,
	// mMapView);
	// // 此处仅展示一个方案作为示例
	// routeOverlay.setData(res.getPlan(0));
	// mMapView.getOverlays().clear();
	// mMapView.getOverlays().add(routeOverlay);
	// mMapView.refresh();
	// // 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
	// mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
	// routeOverlay.getLonSpanE6());
	// mMapView.getController().animateTo(res.getStart().pt);
	// }
	//
	// public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error)
	// {
	// if (error != 0 || res == null) {
	// Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
	// Toast.LENGTH_SHORT).show();
	// return;
	// }
	// RouteOverlay routeOverlay = new RouteOverlay(RoutePlanActivity.this,
	// mMapView);
	// routeOverlay.setData(res.getPlan(0).getRoute(0));
	// mMapView.getOverlays().clear();
	// mMapView.getOverlays().add(routeOverlay);
	// mMapView.refresh();
	// mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(),
	// routeOverlay.getLonSpanE6());
	// mMapView.getController().animateTo(res.getStart().pt);
	//
	// }
	//
	// public void onGetAddrResult(MKAddrInfo res, int error) {
	// }
	//
	// public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {
	// }
	//
	// public void onGetBusDetailResult(MKBusLineResult result, int iError) {
	// }
	//
	// @Override
	// public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
	// }
	//
	// @Override
	// public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int
	// arg2) {
	//
	// }
	//
	// };

	@Override
	protected int getContentViewId() {
		return R.layout.routeplan;
	}

	@Override
	public void finish() {
		// if (mLocClient != null)
		// mLocClient.stop();
		// mMapView.destroy();
		super.finish();
	}

	private View mLastView;

	private void SearchButtonProcess(View v) {
		if (mLocData == null) {
			Toast.makeText(this, "正在获取您的位置", Toast.LENGTH_SHORT).show();
			mLocClient.requestLocation();
			return;
		}
		if (mLastView != null) {
			mLastView.setSelected(false);
		}
		mLastView = v;
		v.setSelected(true);
		EditText editSt = (EditText) findViewById(R.id.start);
		EditText editEn = (EditText) findViewById(R.id.end);

		// MKPlanNode stNode = new MKPlanNode();
		PlanNode stNode = PlanNode.withLocation(new LatLng(mStartGeoPoint.latitude, mStartGeoPoint.longitude));
		// stNode.pt = mStartGeoPoint;
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("北京", editEn.getText().toString());

		// enNode.name = editEn.getText().toString();

		// 实际使用中请对起点终点城市进行正确的设定
		if (v.getId() == R.id.drive) {
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
		} else if (v.getId() == R.id.transit) {
			mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city("北京").to(enNode));
		} else if (v.getId() == R.id.walk) {
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode).to(enNode));
		}
	}

	private LatLng mStartGeoPoint;
	private BDLocation mLocData;

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || mMapView == null)
				return;

			try {
				Builder builder = new MyLocationData.Builder().latitude(location.getLatitude());
				builder = new MyLocationData.Builder().longitude(location.getLongitude());
				mLocData = location;
				// myLocationOverlay.setData(mLocData);
				mStartGeoPoint = new LatLng(mLocData.getLatitude(), mLocData.getLongitude());
				mMapController.animateMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(mLocData.getLatitude(), mLocData.getLongitude())));
			} catch (Exception e) {
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			TextView tv = (TextView) findViewById(R.id.start);
			SearchButtonProcess(mBtnTransit);
			tv.setText("我的位置");
		};
	};

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// mBtnPre.setVisibility(View.VISIBLE);
			// mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			WalkingRouteOverlay overlay = new WalkingRouteOverlay(mMapController);
			mMapController.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// mBtnPre.setVisibility(View.VISIBLE);
			// mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			TransitRouteOverlay overlay = new TransitRouteOverlay(mMapController);
			mMapController.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			// result.getSuggestAddrInfo()
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// mBtnPre.setVisibility(View.VISIBLE);
			// mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			DrivingRouteOverlay overlay = new DrivingRouteOverlay(mMapController);
			routeOverlay = overlay;
			mMapController.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

}
