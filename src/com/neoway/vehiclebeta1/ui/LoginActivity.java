package com.neoway.vehiclebeta1.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.callback.RegCallback;
import com.neoway.vehiclebeta1.constant.Constant;
import com.neoway.vehiclebeta1.data.LocalDataUtil;
import com.neoway.vehiclebeta1.data.LoginCommand;
import com.neoway.vehiclebeta1.data.ObdFrame;
import com.neoway.vehiclebeta1.data.TextUtil;
import com.neoway.vehiclebeta1.utils.*;
import com.neoway.vehiclebeta1.net.SocketManager;
import com.neoway.vehiclebeta1.service.AppService;
import com.neoway.vehiclebeta1.task.*;

import android.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements RegCallback{
	private static final String TAG = "LoginActivity";
	private EditText et_username;
    private EditText et_userpassword;
    private Button btn_login;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView v_goRegister;
    private Context context;
    private String username;
	private String password;
    private String strResponse;
    private String md5;
    
	public static  String username_area;
	public static byte[] login_data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		setContentView(R.layout.activity_login);
		context = LoginActivity.this;

		sp = getSharedPreferences("userInfo", MODE_PRIVATE);		
		et_username = (EditText) findViewById(R.id.edt_username_login);
		et_userpassword = (EditText) findViewById(R.id.edt_userpassword);
		btn_login = (Button) findViewById(R.id.btn_login);
		v_goRegister = (TextView)findViewById(R.id.txt_goRegister);
		
		btn_login.setOnClickListener(clickListener);
		v_goRegister.setOnClickListener(clickListener);
		
		et_username.setText("15866663333");
		et_userpassword.setText("zxc123456");

		
	}
	/**
	 * 长连接发送消息
	 */

	/**
	 * 点击监听
	 */
	 public OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				switch(view.getId()){
				//点击登录
				case R.id.btn_login:					
					if(!Util.haveNetConnect(getApplicationContext())){
						Util.showToast(getApplicationContext(), R.string.toast_disconnect);
						return;
					}
					//获得输入内容
					username = et_username.getText().toString();
					password = et_userpassword.getText().toString();
					Log.i(TAG, "username=="+username+" "+"password=="+password);
					if(Util.isEmpty(username)||Util.isEmpty(password)){
						Util.showToast(getApplicationContext(), R.string.toast_not_null);
						return;
					}
					if(!Util.isPhoneNumber(username)){
						Util.showToast(getApplicationContext(), R.string.toast_username_is_not_phone);
						return;
					}
					if(!Util.isPasswordFormated(password)){
						Util.showToast(getApplicationContext(), R.string.toast_pass_format_error);
						return;
					}
					md5 = EncryptUtil.md5(password);
					Log.i(TAG, "md5=="+md5);
					//login(username,md5);
					
					/*
					 * 长连接登录
					 */				
					username_area = "0086"+username;	//不能删除，用于登录成功之后存储在本地 socketManager handleServerResult
//					SocketManager.loginBySocket(username,md5,Util.getIMEI(LoginActivity.this));
					
					 /*
			         * 启动与后台通信的service
			         */
					setLoginBytes();
			        Intent intentService = new Intent(LoginActivity.this,AppService.class);
					startService(intentService);
					
					
					break;
				case R.id.txt_goRegister:
					Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
					startActivity(intent);
					break;
					default:
						break;
				}
			}
		};
		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
	
	/**
	 * socket 登录 （放到这儿是因为每次连接都要登录）
	 */
	public  void setLoginBytes(){
		
		 Log.i(TAG, "account=="+username);
	        byte[] AREA_NUM = new byte[4];
	        AREA_NUM =("0086").getBytes();
	        				        
	        byte[] ACCOUNT = new byte[11];
	        System.arraycopy(username.getBytes(), 0, ACCOUNT, 0, username.getBytes().length > ACCOUNT.length ?
	                ACCOUNT.length : username.getBytes().length);
	         
	        byte[] IMEI = new byte[15];
	        String imei = Util.getIMEI(LoginActivity.this);
	        System.arraycopy(imei.getBytes(), 0, IMEI, 0, imei.getBytes().length > IMEI.length ?
	        		IMEI.length : imei.getBytes().length);
	        
	        byte[] PASSWORD = md5.getBytes();
	        
	        byte[] commandParams = new byte[PASSWORD.length+30];
	        System.arraycopy(IMEI, 0, commandParams, 0, IMEI.length); //15
	        System.arraycopy(AREA_NUM, 0, commandParams, 15, AREA_NUM.length); //4
	        System.arraycopy(ACCOUNT, 0, commandParams, 19, ACCOUNT.length); //11
	        System.arraycopy(PASSWORD, 0, commandParams, 30, PASSWORD.length); //
	        byte[] msgBody = TextUtil.toMessageBodyBytes(commandParams, ObdFrame.LOGIN_COMMAND_ID);
	        
	        login_data = (new ObdFrame()).encode(msgBody);
			
		//		new SocketManager().dispatchMsg(login_data);
	}
	
	/**
	 * login by http
	 */
	private ProgressBar proBar;
	private Task loginTask;
	private void login(String username, String password) {	
		proBar = (ProgressBar)findViewById(R.id.progras_post_login);
		proBar.setVisibility(View.VISIBLE);
		loginTask = new Task(this,R.id.btn_login);
		loginTask.execute(username, password,Util.getIMEI(LoginActivity.this));
	}

	@Override
	public void onRegResult(String result) {
		if(result==null){
			Log.e(TAG, "登录返回值为NULL");
			Toast.makeText(LoginActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
			return;
		}
		proBar.setVisibility(View.INVISIBLE);
		JSONObject json;
		// TODO Auto-generated method stub
		try {
			json = new JSONObject(result);
			int status = json.getInt("status");
			String msg = json.getString("msg");
		
			switch(status){
			case 0:
				Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();	
				
				JSONArray device_ayyry_json = json.getJSONArray("device");
				//保存已绑定过的设备
				if(device_ayyry_json != null){
					for(int i=0;i<device_ayyry_json.length();i++){
						JSONObject device_json = device_ayyry_json.getJSONObject(i);
						String ura = device_json.getString("ura");
						String ssid = device_json.getString("ssid");
						String passWord = device_json.getString("psw");
						String token = device_json.getString("token");	
						Log.i(TAG, "device"+i+"=="+device_json.toString());
					}
				}
				LocalDataUtil.SaveSharedPre("username", username, "UserData");		//存的是加了 0086 的电话号码
				LocalDataUtil.SaveSharedPre("LoginStatus", "0", "UserData");
				
				
				Intent intent = new Intent(LoginActivity.this,MeActivity.class);
				startActivity(intent);
				finish();
				break;
			case 1:
				Toast.makeText(LoginActivity.this, "该用户名不存在存在", Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG).show();
				break;
			case 3:
				Toast.makeText(LoginActivity.this, "当前已登录用户超出上限5个", Toast.LENGTH_LONG).show();
				break;
			case 4:
				Toast.makeText(LoginActivity.this, "错误"+msg, Toast.LENGTH_LONG).show();
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