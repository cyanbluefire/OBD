package com.neoway.vehiclebeta1.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.callback.RegCallback;
import com.neoway.vehiclebeta1.data.LocalDataUtil;
import com.neoway.vehiclebeta1.task.Task;
import com.neoway.vehiclebeta1.utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NavigationActivity extends Activity implements RegCallback{
	private static final String TAG = "NavigationActivity";
	Button btn_cancleNavi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_navigation);
		btn_cancleNavi = (Button)findViewById(R.id.btn_cancel_navi);
		btn_cancleNavi.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cancelNavi(LocalDataUtil.ReadSharePre("UserData", "username"),Util.getIMEI(NavigationActivity.this));
				
			}
		});
	}
	private Task NaviTask;
	private void cancelNavi(String username, String imei) {	
		NaviTask = new Task(this,R.id.btn_cancel_navi);
		NaviTask.execute(username, imei);
	}
	@Override
	public void onRegResult(String result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "result=="+result);
		if(result==null){
			Log.e(TAG, "��¼����ֵΪNULL");
			Toast.makeText(NavigationActivity.this, "���������쳣", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(NavigationActivity.this, "���������ɹ���", Toast.LENGTH_SHORT).show();
				Intent startIntent = new Intent(NavigationActivity.this,NavigationActivity.class);				
				startActivity(startIntent);
				finish();
				break;
			case 1:
				Toast.makeText(NavigationActivity.this, msg, Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(NavigationActivity.this, "��¼��ʱ��"+msg, Toast.LENGTH_LONG).show();
				Intent reloginIntent = new Intent(NavigationActivity.this,LoginActivity.class);				
				startActivity(reloginIntent);
				finish();
				break;
			case 3:
				Toast.makeText(NavigationActivity.this, "���Ȱ��豸��"+msg, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(NavigationActivity.this,MeActivity.class);				
				startActivity(intent);
				finish();
				break;
			case 4:
				Toast.makeText(NavigationActivity.this, msg, Toast.LENGTH_LONG).show();
				break;
				default:
					break;
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
