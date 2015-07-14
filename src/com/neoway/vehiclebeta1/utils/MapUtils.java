package com.neoway.vehiclebeta1.utils;

import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;

public class MapUtils {
	/**
	 * �������� sdk ���ż���Χ�� [3.0,19.0]
	 */
	public static void perfomZoom(BaiduMap mBaiduMap) {
		try {
			String zoomStr = "15";			//15��ʾ����500m
			float zoomLevel = Float.parseFloat(zoomStr);
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
			mBaiduMap.animateMapStatus(u);
		} catch (NumberFormatException e) {
			Toast.makeText(MyApplication.getContext(), "��������ȷ�����ż���", Toast.LENGTH_SHORT).show();
		}
	}
}
