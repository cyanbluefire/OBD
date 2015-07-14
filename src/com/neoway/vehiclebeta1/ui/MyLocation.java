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
    private static final float ZOOMLEVEL = 16.0f;//���õ�ͼ���ż���Ϊ200��
    
	
	
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
					mLocationClient.requestLocation();//��ٶȷ���λ������
				}
			}
		}).start();
   }

	public void setLocationClient(LocationClient mLocationClient) {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(500);
		mLocationClient.setLocOption(option);
	}
	
	private BDLocationListener myListener= new BDLocationListener(){

		@Override
		public void onReceiveLocation(BDLocation bLocation) {
			// TODO Auto-generated method stub
			Log.i("VIND","   bLocation = "+bLocation+"   bLocation.getCity()  ="+bLocation.getLatitude()+"    bLocation.getProvince() = "+bLocation.getLongitude());
			if (bLocation == null || MainActivity.mBaiduMap == null) {
				Toast.makeText(MyApplication.getContext(), "δ�ҵ���λ����", Toast.LENGTH_LONG).show();
				return;
			}
					bdLocation = bLocation;
		                point = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
						BitmapDescriptor bitmap = BitmapDescriptorFactory  
							    .fromResource(R.drawable.icon_marka); 
						//����MarkerOption�������ڵ�ͼ�����Marker  
						OverlayOptions option = new MarkerOptions()  
						    .position(point)  
						    .icon(bitmap);  
						//�ڵ�ͼ�����Marker������ʾ  
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
