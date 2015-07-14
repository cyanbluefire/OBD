package com.neoway.vehiclebeta1.service;

import java.net.*;
import java.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LocalSocket;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.neoway.vehiclebeta1.callback.SocketResultCallback;
import com.neoway.vehiclebeta1.data.*;
import com.neoway.vehiclebeta1.net.SocketManager;
import com.neoway.vehiclebeta1.utils.MyApplication;
import com.neoway.vehiclebeta1.utils.ToastUtils;
import com.neoway.vehiclebeta1.utils.Util;


import android.os.Handler;
import static com.neoway.vehiclebeta1.ui.LoginActivity.login_data;
import static com.neoway.vehiclebeta1.ui.LoginActivity.username_area;

public class AppService extends Service {

	private static final String TAG = "AppService";

	// 连接到后台的
	public static Socket socket;
	//private static final String SERVER_IP = "115.29.178.98";
	private static final String SERVER_IP = "172.24.21.17";
	
	private int SERVER_PORT = 30003;

	private boolean isReconnectTimerOn = false;

	private socketConnectThread socketConnectThread = null;
	private SocketReceiveThread socketReceiveThread = null;

	// 用于与APP的本地连接。。。
	private ServerSocket lanServerSocket;
	private Socket lanClientSocket;
	private int LAN_SERVER_PORT = 31313;
	
	private SocketReceiveThread lanSocketReceiveThread = null;

	//
	private SocketReceiver socketReceiver;
	private ConnectionChangeReceiver connectionReceiver;
	private RefreshAlarmReceiver timerReceiver;

	// timer
	private AlarmManager alarmManager;
	private PendingIntent timerIntent;

	public static final String SOCKET_ACTION = "com.obdservice.ACTION";
	public static final String SOCKET_SUB_ACTION = "SUB_ACTION";

	public static final String SOCKET_SUB_ACTION_RECONNECT = "reconnect";
	public static final String SOCKET_SUB_ACTION_RECONNECT_DELAYED = "reconnectDelayed";
	public static final String SOCKET_SUB_ACTION_LAN_CONNECT_TIMEOUT = "lanConnectTimeout";
	public static final String SOCKET_SUB_ACTION_PUSH_LOCATION = "push_location";
	public static final String SOCKET_SUB_ACTION_PUSH_CAN = "push_CAN";


	private String accountSaved;
	private String tokenSaved;
	private boolean isLanClientAuthOk = false;
	private boolean isObdBindOK = false;

	// private static byte[] sendBuffer = new byte[1024 * 2];
	// private static int sendPos = 0;
	private int heartbeatUnResponseCount = 0;
	private int reconnectUnSuccessCount = 0;


	private Set<String> echoWaitingSet;

	// 1.涓庡悗鍙扮殑鏁版嵁杩炴帴绠＄悊
	// 2.寤虹珛鏈湴鐨剆ocket server 鐢ㄤ簬鏈湴socket client鐨勮繛鎺ャ�
	
