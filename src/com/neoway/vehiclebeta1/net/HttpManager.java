package com.neoway.vehiclebeta1.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static com.neoway.vehiclebeta1.data.GlobalVariable.ipcState;

import com.neoway.vehiclebeta1.constant.*;
import com.neoway.vehiclebeta1.data.LocalDataUtil;
import com.neoway.vehiclebeta1.data.ObdFrame;
import com.neoway.vehiclebeta1.data.TextUtil;
import com.neoway.vehiclebeta1.ipc.AppIpcMessage;
import com.neoway.vehiclebeta1.ui.MeActivity;
import com.neoway.vehiclebeta1.utils.MyApplication;
import com.neoway.vehiclebeta1.utils.Util;
import com.obdphoneipc.ObdPhoneIpcMessager;
import com.obdphoneipc.data.IpcMessage;


public class HttpManager {
	private static HttpManager mNetWorker;
	private List<NameValuePair> dataPairs = new ArrayList<NameValuePair>();
	private DefaultHttpClient mHttpClient;

	//private boolean ipcState;
	
	public static ObdPhoneIpcMessager ipcMessager;
	IpcReceiver ipcReceiver;
	public HttpManager() {
		
		
		ipcReceiver = new IpcReceiver();
		ipcMessager = new ObdPhoneIpcMessager(LocalDataUtil.ReadSharePre("UserData", "username"), "token");
	}

