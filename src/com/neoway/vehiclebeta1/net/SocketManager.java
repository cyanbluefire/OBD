package com.neoway.vehiclebeta1.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.neoway.vehiclebeta1.data.LocalDataUtil;
import com.neoway.vehiclebeta1.data.ObdFrame;
import com.neoway.vehiclebeta1.data.ObdMessage;
import com.neoway.vehiclebeta1.data.TextUtil;
import com.neoway.vehiclebeta1.net.WifiAutoConnectManager.WifiCipherType;
import com.neoway.vehiclebeta1.service.AppService;
import com.neoway.vehiclebeta1.ui.LoginActivity;
import com.neoway.vehiclebeta1.ui.MeActivity;
import com.neoway.vehiclebeta1.utils.MyApplication;
import com.neoway.vehiclebeta1.utils.ToastUtils;
import com.neoway.vehiclebeta1.utils.Util;

import static com.neoway.vehiclebeta1.ui.LoginActivity.username_area;


public class SocketManager {
	private static AppService mainService;	
	private static final String TAG = "SocketManager";	
	
	private static WifiAdmin mWifiAdmin; 
	static WifiCipherType type = WifiCipherType.WIFICIPHER_WPA;

	public SocketManager(){
		
	}
	
	public SocketManager(AppService Service) {
		// TODO Auto-generated constructor stub
		mainService = Service;
	}
	
	public void dispatchMsg(byte[] data){
		if(mainService==null){
			Log.i(TAG, "mainService is null");
			return;
		}else{
			mainService.sendMessageToServer(data);
		}
	}
	
	
	
	
	/**
	 * 处理服务器返回结果
	 * @param result
	 */
	public static void handleServerResult(short commandId, byte[] result) {
		ToastUtils mToast = new ToastUtils();
		switch (commandId) {

		case ObdFrame.BIND_COMMAND_ID:
		{
			byte status = result[0];
			Log.i(TAG, "result=="+Util.printByteArray(result));
			
			switch(status){
			case 0x00:
				mToast.showToast("绑定成功！");
				Log.i(TAG, "绑定成功！");
				byte[] msg = new byte[result.length-1];
				byte[] ssid = new byte[15];
				byte[] wifi_psw = new byte[10];
				byte[] token = new byte[20];
				
				System.arraycopy(msg, 0, ssid, 0, 15 );
		        System.arraycopy(msg, 15, wifi_psw, 0, 10 );
		        System.arraycopy(msg, 25, token, 0, 20 );
		        
		        String ssidStr = new String(ssid);
		        String wifiPasswordStr = new String(wifi_psw);
		        String tokenStr = new String(token);
		        
		        Log.i(TAG, "ssid=="+ssidStr+" wifi_psw=="+wifiPasswordStr+" token=="+tokenStr);
		        LocalDataUtil.SaveSharedPre("token", new String(token), "UserData");
		        mWifiAdmin = new WifiAdmin(MyApplication.getContext());
				mWifiAdmin.connectWIFI(ssidStr, wifiPasswordStr, type,tokenStr);
				break;
			case 0x01:
				Log.i(TAG, "此设备和此用户已经绑定！");
				mToast.showToast("此设备和此用户已经绑定");

				break;
			case 0x02:
				Log.i(TAG, "设备当前不在线");
				mToast.showToast("设备当前不在线");

				break;
			case 0x03:
				Log.i(TAG, "异常!");
				mToast.showToast("异常!");

				break;
				default:
					break;
			}
		}
			break;
		case ObdFrame.UNBIND_COMMAND_ID:
		{
			byte status = result[0];
			Log.i(TAG, "result=="+Util.printByteArray(result));
			
			switch(status){
			case 0x00:
				Log.i(TAG, "解绑成功!");
				mToast.showToast("解绑成功!");

				break;
			case 0x01:
				Log.i(TAG, "此用户和设备还未绑定过!");
				mToast.showToast("此用户和设备还未绑定过!");

				break;
			case 0x02:
				Log.i(TAG, "异常!");
				mToast.showToast("异常!");

				break;
				default:
					break;
			}
		}
			break;
			default:
			break;
		}
	}
	


	
	

}
