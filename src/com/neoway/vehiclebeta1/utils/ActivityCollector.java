package com.neoway.vehiclebeta1.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
/**
 * 实现随时随地关闭所有activity,退出程序
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
		Log.v("cyan", "finishAll 注销");
		for(Activity activity:activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
		activities.clear();
	}
	
}
