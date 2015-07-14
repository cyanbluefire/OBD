package com.neoway.vehiclebeta1.ui;

import static com.neoway.vehiclebeta1.Data.GlobalVariable.ipcState;
import static com.neoway.vehiclebeta1.net.HttpManager.ipcMessager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.Handler;
=======
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
<<<<<<< HEAD
import android.widget.ImageButton;
import android.widget.LinearLayout;
=======
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
<<<<<<< HEAD
=======
import com.baidu.mapapi.map.BaiduMapOptions;
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
<<<<<<< HEAD
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
=======
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.Data.GlobalVariable;
import com.neoway.vehiclebeta1.Data.LocalDataUtil;
import com.neoway.vehiclebeta1.callback.RegCallback;
import com.neoway.vehiclebeta1.constant.Constant;
import com.neoway.vehiclebeta1.ipc.AppIpcMessage;
import com.neoway.vehiclebeta1.task.Task;
import com.neoway.vehiclebeta1.utils.ActivityCollector;
import com.neoway.vehiclebeta1.utils.MapUtils;
import com.neoway.vehiclebeta1.utils.Util;

public class MainActivity extends Activity implements RegCallback, OnGetPoiSearchResultListener, OnGetRoutePlanResultListener{	
	
	private TextView tv_location;
<<<<<<< HEAD
	private Button btn_routePlan;
	private Button btn_start_navi;
=======
	private Button btn_startNavi;
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
	private Button btn_me;
	private Button btn_myLocation;
	
	protected static final String TAG = "MainActivity";
	private static LatLng lastPlace;
	private LatLng lastPlaceFormNet = new LatLng(22.607262,114.049181);
<<<<<<< HEAD
//	private static final float ZOOMLEVEL = 16.0f;//设置地图缩放级别为200米
=======
	private static final float ZOOMLEVEL = 16.0f;//设置地图缩放级别为200米
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
	ArrayList<String> arrayList_autoStrs = new ArrayList<String>();
	String location = "";
	private SimpleAdapter adapter_recLocation;
	private ArrayAdapter<String> adapter_hisLocation;
	private boolean isMyPlace = true ;
	
	private List<Map<String, Object>> list_aoutoCom;
	private ListView lv_recLocation;
	private ListView lv_historyLocation;
	private TextView tv_noHistoryLocation;
	private TextView lineDetails;
	private ImageButton pre_view;
	private List<String> history_location_list;
	private LinearLayout lnl_bottom_menu;
	private LinearLayout route_bar;
	
	private MapView mMapView = null;
	public static BaiduMap mBaiduMap = null;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    PoiSearch mPoiSearch = null;
	String city = "深圳";
	LocationClient mLocationClient;
    BDLocation bdLocation;
<<<<<<< HEAD
    RoutePlanSearch mRouteSearsh = null;
    List<DrivingRouteLine> drivingResult;
    boolean useDefaultIcon = true;
    int PlanDistance,PlanDuration;
    public static boolean ack = false;
    String dialogStr;
    ProgressDialog mProgressDialog;
=======
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d

	/**
	 * 当前地点击点
	 */
	private LatLng currentPt;
	private double longitude;
	private double latitude;
	private Marker mMarkerA;
<<<<<<< HEAD
=======
	private Marker mMarker_Me;
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
	private Marker mMarker_Des;
	private Marker oldMarker;
	private Marker mMarker_Car;
	BitmapDescriptor oldBitmap;
	private boolean isSgs = true;
    MapStatus mMapStatus ;
	/**
	 * 导航参数
	 */
	private LatLng latLng;		//导航用的经纬度 
	private String dest;		//导航用的地址
	private byte latLngType = Constant.LATLNG_TYPE_BD09_MC;		//经纬度类型
	
	private List<NameValuePair> dataPairs = new ArrayList<NameValuePair>();
	int latitude_int;
	int longitude_int;	
	private boolean isIpcReady;
	LatLng point;
	
	/**
	 * 欢迎界面
	 */
	 SharedPreferences share; 
	 Editor editor;
	 boolean isFirstIn;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MapStatusUpdate mMapStatusUpdate = null;
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		SDKInitializer.initialize(getApplicationContext());					//初始化sdk引用的context信息，必需在setContentView之前
		
