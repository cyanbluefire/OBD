package com.neoway.vehiclebeta1.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.R.id;
import com.neoway.vehiclebeta1.R.layout;
import com.neoway.vehiclebeta1.callback.RegCallback;
import com.neoway.vehiclebeta1.callback.SocketResultCallback;
import com.neoway.vehiclebeta1.constant.Constant;
import com.neoway.vehiclebeta1.data.LocalDataUtil;
import com.neoway.vehiclebeta1.data.ObdFrame;
import com.neoway.vehiclebeta1.data.TextUtil;
import com.neoway.vehiclebeta1.net.HttpManager;
import com.neoway.vehiclebeta1.net.SocketManager;
import com.neoway.vehiclebeta1.net.WifiAdmin;
import com.neoway.vehiclebeta1.net.WifiAutoConnectManager.WifiCipherType;
import com.neoway.vehiclebeta1.task.Task;
import com.neoway.vehiclebeta1.utils.ActivityCollector;
import com.neoway.vehiclebeta1.utils.EncryptUtil;
import com.neoway.vehiclebeta1.utils.Util;
import com.obdphoneipc.ObdPhoneIpcMessager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;
import static com.neoway.vehiclebeta1.data.GlobalVariable.ipcState;

public class MeActivity extends Activity implements RegCallback{
private static final String TAG = "MeActivity";
private Button btn_bind;
private Button btn_login;
private Button btn_ipc_state;

private boolean menu_display = false;

private PopupWindow menuWindow;//menu弹窗
private LayoutInflater inflater;
private View layout;
private LinearLayout mClose;//menu中的退出布局
private LinearLayout mCloseBtn;//menu中的退出按钮

private ProgressBar proBar;
private Task bindTask;
private WifiAdmin mWifiAdmin; 

//WIFI配置信息
String SSID ="360WiFi";
String passWord =  "yangle123";
//String SSID = "VehicleBaby";
//String passWord = "baby123456";
WifiCipherType type = WifiCipherType.WIFICIPHER_WPA;
private String token  = "OBDtoken";

String uraStr;
String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_me);
        
        btn_bind = (Button)findViewById(R.id.btn_bind);
        btn_login = (Button)findViewById(R.id.btn_login_main);
        Button btn_wificonnect = (Button)findViewById(R.id.btn_wifiConnect);
        Button btn_BmapNavi = (Button)findViewById(R.id.btn_BmapNavi);
    	btn_ipc_state =(Button)findViewById(R.id.btn_ipc_state);
    	
        btn_login.setOnClickListener(clickListener);
        btn_bind.setOnClickListener(clickListener);
        btn_wificonnect.setOnClickListener(clickListener);
        btn_BmapNavi.setOnClickListener(clickListener);
        btn_ipc_state.setOnClickListener(clickListener);
        
        mWifiAdmin = new WifiAdmin(MeActivity.this);
        
        
        /*
         * 已登录，“登录”按钮不可见
         */
