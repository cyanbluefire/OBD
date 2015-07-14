package com.neoway.vehiclebeta1.utils;

import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;

public class MapUtils {
	/**
	 * 处理缩放 sdk 缩放级别范围： [3.0,19.0]
	 */
	public static void perfomZoom(BaiduMap mBaiduMap) {
		try {
			String zoomStr = "15";			//15表示缩放500m
			float zoomLevel = Float.parseFloat(zoomStr);
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
			mBaiduMap.animateMapStatus(u);
		} catch (NumberFormatException e) {
			Toast.makeText(MyApplication.getContext(), "请输入正确的缩放级别", Toast.LENGTH_SHORT).show();
		}
	}
}
