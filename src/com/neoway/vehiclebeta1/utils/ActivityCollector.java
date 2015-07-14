package com.neoway.vehiclebeta1.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
/**
 * ʵ����ʱ��عر�����activity,�˳�����
 * @author cuiyan
 *
 */
public class ActivityCollector {
	public static List<Activity> activities = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity){
		activities.add(activity);		
	}
	
	public static void removeActivity(Activity activity){
		activities.remove(activity);
	}
	
	public static void finishAll(){
		Log.v("cyan", "finishAll ע��");
		for(Activity activity:activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
		activities.clear();
	}
	
}
