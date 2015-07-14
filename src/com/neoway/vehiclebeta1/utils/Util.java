package com.neoway.vehiclebeta1.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.neoway.vehiclebeta1.R;
import com.neoway.vehiclebeta1.ipc.AppIpcMessage;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Util {
	
	public static String printByteArray(byte[] data){
		if(data==null || data.length<1){
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		for(byte b: data){
			sb.append(",0x"+Integer.toHexString(b));
		}
		return sb.toString();
	}
	
	public static boolean haveNetConnect(Context ctx) {
		ConnectivityManager connectivityManager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return info != null && info.isAvailable();
	}
	
	public static void showToast(Context ctx, int string) {
		Toast.makeText(ctx, string, Toast.LENGTH_SHORT).show();
	}
	
	// 文字是否为空
	public static boolean isEmpty(String content) {
		if (content == null || "".equals(content.trim())) {
			return true;
		}
		return false;
	}
	// 判断用户名是不是电话号码
	public static boolean isPhoneNumber(String username) {
		// 13,15,18开头,粗略判断
		if (username == null) {
			username = "";
		}
		String phoneNumberFormat = "^[1][358]\\d{9}$";
		Pattern pattern = Pattern.compile(phoneNumberFormat);
		Matcher matcher = pattern.matcher(username);
		if (matcher.matches()) {
			return true;
		}
		return false;

	}		
	// 判断密码格式是否正确
		public static boolean isPasswordFormated(String password) {
			if (password == null) {
				password = "";
			}
			String passwordFormat = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z!@#$%^&*]{8,16}$";
			Pattern p = Pattern.compile(passwordFormat);
			Matcher m = p.matcher(password);
			if (m.matches()) {
				return true;
			}
			return false;
		}
	//获得IMEI号
		public static String getIMEI(Context context){
			TelephonyManager telephonyManager= (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			return imei;
			
		}
		

		
}
