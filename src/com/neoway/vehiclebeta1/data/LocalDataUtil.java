package com.neoway.vehiclebeta1.data;

import com.neoway.vehiclebeta1.utils.MyApplication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class LocalDataUtil {

	private static final String TAG = "SaveDataUtil";

	public static void SaveSharedPre(String dataName,String data,String fileName){
		SharedPreferences sp = MyApplication.getContext().getSharedPreferences(fileName, 0);
	    SharedPreferences.Editor editor = sp.edit();
	    editor.putString(dataName, data);
	    editor.commit();
		Log.i(TAG, "Save "+dataName+"="+data+" Successfully");
		
	}
	public static String ReadSharePre(String fileName,String dataName){
		SharedPreferences sp = MyApplication.getContext().getSharedPreferences(fileName, 0);
			String data = sp.getString(dataName, "");
			Log.i(TAG, "Read "+dataName+"="+data+" Successfully");
			return data;
	}


}
