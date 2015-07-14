package com.neoway.vehiclebeta1.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.callback.RegCallback;
import com.neoway.vehiclebeta1.data.ObdFrame;
import com.neoway.vehiclebeta1.data.RegisterCommand;
import com.neoway.vehiclebeta1.net.SocketManager;
import com.neoway.vehiclebeta1.task.Task;
import com.neoway.vehiclebeta1.utils.ActivityCollector;
import com.neoway.vehiclebeta1.utils.EncryptUtil;
import com.neoway.vehiclebeta1.utils.Util;

public class RegisterActivity extends Activity implements RegCallback{
	private static final String TAG = "RegisterActivity";
	private EditText et_username;
	private EditText et_userpw;
	private EditText et_userpw_confirm;
	private Button btn_register;
	private int userNameLenth;
	private String username;
	private String password;
	private String pw_confirm;
	private Context context;
	private String response;
	
	private String AREA_NUM= "0086";
	String[] RegisInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);
		context = RegisterActivity.this;
		et_username = (EditText)findViewById(R.id.edt_username_register);
		et_userpw = (EditText)findViewById(R.id.edt_userpassword_register);
		et_userpw_confirm=(EditText)findViewById(R.id.edt_userpassword_register_confirm);
		btn_register = (Button)findViewById(R.id.btn_register);
		
		btn_register.setOnClickListener(clickListener);
		
		et_username.setText("15866663333");
		et_userpw.setText("zxc123456");
		et_userpw_confirm.setText("zxc123456");
		
	}

	public OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			/**
			 * 注册
			 */
			case R.id.btn_register:
				//检查是否有网络
				if (!Util.haveNetConnect(getApplicationContext())) {
					Util.showToast(getApplicationContext(),
							R.string.toast_disconnect);
					return;
				}
				//获得输入内容
				username = et_username.getText().toString();
				password = et_userpw.getText().toString();
				pw_confirm = et_userpw_confirm.getText().toString();
				Log.i(TAG, "username=="+username+" "+"password=="+password+" "+"pw_confirm=="+pw_confirm);
				if (Util.isEmpty(username)
						|| Util.isEmpty(password) || Util.isEmpty(pw_confirm)) {
					Util.showToast(getApplicationContext(),
							R.string.toast_not_null);
					return;
				}
				if (!Util.isPhoneNumber(username)) {
					Util.showToast(getApplicationContext(),
							R.string.toast_username_is_not_phone);
					return;
				}
				if (!(password.equals(pw_confirm))) {
					Util.showToast(getApplicationContext(),
							R.string.toast_pass1_not_equals_pass2);
					return;
				}
				if ((password.equals(pw_confirm)) && !Util.isPasswordFormated(password)) {
					Util.showToast(getApplicationContext(),
							R.string.toast_pass_format_error);
					return;
				}
				password = EncryptUtil.md5(password);
				Log.i(TAG, "md5=="+password);
				/*
				 * Http发送
				 */
				register(AREA_NUM+username, password);	//添加了国家区号
				
				/*
				 * socket发送
				 */
//				RegisterCommand mRegisterCommand = new RegisterCommand(username, password);
//				byte[] registerData = (new ObdFrame(mRegisterCommand)).encode();
//				(new SocketManager()).dispatchMsg(registerData);
				break;
				
				default:
					break;
			}
		}
	};
	/*
	 * 长连接注册
	 */
	
	
	private ProgressBar proBar;
	private Task regTask;
	private void register(String username, String password) {	
		proBar = (ProgressBar)findViewById(R.id.progras_post);
		proBar.setVisibility(View.VISIBLE);
		regTask = new Task(this,R.id.btn_register);
		regTask.execute(username, password);
	}

	@Override
	public void onRegResult(String result) {
		if(result==null){
			Log.e(TAG, "注册返回值为NULL");
			Toast.makeText(RegisterActivity.this, "异常,注册失败！", Toast.LENGTH_SHORT).show();
			return;
		}
		proBar.setVisibility(View.INVISIBLE);
		JSONObject json;
		// TODO Auto-generated method stub
		try {
			json = new JSONObject(result);
			int status = json.getInt("status");
//			String msg = json.getString("msg");
		
			switch(status){
			case 0:
				Toast.makeText(RegisterActivity.this, "注册成功！请用新账号登录!", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
				startActivity(intent);
				onDestroy();
				break;
			case 1:
				Toast.makeText(RegisterActivity.this, "该用户名已存在，请重新输入", Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(RegisterActivity.this, "错误", Toast.LENGTH_LONG).show();
				break;
				default:
					break;
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}
}