	private void init() {
		dataPairs.clear();
		DefaultHttpClient client = new DefaultHttpClient();
		ClientConnectionManager manager = client.getConnectionManager();
		HttpParams params = client.getParams();
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 5*1000);					//post读取超时
		mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
				params, manager.getSchemeRegistry()), params);
		// ((AbstractHttpClient) mHttpClient).getCookieStore().clear();
	}

	public static HttpManager getInstance() {
		if (mNetWorker == null) {
			mNetWorker = new HttpManager();
		}
		return mNetWorker;
	}

	public String register(String user, String pass) {
		init();
		try {
			dataPairs.add(new BasicNameValuePair("method", "register"));
			dataPairs.add(new BasicNameValuePair("username", user));
			dataPairs.add(new BasicNameValuePair("password", pass));
			HttpPost post = new HttpPost(Constant.REG_URL);
			post.setEntity(new UrlEncodedFormEntity(dataPairs, "utf8"));
			HttpResponse response = mHttpClient.execute(post);
			if(response.getStatusLine().getStatusCode() == 200){
				return getResultByStream(response.getEntity().getContent());
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getResultByStream(InputStream in) {
		String result = "";
		if (in != null) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					result = result + line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public String login(String user, String pass, String imei) {
		init();
		((AbstractHttpClient) mHttpClient).getCookieStore().clear();
		try {
			dataPairs.add(new BasicNameValuePair("username", user));
			dataPairs.add(new BasicNameValuePair("password", pass));
			dataPairs.add(new BasicNameValuePair("imei", imei));
			HttpPost post = new HttpPost(Constant.LOGIN_URL);
			post.setEntity(new UrlEncodedFormEntity(dataPairs, "utf8"));
			HttpResponse response = mHttpClient.execute(post);
			getSessionId();
			return getResultByStream(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	 * login调用,获取服务器分配的ssionid
	 */
	private void getSessionId(){
		CookieStore cookieStore = mHttpClient.getCookieStore();
		List<Cookie> cookies = cookieStore.getCookies();
		for(int i=0;i<cookies.size();i++){
			if("JSESSIONID".equals(cookies.get(i).getName())){
				String JSESSIONID = cookies.get(i).getValue();
				LocalDataUtil.SaveSharedPre("JSESSIONID", JSESSIONID, "UserData");
				break;
			}
		}
	}

	public String bind(String username,String ura,String phoneImei){
		init();
		Log.i("HttpManager bind", "ura=="+ura);
		try {
			dataPairs.add(new BasicNameValuePair("username", username));
			dataPairs.add(new BasicNameValuePair("ura", ura));
			dataPairs.add(new BasicNameValuePair("imei", phoneImei));
			HttpPost post = new HttpPost(Constant.BIND_URL);
			
			Log.i("HttpManager bind", "dataPairs"+dataPairs);
			post.setEntity(new UrlEncodedFormEntity(dataPairs, "utf-8"));
			/*
			 * 需要验证是否登录的时候调用（绑定、解绑）
			 * 向服务器发送保存的ssionid
			 */
			String JSESSIONID = LocalDataUtil.ReadSharePre("UserData", "JSESSIONID");
			if(null != (JSESSIONID) ){
				post.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
			}
			HttpResponse response = mHttpClient.execute(post);
			return getResultByStream(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public  String unBind(String username,String ura,String phoneImei){
		init();
		Log.i("HttpManager bind", "ura=="+ura);
		try {
			dataPairs.add(new BasicNameValuePair("username", username));
			dataPairs.add(new BasicNameValuePair("ura", ura));
			dataPairs.add(new BasicNameValuePair("imei", phoneImei));
			HttpPost post = new HttpPost(Constant.UNBIND_URL);
			Log.i("HttpManager bind", "dataPairs"+dataPairs);
			post.setEntity(new UrlEncodedFormEntity(dataPairs, "utf-8"));
			/*
			 * 需要验证是否登录的时候调用（绑定、解绑）
			 * 向服务器发送保存的ssionid
			 */
			String JSESSIONID = LocalDataUtil.ReadSharePre("UserData", "JSESSIONID");
			if(null != (JSESSIONID) ){
				post.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
			}
			HttpResponse response = mHttpClient.execute(post);
			return getResultByStream(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	
	}

	public String exit(String username,String phoneImei){
		init();
		try {
			dataPairs.add(new BasicNameValuePair("username", username));
			dataPairs.add(new BasicNameValuePair("imei", phoneImei));
			HttpPost post = new HttpPost(Constant.Exit_URL);
			post.setEntity(new UrlEncodedFormEntity(dataPairs, "utf8"));
			/*
			 * 需要验证是否登录的时候调用（绑定、解绑、注销）
			 * 向服务器发送保存的ssionid
			 */
			String JSESSIONID = LocalDataUtil.ReadSharePre("UserData", "JSESSIONID");
			if(null != (JSESSIONID) ){
				post.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
			}
			HttpResponse response = mHttpClient.execute(post);
			return getResultByStream(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String cancelNavi(String username,String phoneImei){
		init();
		try {
			dataPairs.add(new BasicNameValuePair("username", username));
			dataPairs.add(new BasicNameValuePair("imei", phoneImei));
			HttpPost post = new HttpPost(Constant.CANCEL_NAVI);
			post.setEntity(new UrlEncodedFormEntity(dataPairs, "utf8"));
			/*
			 * 需要验证是否登录的时候调用（绑定、解绑、注销）
			 * 向服务器发送保存的ssionid
			 */
			String JSESSIONID = LocalDataUtil.ReadSharePre("UserData", "JSESSIONID");
			if(null != (JSESSIONID) ){
				post.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
			}
			HttpResponse response = mHttpClient.execute(post);
			return getResultByStream(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String startNavi(String username,String phoneImei,String dest,String latitude,String longitude,String latLngtype){				
		init();
		try {
			dataPairs.add(new BasicNameValuePair("username", username));
			dataPairs.add(new BasicNameValuePair("imei", phoneImei));
			dataPairs.add(new BasicNameValuePair("dest", dest));	
			dataPairs.add(new BasicNameValuePair("latitude", latitude));	
			dataPairs.add(new BasicNameValuePair("longitude", longitude));	
			dataPairs.add(new BasicNameValuePair("type", latLngtype));
			
			//Log.i("startNavi", "dest==="+URLDecoder.decode(dest, "utf-8"));
			Log.i("startNavi", "dataPairs==="+dataPairs.toString());
			
			HttpPost post = new HttpPost(Constant.START_NAVI);
			post.setEntity(new UrlEncodedFormEntity(dataPairs, "utf8"));				
			
			/*
			 * 需要验证是否登录的时候调用（绑定、解绑、注销）
			 * 向服务器发送保存的ssionid
			 */
			String JSESSIONID = LocalDataUtil.ReadSharePre("UserData", "JSESSIONID");
			if(null != (JSESSIONID) ){
				post.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
			}
			HttpResponse response = mHttpClient.execute(post);			
			return getResultByStream(response.getEntity().getContent());

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	class IpcReceiver implements ObdPhoneIpcMessager.IpcMessageHandler{

		@Override
		public void handleMessage(String srcAppId,
				String destAppId, byte[] ipcMessage) {
			// TODO Auto-generated method stub
			Log.i("identifyDevice", "Receive Msg from "+srcAppId);
			Log.i("identifyDevice", "ipcMessage=="+TextUtil.toHexString(ipcMessage));
			
			AppIpcMessage appIpcMsg = new AppIpcMessage(ipcMessage);
			short cmd = appIpcMsg.getCmd();
			String data = appIpcMsg.getDataStr();
			Log.i("identifyDevice", "ReceiveMsg cmd=="+cmd);
			Log.i("identifyDevice", "ReceiveMsg data=="+data.toString());
		}
		
	}


	
	public  String ipcConnect(final String token) {
		new Thread() {
			String msg = "";
			boolean ret = false;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();					
                   int timeout = 20;
                   
                   
					while(timeout > 0){
						ret = ipcMessager.isIpcReady();
						Log.i("identifyDevice","ret = " + ret);
						
						
						if(ret == false){
							Log.i("identifyDevice","isIpcReady not ready, retry");
							//return;							
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							timeout--;							
						}else{
							break;
						}
						
						
					}
					
					
					if(ret == false){
						Log.i("identifyDevice","isIpcReady not ready, return");
						/**
						 * 异常!!
						 * 解除绑定
						 */
						String username = LocalDataUtil.ReadSharePre("UserData", "username");
						String uraStr = LocalDataUtil.ReadSharePre("UserData","uraStr");
						String imei = LocalDataUtil.ReadSharePre("UserData","imei");
						
//						new UnBindTask().execute(username,uraStr,imei);	
						/*
						 * 解绑 by socket
						 */
						byte[] commandParams = new byte[65];
				        System.arraycopy(username.getBytes(), 0, commandParams, 0,username.length());
				        System.arraycopy(uraStr.getBytes(), 0, commandParams, 15,uraStr.length());
				        
				        byte[] unBind_frame = new ObdFrame().encode(TextUtil.toMessageBodyBytes(commandParams, ObdFrame.UNBIND_COMMAND_ID));
						new SocketManager().dispatchMsg(unBind_frame);
						return;
					}
					
					if(ipcMessager.registerApp(Constant.APPID)<0 ){
					Log.i("identifyDevice", "registerApp failed");
						return;
					}					
					ipcMessager.registerReceivedMessageHandler(ipcReceiver);
//					GlobalVariable globalVariable = new GlobalVariable();
//					globalVariable.setIPCReady(true);
					ipcState = true;
					//Log.i("identifyDevice",""+globalVariable.getIsIPCReady());
					
//					//发消息
					//ipcMessager.sendMessage("10010", "IPC Message from PhoneApp!!!".getBytes());
			}
		}.start();
		
		
		return null;
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
			Log.i("UnBindTask", "解除绑定返回值=="+result);
			// TODO Auto-generated method stub
			if(result==null){
				Log.e("UnBindTask", "解除绑定返回值为NULL");
				Toast.makeText(MyApplication.getContext(), "网络连接异常", Toast.LENGTH_SHORT).show();
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
					Toast.makeText(MyApplication.getContext(), "解除绑定", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_LONG).show();
					break;
				case 2:
					Toast.makeText(MyApplication.getContext(), "错误: "+msg, Toast.LENGTH_LONG).show();
					break;
				case 3:
					Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_LONG).show();
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
	
	
	public void sendMsg2OBD(String destID,short cmd,String data){
		try {
			Log.i("sendMsg2OBD", "data=="+data);
			AppIpcMessage ipcSendMsg = new AppIpcMessage(cmd,data.getBytes("UTF-8"));
			if(ipcMessager != null){
				ipcMessager.sendMessage(destID,ipcSendMsg.encode());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void sendNaviMsg2OBD(String destID,short cmd,byte type,int longitude,int latitude,String dest_name){
		Log.i("sendNaviMsg2OBD","destID=="+destID+" cmd=="+cmd+" type=="+type+" longitude=="+longitude+" latitude=="+latitude+" dest_name=="+dest_name);
		AppIpcMessage ipcSendMsg = new AppIpcMessage(cmd, type, longitude, latitude, dest_name);
		
		if(ipcMessager != null){
			ipcMessager.sendMessage(destID,ipcSendMsg.encodeNavi());
		}

	}
	
	public ObdPhoneIpcMessager getIpcMessager(){
		return this.ipcMessager;
	}
	
}