	/*add by cuiyan */
	private SocketManager mSocketManager;
	private ToastUtils mToast = new ToastUtils();
	private byte[] resultBytes;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "OBD Service created");

		echoWaitingSet = new HashSet<String>();

		socketReceiver = new SocketReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SOCKET_ACTION);
		registerReceiver(socketReceiver, filter);

		connectionReceiver = new ConnectionChangeReceiver();
		IntentFilter connfilter = new IntentFilter();
		connfilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectionReceiver, connfilter);

		IntentFilter reffilter = new IntentFilter(
				RefreshAlarmReceiver.TIMER_UPDATE);
		timerReceiver = new RefreshAlarmReceiver();
		registerReceiver(timerReceiver, reffilter);
		


		socketConnectThread = new socketConnectThread();
		socketConnectThread.start();

		mSocketManager = new SocketManager(this);


	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "socket service destroy!");
		if (timerIntent != null) {
			alarmManager.cancel(timerIntent);
		}

		unregisterReceiver(socketReceiver);
		unregisterReceiver(connectionReceiver);

		super.onDestroy();

	}

	private void restartTimer() {
		if (timerIntent == null) {
			Intent intent = new Intent(RefreshAlarmReceiver.TIMER_UPDATE);
			timerIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		} else {
			alarmManager.cancel(timerIntent);
		}

		int time = 30 * 1000;// 3600000; //1 minute
		long initialRefreshTime = SystemClock.elapsedRealtime() + (30 * 1000); // 30s
																				// 开始检测
		Log.d(TAG, "restartTimer: " + time + " initialRefreshTime: "
				+ initialRefreshTime);
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				initialRefreshTime, time, timerIntent);
		
		
	}

	public class RefreshAlarmReceiver extends BroadcastReceiver {
		public static final String TIMER_UPDATE = "com.obdservice.TIMER_UPDATE";

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "timer service: push.");
			// 杩欎簺鍑芥暟骞朵笉鑳戒綔涓哄垽鏂璼ocket鐘舵�鐨勪緷鎹紝鍙湁閫氳繃鍙戦�鍒ゆ柇寮傚父澶勭悊鏉ュ仛銆�
			if (socket == null || (!socket.isConnected() || socket.isClosed())) {
				if (socket != null)
					disconnect(socket);

				reconnectDelayed(); // 不负责重连控制
			} else {
				//心跳包
				byte[] data = new byte[2];
				data[0] = 0;
				data[1] = 0;
				ObdMessage msg = new ObdMessage(data, data.length);
				sendMessage(socket, (new ObdFrame(msg)).encode());
				heartbeatUnResponseCount++;

				if (heartbeatUnResponseCount > 50) {// 50次心跳都没响应socket需要复位				/**************要不要加这个？？？？？************/
					Log.d(TAG, "timer service: heart beat timeout");
					disconnect(socket);
					socket = null;
					reconnectDelayed();
				}

			}
		}
	}

	public class ConnectionChangeReceiver extends BroadcastReceiver {

		private boolean mConnection = false;

		@Override
		public void onReceive(Context context, Intent intent) {
			//处于连接状态，但网络未连接，则断开
			if (mConnection
					&& intent.getBooleanExtra(
							ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				mConnection = false;
			} else if (!mConnection
					&& !intent.getBooleanExtra(
							ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				mConnection = true;

				// 閾炬帴涓婁簡锛岃�铏�鍙戣捣杩炴帴
				if (socket == null) {
					// reconnect(); //鏆傛椂涓嶉噰鐢ㄨ繖涓柟寮�
				}

			}
		}
	}
	
	/**
	 * socket  重 连
	 * @author 20140707369
	 *
	 */
	public class SocketReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(SOCKET_ACTION)) {
				String sub_action = intent.getStringExtra(SOCKET_SUB_ACTION);
				if (sub_action == null) {
					// nothing
					Log.d(TAG, "onReceive: SOCKET_SUB_ACTION. is NULL, return");
					return;
				}

				if (sub_action.equals(SOCKET_SUB_ACTION_RECONNECT_DELAYED)) {
					int delayTime = 5 * 1000;

					// 重新计算延时时间
					if (reconnectUnSuccessCount >= 1) {											/*****reconnectUnSuccessCount 大于10肯定大于1了？？？*/
						delayTime = 30 * 1000;

					} else if (reconnectUnSuccessCount >= 10) {
						delayTime = 60 * 1000;
					}

					if (isReconnectTimerOn) {
						Log.d(TAG,
								"socket service: reconnect time is on, ignore ");
					} else {
						isReconnectTimerOn = true;
						Log.d(TAG, "socket service: reconnect delayed: "
								+ delayTime);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								reconnect();
								isReconnectTimerOn = false;
							}
						}, delayTime);
					}

				} else if (sub_action.equals(SOCKET_SUB_ACTION_RECONNECT)) {
					Log.d(TAG, "socket service: reconnect.");

					socketConnectThread = new socketConnectThread();
					socketConnectThread.start();
					reconnectUnSuccessCount++;

				}

				else {
					// do nothing

				}

			}
		}
	}
	
	/**
	 * socket连接
	 * @author 20140707369
	 *
	 */
	private class socketConnectThread extends Thread {
		@Override
		public void run() {
			Log.d(TAG, "socket service - socketConnectThread::run");
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT),
						5000);
				Log.d(TAG, "socket service - connected.");
				reconnectUnSuccessCount = 0; //  一旦连上了,归零
			} catch (SocketTimeoutException to) {
				Log.d(TAG, "socket connect timeout, retry later");
				to.printStackTrace();

				socket = null;
				reconnectDelayed();
				return;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "socket connect error, retry later");
				e.printStackTrace();

				socket = null;
				reconnectDelayed();
				return;
			}
			//连接成功后，登录
			sendMessage(socket, login_data);
			//接收消息
			socketReceiveThread = new SocketReceiveThread(socket);
			socketReceiveThread.start();
			
			alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			restartTimer();
		}
	}

	void reconnectDelayed() {						//无延迟的重连
		// Log.d(TAG, "reconnectDelayed");
		Intent sendIntent = new Intent(SOCKET_ACTION);
		sendIntent.putExtra(SOCKET_SUB_ACTION,
				SOCKET_SUB_ACTION_RECONNECT_DELAYED);
		this.sendBroadcast(sendIntent);
	}

	void reconnect() {
		// Log.d(TAG, "reconnect");
		Intent sendIntent = new Intent(SOCKET_ACTION);
		sendIntent.putExtra(SOCKET_SUB_ACTION, SOCKET_SUB_ACTION_RECONNECT);
		sendBroadcast(sendIntent);
	}

	private class SocketReceiveThread extends Thread {
		private InputStream mInputStream = null;
		private String str = null;
		private Socket sock;
		//private byte[] readBuff;
		private boolean stop = false;

		private byte[] receiveBuffer;
		private int receivePos;

		public SocketReceiveThread(Socket s) {
			Log.i(TAG, "socket service - SocketReceiveThread Created");
			sock = s;
			//readBuff = new byte[1024];

			receiveBuffer = new byte[1024 * 2];
			receivePos = 0;
		}

		@Override
		public void run() {
			Log.i(TAG, "socket service - SocketReceiveThread::run");
			try {
				InputStream in =sock.getInputStream();
				while (!stop) {
					byte[] buffer = new byte[1024];
					int temp = 0;
					while((temp=in.read(buffer))>0){
						Log.i(TAG,"socket service - receive:"
							+ TextUtil.toHexString(buffer,temp));
						parseReceivedRawData(buffer, temp);
					}
					
					/*int readLen = in.read(readBuff);

					if (readLen > 0) {
						
						parseReceivedRawData(readBuff, readLen);
					}*/
				}
			} catch (Exception e) {
				// TODO: handle exception
				// TODO Auto-generated catch block
				e.printStackTrace();

				if ((sock != null)) {
					// 濡傛灉閫氫俊鍑洪棶棰樹簡锛岄渶瑕侀噸鏂拌繛鎺�
					disconnect(sock);
					sock = null;
				}
				reconnectDelayed();
				stop = true;
			}
				// 涓嶅箍鎾�
		}

		/**
		 * 解析接收到的数据
		 * @param buff
		 * @param buffLength
		 */
		@SuppressLint("NewApi")
		private void parseReceivedRawData(byte[] buff, int buffLength) {
			Log.i(TAG, "parseReceivedRawData:buffLength==" + buffLength);
			// byte[] temp=new byte[1024*4];
			// byte[] msg=new byte[1024];
			int start = -1, end = -1;
			int tag = 0;

			int len = buffLength;
			System.arraycopy(buff, 0, receiveBuffer, receivePos, len);
			int length = receivePos + len;
			for (int i = 0; i < length; i++) {
				if (receiveBuffer[i] == 0x7e) {
					if (tag % 2 == 0) {
						start = i;
					} else if (tag % 2 == 1) {
						end = i;
						// System.out.println(start+","+end);
						byte[] b = Arrays.copyOfRange(receiveBuffer, start,end + 1);
						Log.i(TAG, "ReceivedMessage:" + TextUtil.toHexString(b));
						HandleReceivedMessage(sock, b);

						// pos=end+1;
						start = -1;
						end = -1;
						if ((i + 1) < length && receiveBuffer[i + 1] != 0x7e) {
							start = i;
							tag++;
						}
					}
					tag++;
				}
			}
			if (start >= 0) {
				byte[] restore = Arrays.copyOfRange(receiveBuffer, start,length);
				System.arraycopy(restore, 0, receiveBuffer, 0, restore.length); // 濡傛灉涓嶈兘鏋勬垚涓�釜甯э紝鍒欎繚瀛樺湪鏁扮粍涓�
				receivePos = restore.length;
			} else {
				receivePos = 0;
				end = 0;
			}
		}
	
	}

	void disconnect(Socket sock) {
		Log.d(TAG, "disconnect socket: " + sock);
		try {
			sock.close();
		} catch (SocketTimeoutException aa) {

		} catch (IOException ioe) {

		}

	}

	void disconnect(LocalSocket sock) {
		Log.d(TAG, "disconnect local socket: " + sock);
		try {
			sock.close();
		} catch (SocketTimeoutException aa) {

		} catch (IOException ioe) {

		}

	}

	// ///////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * MyBinder 获得返回值
	 */
	public class MyBinder extends Binder{
		public byte[] getResultBytes(){
			return resultBytes;
		}
	}

	public  void sendMessageToServer(byte[] data) {

		sendMessage(socket, data);
	}

	// 如果没有发送成功，注意要保存。。。。。。。
	void sendMessage(Socket sock, byte[] data) {
		Log.d(TAG,
				"sendMessageToServer: " + " " + sock + " : "
						+ TextUtil.toHexString(data));

		if (sock == null) {

			if (sock == socket) { // 杩炴帴鍒版湇鍔″櫒鐨�
				reconnectDelayed();
			}

			// 缂撳瓨锛屾病鏈夋垚鍔熷彂閫佺殑鏁版嵁, 绛夎繛鎺ヨ繛涓婁簡锛岄┈涓婂張閲嶆柊鐧诲綍銆�
			// System.arraycopy(data, 0, sendBuffer, sendPos, data.length);
			// sendPos += data.length;
			return;
			// 锛燂紵锛燂紵锛燂紵锛燂紵锛燂紵锛燂紵锛燂紵锛燂紵
		}

		try {
			OutputStream ou = sock.getOutputStream();
			// 鍚戞湇鍔″櫒鍙戦�淇℃伅

			// if(sendPos > 0) //涔嬪墠杩樻湁鏁版嵁娌℃湁鍙戦�鍑哄幓鐨勶紝鍏堝彂
			// {
			// System.arraycopy(data, 0, sendBuffer, sendPos, data.length);
			// byte[] resendBuffer = Arrays.copyOfRange(sendBuffer,0, sendPos-1
			// );
			// sendPos -= resendBuffer.length;

			// ou.write(resendBuffer);
			// }

			ou.write(data);
			ou.flush();

		} catch (IOException e) {
			Log.d(TAG, "send Message To server failed");
			e.printStackTrace();

			if (sock == socket) {
				disconnect(sock);
				socket = null;
				reconnectDelayed();

			} else if (socket == lanClientSocket) {
				disconnect(sock);
				lanClientSocket = null;
				isLanClientAuthOk = false;
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////
	// 瑙ｆ瀽 //瑙ｆ瀽鎶ユ枃锛屼繚瀛樹负琚鐞嗙殑閮ㄥ垎

	public void HandleReceivedMessage(Socket sock, byte[] data) {
		ObdFrame frame = new ObdFrame(data);
		ObdMessage msg = frame.getMessage();

		Log.d(TAG, "HandleReceivedMessage " + msg.commandId);
		if(msg.commandId == 0){
			heartbeatUnResponseCount = 0;
			return;
		}
		if(msg.commandId == ObdFrame.LOGIN_COMMAND_ID){
			byte status = msg.commandParams[0];
			switch(status){
			case 0x00:
				mToast.showToast("登录成功！");
				Log.i(TAG, "登录成功！");			
		        LocalDataUtil.SaveSharedPre("username", username_area, "UserData");		//存的是加了 0086 的电话号码
				LocalDataUtil.SaveSharedPre("LoginStatus", "0", "UserData");
				break;
			case 0x01:
				mToast.showToast("登录成功！已有绑定过设备!");
				byte[] MessageBody = new byte[data.length-1];
				
				byte[] ura = new byte[50];
				byte[] ssid = new byte[15];
				byte[] wifi_psw = new byte[10];
				byte[] token = new byte[20];
			
		        System.arraycopy(MessageBody, 0, ura, 0, 50 );
		        System.arraycopy(MessageBody, 50, ssid, 0, 15 );
		        System.arraycopy(MessageBody, 65, wifi_psw, 0, 10 );
		        System.arraycopy(MessageBody, 75, token, 0, 20 );
		        Log.i(TAG, "ura=="+(new String(ura))+" ssid=="+(new String(ssid))+" wifi_psw=="+(new String(wifi_psw))+" token=="+(new String(token)));
		        
		        LocalDataUtil.SaveSharedPre("username", username_area, "UserData");		//存的是加了 0086 的电话号码
				LocalDataUtil.SaveSharedPre("LoginStatus", "0", "UserData");
		        break;
			case 0x02:
				mToast.showToast("用户不存在或密码错误");
				Log.i(TAG, "用户不存在或密码错误！");
				
				alarmManager.cancel(timerIntent);	//取消计时器
				break;
			case 0x03:
				mToast.showToast("登录客户端个数超出最大限制");
				Log.i(TAG, "登录客户端个数超出最大限制");
				
				alarmManager.cancel(timerIntent);

				break;
			case 0x04:
				mToast.showToast("异常!");
				Log.i(TAG, "异常!");
				
				alarmManager.cancel(timerIntent);

				break;
				default:
					break;
			}
		}		
		
	SocketManager.handleServerResult(msg.commandId,msg.commandParams);			
	resultBytes = msg.commandParams;	
	
	}
	


	void lanConnectTimeout() {
		Intent sendIntent = new Intent(SOCKET_ACTION);
		sendIntent.putExtra(SOCKET_SUB_ACTION,
				SOCKET_SUB_ACTION_LAN_CONNECT_TIMEOUT);
		sendBroadcast(sendIntent);

	}

	void pushLocation() {
		Intent sendIntent = new Intent(SOCKET_ACTION);
		sendIntent.putExtra(SOCKET_SUB_ACTION, SOCKET_SUB_ACTION_PUSH_LOCATION);
		sendBroadcast(sendIntent);
	}

	void pushCAN() {
		Intent sendIntent = new Intent(SOCKET_ACTION);
		sendIntent.putExtra(SOCKET_SUB_ACTION, SOCKET_SUB_ACTION_PUSH_CAN);
		sendBroadcast(sendIntent);
	}





}