//        if("0".equals(LocalDataUtil.ReadSharePre("UserData", "LoginStatus"))){
//        	btn_login.setVisibility(View.INVISIBLE);
//        }
    }
    
    public OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch(view.getId()){
			//点击登录
			case R.id.btn_login_main:
				Intent intentLogin = new Intent(MeActivity.this,LoginActivity.class);
				startActivity(intentLogin);
				finish();
				break;
			case R.id.btn_bind:
				byte[] uraByte = TextUtil.createUra(Constant.URA_DEVICE_TYPE_OBD,TextUtil.String2Byte("123456789012345678", 20),
						TextUtil.String2Byte("123456789054321", 20));
				
				StringBuffer sb = new StringBuffer();
				for(int i=0;i<uraByte.length;i++){
					sb.append(",0x"+Integer.toHexString(uraByte[i]));
				}
				Log.i(TAG, "uraByte:::::"+sb.toString());
				
				uraStr = new String(uraByte);
				LocalDataUtil.SaveSharedPre("uraStr", uraStr, "UserData");				
				
				String imei = Util.getIMEI(MeActivity.this);
				LocalDataUtil.SaveSharedPre("imei", imei, "UserData");				

				username = LocalDataUtil.ReadSharePre("UserData", "username");
				//bind(username,uraStr,Util.getIMEI(MeActivity.this));			//*************SN码待确定		
				bindBySocket(username,uraStr,Util.getIMEI(MeActivity.this));
				break;
			case R.id.btn_wifiConnect:
				Log.i(TAG, "click 连接wifi");
				token = LocalDataUtil.ReadSharePre("UserData", "token");
				mWifiAdmin.connectWIFI(SSID, passWord, type,token);
				break;
			case R.id.btn_BmapNavi:
				Intent intentSetLocation = new Intent(MeActivity.this,MainActivity.class);
				startActivity(intentSetLocation);
				break;
			case R.id.btn_ipc_state:
				if(ipcState){
					ipcState = false;
					btn_ipc_state.setText("远程通信");
				}else{
					ipcState = true;
					btn_ipc_state.setText("ipc通信");

				}
				break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 绑定
	 */
	public void bind(String username,String uraStr,String PhoneImei){
		proBar = (ProgressBar)findViewById(R.id.progras_bind);
		proBar.setVisibility(View.VISIBLE);
		bindTask = new Task(this,R.id.btn_bind);
		bindTask.execute(username, uraStr,PhoneImei);
	}
	
	/**
	 * 绑定 by socket
	 */
	public void bindBySocket(String account,String uraStr,String PhoneImei){
		proBar = (ProgressBar)findViewById(R.id.progras_bind);
		proBar.setVisibility(View.VISIBLE);
        
        byte[] commandParams = new byte[65];
        System.arraycopy(account.getBytes(), 0, commandParams, 0,account.length());
        System.arraycopy(uraStr.getBytes(), 0, commandParams, 15,uraStr.length());
        
        byte[] bind_frame = new ObdFrame().encode(TextUtil.toMessageBodyBytes(commandParams, ObdFrame.BIND_COMMAND_ID));
		new SocketManager().dispatchMsg(bind_frame);
        
	}
	
	@Override
	public void onRegResult(String result) {
		Log.i(TAG, "绑定返回值=="+result);
		// TODO Auto-generated method stub
		if(result==null){
			Log.e(TAG, "绑定返回值为NULL");
			Toast.makeText(MeActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(MeActivity.this, "绑定成功！等待通过WIFI连接OBD...", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "bind ok resule==="+json.toString());
				/*
				 * 解密AES	
				 */	
				//String passWord = EncryptUtil.decrypt(json.getString("psw"), "VehicleBaby");
				String passWord = json.getString("psw");
				String ssid =json.getString("ssid");
				String token = json.getString("token");				
//				if(ipcMessager == null){
//				   ipcMessager = new ObdPhoneIpcMessager(Constant.APPID, token);
//				}
				LocalDataUtil.SaveSharedPre(token, token, "UserData");
				mWifiAdmin.connectWIFI(ssid, passWord, type,token);
				break;
			case 1:
				Toast.makeText(MeActivity.this, "错误1"+msg, Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(MeActivity.this, "错误2"+msg, Toast.LENGTH_LONG).show();
				/**
				 * 异常!!
				 * 解除绑定
				 */
				new UnBindTask().execute(username,uraStr,Util.getIMEI(MeActivity.this));
				break;
			case 3:
				Toast.makeText(MeActivity.this, "登录超时重新登录"+msg, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(MeActivity.this,LoginActivity.class);
				startActivity(intent);
				break;
			case 4:
				Toast.makeText(MeActivity.this, "错误4"+msg, Toast.LENGTH_LONG).show();
				break;
			case 5:
				Toast.makeText(MeActivity.this, "错误5"+msg, Toast.LENGTH_LONG).show();
				break;
				default:
					break;
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 解绑类
	 * @author 20140707369
	 *
	 */
	class UnBindTask extends AsyncTask<String,Void, String>{

		@Override
		protected String doInBackground(String... str) {
			return HttpManager.getInstance().unBind(str[0],str[1],str[2]);
			
		}
		
		protected void onPostExecute(String result) {
			Log.i(TAG, "解除绑定返回值=="+result);
			// TODO Auto-generated method stub
			if(result==null){
				Log.e(TAG, "解除绑定返回值为NULL");
				Toast.makeText(MeActivity.this, "网络连接异常", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(MeActivity.this, "解除绑定", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(MeActivity.this, msg, Toast.LENGTH_LONG).show();
					break;
				case 2:
					Toast.makeText(MeActivity.this, "错误: "+msg, Toast.LENGTH_LONG).show();
					break;
				case 3:
					Toast.makeText(MeActivity.this, msg, Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				return;
			
		}
		
	}
	/**
	 * 按键处理：包括menu和back
	 */
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		
        	if(menu_display){         //如果 Menu已经打开 ，先关闭Menu
        		menuWindow.dismiss();
        		menu_display = false;
        		}
        	else {
        		Log.v("cyan", "退出但不注销");
        		onDestroy();
        	}
    	}
    	
    	else if(keyCode == KeyEvent.KEYCODE_MENU){   //获取 Menu键			
			if(!menu_display){
				//获取LayoutInflater实例
				//inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				inflater= (LayoutInflater)LayoutInflater.from(this);
				
				//这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
				//该方法返回的是一个View的对象，是布局中的根
				layout = inflater.inflate(R.layout.main_menu, null);
				
				//下面我们要考虑了，我怎样将我的layout加入到PopupWindow中呢？？？很简单
				menuWindow = new PopupWindow(layout,LayoutParams.FILL_PARENT,  LayoutParams.WRAP_CONTENT); //后两个参数是width和height
				//menuWindow.showAsDropDown(layout); //设置弹出效果
				//menuWindow.showAsDropDown(null, 0, layout.getHeight());
				menuWindow.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
				//如何获取我们main中的控件呢？也很简单
				mClose = (LinearLayout)layout.findViewById(R.id.menu_close);
				mCloseBtn = (LinearLayout)layout.findViewById(R.id.menu_close_btn);
				
				
				//下面对每一个Layout进行单击事件的注册吧。。。
				//比如单击某个MenuItem的时候，他的背景色改变
				//事先准备好一些背景图片或者颜色
				mCloseBtn.setOnClickListener (new View.OnClickListener() {					
					
					public void onClick(View arg0) {						
						//Toast.makeText(Main.this, "退出", Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
			        	intent.setClass(MeActivity.this,ExitActivity.class);
			        	startActivity(intent);
			        	menuWindow.dismiss(); //响应点击事件之后关闭Menu
					}
				});				
				menu_display = true;				
			}else{
				//如果当前已经为显示状态，则隐藏起来
				menuWindow.dismiss();
				menu_display = false;
				}
			
			return false;
		}
    	return false;
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
		finish();
	}


    
}