		setContentView(R.layout.activity_main);		
		Log.v(TAG,"start oncreate setContentView");
		

		/*
		 *  判断是否首次登录程序  
		 */
		share = getSharedPreferences("data", Context.MODE_PRIVATE);  
        editor = share.edit(); 
        isFirstIn = share.getBoolean("isFirstIn", true);

        if(isFirstIn){       	
        	Log.i(TAG, "welcomeActivity");
        	editor.putBoolean("isFirstIn", false);
        	editor.commit();
        	Intent intentWelcom = new Intent(this,WhatsnewActivity.class);
        	startActivity(intentWelcom);
        	finish();
        	return;
        }
       
        
		/*
		 * 显示地图
		 */
		mMapView = (MapView)findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
<<<<<<< HEAD
		initMap();
=======
		  /*
         * 初始化地图信息
         */
       
       if(lastPlaceFormNet !=null){  //判断是否从服务器获取了数据
    	   lastPlace = lastPlaceFormNet;
			BitmapDescriptor bitmap = BitmapDescriptorFactory  
				    .fromResource(R.drawable.icon_st); 
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions()  
			    .position(lastPlace)  
			    .icon(bitmap);  
			//在地图上添加Marker，并显示  
			if(mMarker_Car != null){
				mMarker_Car.remove();
				Log.i(TAG, "remove exit marker my car");
			}
			mMarker_Car = (Marker)mBaiduMap.addOverlay(option);
       }else{
       	lastPlace = new LatLng(22.53998, 113.908695);
       }
       mMapStatus = new MapStatus.Builder().target(lastPlace).zoom(16.0f)
	             .build();
        mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
 		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.setMyLocationEnabled(true);
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker mark) {
				// TODO Auto-generated method stub
				Log.i("VIND","mark.getPosition() ="+mark.getPosition());
				if(oldMarker != null ){
					oldMarker.setIcon(oldBitmap);
				}
				if(mMarkerA != null){
					mMarkerA.remove();
				}
				if(mark != mMarker_Car){
					oldMarker = mark;
					oldBitmap = mark.getIcon();
					BitmapDescriptor bitmap = BitmapDescriptorFactory  
						    .fromResource(R.drawable.icon_en);
					mark.setIcon(bitmap);
					showLocation(mark.getPosition());
				}else{
					Toast.makeText(MainActivity.this, "请重新选择目标地点！", Toast.LENGTH_LONG).show();
				}
				
				return true;
			}
		});
		
		
		//控件初始化
<<<<<<< HEAD
		btn_routePlan = (Button)findViewById(R.id.btn_routePlan);
		btn_me = (Button)findViewById(R.id.btn_me);
		tv_location = (TextView)findViewById(R.id.tv_input_location);
		btn_myLocation = (Button)findViewById(R.id.myLocation);
		btn_start_navi = (Button)findViewById(R.id.btn_start_navi);
		lnl_bottom_menu = (LinearLayout)findViewById(R.id.lnl_bottom_menu);
		lineDetails = (TextView)findViewById(R.id.lineDetails);
		route_bar = (LinearLayout)findViewById(R.id.route_bar);
		pre_view = (ImageButton)findViewById(R.id.pre_view);
		
=======
		btn_startNavi = (Button)findViewById(R.id.btn_start_navi);
		btn_me = (Button)findViewById(R.id.btn_me);
		tv_location = (TextView)findViewById(R.id.tv_input_location);
		btn_myLocation = (Button)findViewById(R.id.myLocation);
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
		
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(GeoListener);
		
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		
		mRouteSearsh = RoutePlanSearch.newInstance();
		mRouteSearsh.setOnGetRoutePlanResultListener(this);
		
		btn_me.setOnClickListener(clickListener);
		tv_location.setOnClickListener(clickListener);
<<<<<<< HEAD
		btn_routePlan.setOnClickListener(clickListener);
		btn_start_navi.setOnClickListener(clickListener);
		pre_view.setOnClickListener(clickListener);
		btn_myLocation.setOnClickListener(clickListener);
			
