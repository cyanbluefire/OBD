package com.neoway.vehiclebeta1.task;

import android.os.AsyncTask;
import android.util.Log;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.callback.RegCallback;
import com.neoway.vehiclebeta1.net.HttpManager;

public class Task extends AsyncTask<String, Void, String> {
	int id;							//用id来区分不同Task
	
	private RegCallback mCallback;
	
	public Task(RegCallback mCallback,int id){
		this.mCallback = mCallback;
		this.id = id;
	}

	@Override
	protected String doInBackground(String... str) {
		if(str.length>0){	
			String result = null;	
			switch(id){
			case R.id.btn_bind:
				Log.e("Task", "btn_bind");
				result = HttpManager.getInstance().bind(str[0],str[1],str[2]);
				break;
			case R.id.btn_login:
				Log.e("Task", "btn_login");
				result = HttpManager.getInstance().login(str[0],str[1],str[2]);
				break;
			case R.id.btn_register:
				Log.e("Task", "btn_register");
				result = HttpManager.getInstance().register(str[0],str[1]);
				break;
			case R.id.exitBtn0:
				Log.e("Task", "exitBtn0");
				result = HttpManager.getInstance().exit(str[0],str[1]);
				break;
			case R.id.btn_start_navi:
				Log.e("Task", "btn_start_navi");
				result = HttpManager.getInstance().startNavi(str[0],str[1],str[2],str[3],str[4],str[5]);
				break;
			case R.id.btn_cancel_navi:
				Log.e("Task", "btn_cancel_navi");
				result = HttpManager.getInstance().cancelNavi(str[0],str[1]);
				break;
			default:
				break;
			}
			Log.i("cyan", "result=="+result);
			return result;			
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(mCallback!=null){
			Log.i("cyan","callback");
			mCallback.onRegResult(result);
		}
		
	}

}
