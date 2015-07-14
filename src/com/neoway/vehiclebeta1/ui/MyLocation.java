package com.neoway.vehiclebeta1.ui;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.utils.MyApplication;

import android.util.Log;
import android.widget.Toast;

public class MyLocation {
	LocationClient mLocationClient;
    BDLocation bdLocation;	
    LatLng point;
    private Marker mMarker_Me;
    static MyLocation instance;
    private static final float ZOOMLEVEL = 16.0f;//设置地图缩放级别为200米
    
	
	
	public static MyLocation getInstance(){
		if(instance == null){
			instance = new MyLocation();
		}
		return instance;
	}
	
	public void showMyLocation(){
	    mLocationClient = new LocationClient(MyApplication.getContext());
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
	
	private BDLocationListener myListener= new BDLocationListener(){

		@Override
		public void onReceiveLocation(BDLocation bLocation) {
			// TODO Auto-generated method stub
			Log.i("VIND","   bLocation = "+bLocation+"   bLocation.getCity()  ="+bLocation.getLatitude()+"    bLocation.getProvince() = "+bLocation.getLongitude());
			if (bLocation == null || MainActivity.mBaiduMap == null) {
				Toast.makeText(MyApplication.getContext(), "未找到定位数据", Toast.LENGTH_LONG).show();
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
							Log.i("VIND", "remove exit marker my location");
						}
						mMarker_Me = (Marker)MainActivity.mBaiduMap.addOverlay(option);
						MainActivity.mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(point,ZOOMLEVEL));
					Log.i("VIND","get location successful");
					
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	   public void onDestory(){
		   if(mLocationClient != null && mLocationClient.isStarted()){
				mLocationClient.stop();
				mLocationClient.unRegisterLocationListener(myListener);
			}
	   }
}