=======
		btn_startNavi.setOnClickListener(clickListener);
		btn_myLocation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showMyLocation();
			}
		});
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
		
		/**
		 * 单击地图监听
		 */
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				if(oldMarker != null){
					oldMarker.setIcon(oldBitmap);
					oldMarker = null;
				}
				updateMap(point);
			}

			public boolean onMapPoiClick(MapPoi poi) {
				Log.i("vind","poi clicked");
				updateMap(poi.getPosition());
				return true;
			}
		});
	}
	
	private void updateMap(LatLng lat){
		latLng = lat;					//设置为发送的导航经纬度
<<<<<<< HEAD
=======
		
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
		currentPt = lat;
		longitude=currentPt.longitude;
		latitude = currentPt.latitude;
		Log.i(TAG, "longitude=="+longitude+"  latitude=="+latitude);
		if(mMarkerA != null){
			mMarkerA.remove();
			Log.i(TAG, "remove exit marker");
		}
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.nav_destination);
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(latLng)  
		    .icon(bitmap);  
		//在地图上添加Marker，并显示  
		mMarkerA = (Marker)mBaiduMap.addOverlay(option);
		//下面这句必须有
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng));				
		/*
		 * 坐标转地址
		 */
		/***************************latLng = point;	这句在这儿，百度相关文件无法调取，Why????************************************/
		mSearch.reverseGeoCode(new ReverseGeoCodeOption()
		.location(latLng));
	}
	
<<<<<<< HEAD
	private void initMap(){
		  /*
         * 初始化地图信息
         */
		MapStatusUpdate mMapStatusUpdate = null;
       if(lastPlaceFormNet !=null){  //判断是否从服务器获取了数据
    	   lastPlace = lastPlaceFormNet;
			BitmapDescriptor bitmap = BitmapDescriptorFactory  
				    .fromResource(R.drawable.icon_st); 
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions()  
			    .position(lastPlace)  
			    .icon(bitmap);  
			//在地图上添加Marker，并显示  
			if(mMarker_Car != null){
				mMarker_Car.remove();
				Log.i(TAG, "remove exit marker my car");
			}
			mMarker_Car = (Marker)mBaiduMap.addOverlay(option);
       }else{
       	lastPlace = new LatLng(22.53998, 113.908695);
       }
       mMapStatus = new MapStatus.Builder().target(lastPlace).zoom(16.0f)
	             .build();
        mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
 		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.setMyLocationEnabled(true);
	}

