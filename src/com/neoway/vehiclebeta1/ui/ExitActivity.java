package com.neoway.vehiclebeta1.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.callback.RegCallback;
import com.neoway.vehiclebeta1.constant.Constant;
import com.neoway.vehiclebeta1.data.LocalDataUtil;
import com.neoway.vehiclebeta1.task.Task;
import com.neoway.vehiclebeta1.utils.ActivityCollector;
import com.neoway.vehiclebeta1.utils.Util;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ExitActivity extends Activity implements RegCallback{
	private static final String TAG = "ExitActivity";
	private LinearLayout layout;
	private String username="15866669999";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_exit);
		ActivityCollector.addActivity(this);			
		//dialog=new MyDialog(this);
		layout=(LinearLayout)findViewById(R.id.exit_layout);
		layout.setOnClickListener(new OnClickListener() {		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！", 
						Toast.LENGTH_SHORT).show();	
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	public void exitbutton1(View v) {  
    	this.finish();    	
      }  
	public void exitbutton0(View v) {  

    	exit(username,Util.getIMEI(ExitActivity.this));    	
    	ActivityCollector.finishAll();
      }  
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);

	}


	private Task exitTask;
	private void exit(String username, String imei) {	
		exitTask = new Task(this,R.id.exitBtn0);
		exitTask.execute(username, imei);
	}

	@Override
	public void onRegResult(String result) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Exit result=="+result);
		LocalDataUtil.SaveSharedPre("LoginStatus", "1", "UserData");
	}
	
}