=======
	private BDLocationListener myListener= new BDLocationListener(){

		@Override
		public void onReceiveLocation(BDLocation bLocation) {
			// TODO Auto-generated method stub
			Log.i("VIND","   bLocation = "+bLocation+"   bLocation.getCity()  ="+bLocation.getLatitude()+"    bLocation.getProvince() = "+bLocation.getLongitude());
			if (bLocation == null || mBaiduMap == null) {
				Toast.makeText(MainActivity.this, "未找到定位数据", Toast.LENGTH_LONG).show();
				return;
			}
					bdLocation = bLocation;
		                point = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
						BitmapDescriptor bitmap = BitmapDescriptorFactory  
							    .fromResource(R.drawable.icon_marka); 
						//构建MarkerOption，用于在地图上添加Marker  
						OverlayOptions option = new MarkerOptions()  
						    .position(point)  
						    .icon(bitmap);  
						//在地图上添加Marker，并显示  
						if(mMarker_Me != null){
							mMarker_Me.remove();
							Log.i(TAG, "remove exit marker my location");
						}
						mMarker_Me = (Marker)mBaiduMap.addOverlay(option);
							mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(point,ZOOMLEVEL));
					Log.i("VIND","get location successful");
					
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	   public void showMyLocation(){
		   mLocationClient = new LocationClient(this);
			mLocationClient.registerLocationListener(myListener);
			setLocationClient(mLocationClient);
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.i("VIND","thread run");
					mLocationClient.start();
					if (mLocationClient.isStarted()) {
						Log.i("VIND","requestLocation");
						mLocationClient.requestLocation();//向百度发起位置请求
					}
				}
			}).start();
	   }
   
    	public void setLocationClient(LocationClient mLocationClient) {
    		LocationClientOption option = new LocationClientOption();
    		option.setOpenGps(true);// 打开gps
    		option.setCoorType("bd09ll"); // 设置坐标类型
    		option.setScanSpan(500);
    		mLocationClient.setLocOption(option);
    	}
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
	/**
	 * 控件点击监听
	 */
	 public OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()){
			case R.id.btn_me:
				Intent intentMe = new Intent(MainActivity.this,MeActivity.class);
				startActivity(intentMe);
				break;
			case R.id.btn_routePlan:
				startRoutePlan();
				break;
			case R.id.tv_input_location:
				Intent intentSelectLocation = new Intent(MainActivity.this,SelectLocationActivity.class);
				startActivityForResult(intentSelectLocation,1);
				break;
			case R.id.btn_start_navi:
				startNavi();
				break;
			case R.id.myLocation:
				 MyLocation.getInstance().showMyLocation();
				 break;
			case R.id.pre_view:
				backToFirst();
			     break;
				default:
					break;
			}
		}
		 
	 };
	void backToFirst(){
		mBaiduMap.clear();
		initMap();
		btn_myLocation.setVisibility(View.VISIBLE);
		lnl_bottom_menu.setVisibility(View.VISIBLE);
		route_bar.setVisibility(View.GONE);
        
	}
	  
	/*
	 *进行路径规划 		
	 */
	public void startRoutePlan(){
		if(dest==null){
			Toast.makeText(MainActivity.this, "请输入或点击目的地！", Toast.LENGTH_LONG);
			Log.i(TAG, "请输入或点击目的地！");
			return;
		}
		PlanNode startNode =  PlanNode.withLocation(lastPlace);
		PlanNode endNode = PlanNode.withLocation(latLng);
		mRouteSearsh.drivingSearch((new DrivingRoutePlanOption())
                .from(startNode)
                .to(endNode).policy(DrivingPolicy.ECAR_DIS_FIRST));
		lnl_bottom_menu.setVisibility(View.GONE);
		btn_myLocation.setVisibility(View.GONE);
		route_bar.setVisibility(View.VISIBLE);
	} 
	
	/*
	 * 路径规划的结果回调
	 * (non-Javadoc)
	 * @see com.baidu.mapapi.search.route.OnGetRoutePlanResultListener#onGetDrivingRouteResult(com.baidu.mapapi.search.route.DrivingRouteResult)
	 */
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		
		int hours = 0;
		int minutes = 0;
		String str;
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            mBaiduMap.clear();
            PlanDistance = result.getRouteLines().get(0).getDistance();
            PlanDuration = result.getRouteLines().get(0).getDuration();
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
            Log.i("VIND","  PlanDistance = "+PlanDistance+"  PlanDuration = "+PlanDuration);
            hours = PlanDuration/3600;
            if(hours == 0){
            	minutes = PlanDuration/60;
            	str = "约"+minutes+"分钟";
            }else{
            	minutes = (PlanDuration - hours*3600)/60;
            	str = "约"+hours+"小时"+minutes+"分钟";
            }
            lineDetails.setText("全程大约"+PlanDistance/1000+"公里,"+str);
        }
    }
	
	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
	}
	 /**
	  * 开启导航按钮
	  */
	public void startNavi(){
		if(dest==null){
			Toast.makeText(MainActivity.this, "请输入或点击目的地！", Toast.LENGTH_LONG);
			Log.i(TAG, "请输入或点击目的地！");
			return;
		}
<<<<<<< HEAD
		Log.i(TAG, "开始导航");
=======

		
		Log.i(TAG, "开始导航");

		
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
		latitude_int = (int)(latLng.latitude * 1e6);
		longitude_int = (int)(latLng.longitude * 1e6);
		 //TODO Auto-generated method stub
		GlobalVariable globalVariable = new GlobalVariable();
		//isIpcReady = globalVariable.getIsIPCReady();
		Log.i(TAG, "IPC state=="+ipcState);
		if(ipcState){
			//send by ipc
			Log.i(TAG, "IPC 连接发送");
			showDialog();
			sendNaviMsg2OBD(Constant.NAVI_APP_ID, Constant.EVENT_DESTINATION, latLngType, longitude_int, latitude_int, dest);
		}else{
			Log.i(TAG, "远程连接发送");
			String username = LocalDataUtil.ReadSharePre("UserData", "username");
			String imei = Util.getIMEI(MainActivity.this);
			startNavi(username,imei,dest,latLng.latitude,latLng.longitude,latLngType);
			
		}
	}
	
	public void showDialog(){
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("消息发送中，请稍等...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.show();	
		new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mProgressDialog.cancel();
						if (ack) {
							dialogStr = "消息发送成功！";
						} else {
							dialogStr = "消息发送失败，请重新发送";
						}
		
						new AlertDialog.Builder(MainActivity.this).setMessage(dialogStr)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
									}
								}).create().show();
					}
				}, 2000);
		
		
		
	}
	
	/**
	 * 开始导航 by ipc
	 */
	public void sendNaviMsg2OBD(String destID,short cmd,byte type,int longitude,int latitude,String dest_name){
		Log.i("sendNaviMsg2OBD","destID=="+destID+" cmd=="+cmd+" type=="+type+" longitude=="+longitude+" latitude=="+latitude+" dest_name=="+dest_name);
		AppIpcMessage ipcSendMsg = new AppIpcMessage(cmd, type, longitude, latitude, dest_name);
		
		if(ipcMessager != null){
			ipcMessager.sendMessage(destID,ipcSendMsg.encodeNavi());
		}
		
		StringBuffer sb = new StringBuffer();
		byte[] ipcMsg = ipcSendMsg.encodeNavi();
		for(int i=0;i<ipcMsg.length;i++){
			sb.append(",0x"+Integer.toHexString(ipcMsg[i]));
		}
		Log.i(TAG, "ipcMsg:::::"+sb.toString());

	}
	/**
	 * 开始导航 by server
	 */
	private Task StartNaviTask;
	private void startNavi(String username,String imei, String dest,double latitude,double longitude,int latLngtype) {
		Log.i(TAG, "dest=="+dest);
<<<<<<< HEAD
		StartNaviTask = new Task(this,R.id.btn_routePlan);
=======
		StartNaviTask = new Task(this,R.id.btn_start_navi);
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
		String latitude_str = (int)(latitude * 1e6)+"";
		String longitude_str = (int)(longitude * 1e6)+"";
		
		Log.i(TAG, "latitude=="+latitude);
		Log.i(TAG, "longitude=="+longitude);
		Log.i(TAG, "latitude_str=="+latitude_str);
		Log.i(TAG, "longitude_str=="+longitude_str);
		
		StartNaviTask.execute(username,imei,dest,latitude_str,longitude_str,Integer.toString(latLngtype));
	}
	
	OnGetGeoCoderResultListener GeoListener =  new OnGetGeoCoderResultListener(){
		/**
		 * 地点转经纬度
		 */
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
						.show();
				return;
			}
			String strInfo = String.format("纬度：%f 经度：%f",
					result.getLocation().latitude, result.getLocation().longitude);
			
			//定义Maker坐标点  
			LatLng point = result.getLocation();
			latLng = point;								//设置为发送的导航经纬度
			Log.i("MainActivity","latLng=="+latLng);
			Log.i(TAG, "latitude=="+point.latitude+" longitude=="+point.longitude);

			//构建Marker图标  
			BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    .fromResource(R.drawable.icon_markb);  
			//构建MarkerOption，用于在地图上添加Marker  
			OverlayOptions option = new MarkerOptions()  
			    .position(point)  
			    .icon(bitmap);  
			//在地图上添加Marker，并显示  
			if(mMarker_Des != null){
				mMarker_Des.remove();
			}
			mMarker_Des = (Marker)mBaiduMap.addOverlay(option);
			//下面这句必须有
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
					.getLocation()));
			
			Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG).show();
		}

		/**
		 * 经纬度转地点
		 */		
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(MainActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
						.show();
				return;
			}
			Log.i(TAG,"onGetReverseGeoCodeResult");
			tv_location.setText(result.getAddress());
			dest = result.getAddress();
			Toast.makeText(MainActivity.this, result.getAddress(),
					Toast.LENGTH_LONG).show();

		}
	};
	/**
	 * activity返回值
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case 1:
			if(resultCode == RESULT_OK){
				String type = data.getStringExtra("type");
				if(type.equals("suggestion_location")){
					String returnedData = data.getStringExtra("suggestion_location");
					Log.v(TAG, "afterTextChanged "+returnedData);
					tv_location.setText(returnedData);
					location = returnedData;
					/*
					 *  在地图上   画出地点
					 */
					MapUtils.perfomZoom(mBaiduMap);
					mSearch.geocode(new GeoCodeOption().city(city).address(location));
				}
				if(type.equals("search_location")){
					String returnedData = data.getStringExtra("search_location");
					Log.v(TAG, "afterTextChanged "+returnedData);
					tv_location.setText(returnedData);
					mPoiSearch.searchInCity((new PoiCitySearchOption())
							.city(city)
							.keyword(returnedData));
				}
			}
			break;
		}
	}

	/**
	 * 隐藏输入键盘
	 * @param view
	 */
	private void hideInputMethod(View view) {
		InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(manager.isActive()){
			manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		
	}

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
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
	protected void onDestroy() {
		//hideInputMethod();
		if(!isFirstIn){
			mMapView.onDestroy();
			mMapView = null;
<<<<<<< HEAD
//			if(mLocationClient != null && mLocationClient.isStarted()){
//				mLocationClient.stop();
//				mLocationClient.unRegisterLocationListener(myListener);
//			}
			MyLocation.getInstance().onDestory();
=======
			if(mLocationClient != null && mLocationClient.isStarted()){
				mLocationClient.stop();
				mLocationClient.unRegisterLocationListener(myListener);
			}
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
		}
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}


	public void showLocation(LatLng lat){
		latLng = lat;
		currentPt = latLng;
		longitude = currentPt.longitude;
		latitude = currentPt.latitude;
		mSearch.reverseGeoCode(new ReverseGeoCodeOption()
		.location(latLng));
	}
	
	@Override
	public void onRegResult(String result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "result=="+result);
		if(result==null){
			Log.e(TAG, "登录返回值为NULL");
			Toast.makeText(MainActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
			return;
		}
		JSONObject json;
		// TODO Auto-generated method stub
		try {
			json = new JSONObject(result);
			int status = json.getInt("status");
			String msg = json.getString("msg");
		
			switch(status){
			case 0:
				Toast.makeText(MainActivity.this, "开启导航成功！", Toast.LENGTH_SHORT).show();
				Intent startIntent = new Intent(MainActivity.this,NavigationActivity.class);				
				startActivity(startIntent);
				finish();
				break;
			case 1:
				Toast.makeText(MainActivity.this, "设备不在线  "+msg, Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(MainActivity.this, "登录超时！"+msg, Toast.LENGTH_LONG).show();
				Intent reloginIntent = new Intent(MainActivity.this,LoginActivity.class);				
				startActivity(reloginIntent);
				finish();
				break;
			case 3:
				Toast.makeText(MainActivity.this, "请先绑定设备！"+msg, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(MainActivity.this,MeActivity.class);				
				startActivity(intent);
				finish();
				break;
			case 4:
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
				break;
				default:
					break;
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			Log.i("VIND","poi result = "+result.getAllPoi());
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}
	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(MainActivity.this, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
			.show();
		}
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
				mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
						.poiUid(poi.uid));
			// }
			return true;
		}
	}

<<<<<<< HEAD
	
	
	/*
	 * 重写驾车Overlay
	 */
	 private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

	        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
	            super(baiduMap);
	        }

	        @Override
	        public BitmapDescriptor getStartMarker() {
	            if (useDefaultIcon) {
	                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
	            }
	            return null;
	        }

	        @Override
	        public BitmapDescriptor getTerminalMarker() {
	            if (useDefaultIcon) {
	                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
	            }
	            return null;
	        }
	    }

=======
>>>>>>> bbfa51105c7d977a277ea14d91a0bc025cd6368d
}
