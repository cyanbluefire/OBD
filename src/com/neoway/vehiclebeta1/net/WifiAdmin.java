package com.neoway.vehiclebeta1.net;
import java.net.UnknownHostException;
import java.util.List;

import com.neoway.vehiclebeta1.data.GlobalVariable;
import com.neoway.vehiclebeta1.net.WifiAutoConnectManager;
import com.neoway.vehiclebeta1.net.WifiAutoConnectManager.WifiCipherType;
import com.neoway.vehiclebeta1.ui.MeActivity;
import com.neoway.vehiclebeta1.utils.MyApplication;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class WifiAdmin {
	private static final String TAG = "WifiAdmin";
	//定义一个WifiManager对象
	private WifiManager mWifiManager;
	//定义一个WifiInfo对象
	private WifiInfo mWifiInfo;
	//扫描出的网络连接列表
	private List<ScanResult> mWifiList;
	private ScanResult mScanResult;
	//网络连接列表
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mWifiLock;
	
	public WifiAdmin(Context context){
		//取得WifiManager对象
		mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		//取得WifiInfo对象
		mWifiInfo=mWifiManager.getConnectionInfo();
	}
	//打开wifi
	public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
            Log.v(TAG,"openWifi");
        }
        return bRet;
    }
	//关闭wifi
	public void closeWifi(){
		if(mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
		}
	}
	 // 检查当前wifi状态  
    public int checkState() {  
        return mWifiManager.getWifiState();  
    }  
	//锁定wifiLock
	public void acquireWifiLock(){
		mWifiLock.acquire();
	}
	//解锁wifiLock
	public void releaseWifiLock(){
		//判断是否锁定
		if(mWifiLock.isHeld()){
			mWifiLock.acquire();
		}
	}
	//创建一个wifiLock
	public void createWifiLock(){
		mWifiLock=mWifiManager.createWifiLock("test");
	}
	//得到配置好的网络
	public List<WifiConfiguration> getConfiguration(){
		return mWifiConfigurations;
	}
	//指定配置好的网络进行连接
	public void connetionConfiguration(int index){
		if(index>mWifiConfigurations.size()){
			return ;
		}
		//连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
	}
	
	public void startScan(){
		//Log.v(TAG,"startScan()");
		mWifiManager.startScan();
		//得到扫描结果
		mWifiList=mWifiManager.getScanResults();
		//得到配置好的网络连接                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		mWifiConfigurations=mWifiManager.getConfiguredNetworks();
	}
	//得到网络列表
	public List<ScanResult> getWifiList(){
		//Log.v(TAG,"getWifiList");
		return mWifiList;
	}
	
	/*
	 * 连接指定WIFI(已经连过的)
	 */
	public void connectSpecifiedWIFI(String SSID,String PassWord,int Type){
			WifiConfiguration tempConfig = this.IsExistSSID(SSID);
			Log.v(TAG, "IsexistSSID tempConfig "+tempConfig);
			mWifiManager.enableNetwork(tempConfig.networkId, true);			   
	}
	public WifiConfiguration IsExistSSID(String SSID){
		List<WifiConfiguration> list_existConfig = mWifiManager.getConfiguredNetworks();	//*****************后续需添加扫描出相同前缀的多个WIFI情况处理
		WifiConfiguration existWificonfig = null;
		for(WifiConfiguration wifiConfig: list_existConfig ){
			Log.i(TAG, "SSID "+wifiConfig.SSID);
			if(wifiConfig.SSID.equals("\""+SSID+"\"")){
				existWificonfig = wifiConfig;
			}
		}
		return existWificonfig;
	}
	
	
	public void connectWIFI(final String SSID,final String password,final WifiCipherType type,final String token){
		Log.i(TAG,"start connect wifi!");
		final Handler handler = new Handler();
	     Runnable runnable = new Runnable(){
	         @Override
	         public void run() {
	        	/*
	        	 * 开启wifi,并延迟2秒
	        	 */
	     		Log.i(TAG, "WIFIState=="+mWifiManager.getWifiState());
	     		if(mWifiManager.getWifiState() != mWifiManager.WIFI_STATE_ENABLED){
	     			Toast.makeText(MyApplication.getContext(), "Wifi正在打开", Toast.LENGTH_LONG).show();
	     			openWifi();
	     			
		     		try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	     		}
	     		/*
	     		 * 每0.1秒检测一次wifi是否开启
	     		 */
	             while(mWifiManager.getWifiState() != mWifiManager.WIFI_STATE_ENABLED){
			     		try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	 Log.i(TAG, "waiting for opening WIFI ");
	             }
	     		Log.i(TAG, "WIFIState2=="+mWifiManager.getWifiState());
	     		
	     		/*
	     		 * 绑定指定wifi
	     		 */
     			//Log.i(TAG, "wifi list=="+getWifiList().toString());
	         	boolean GetWifi = false;
	         	int timeout = 5*1000;			//扫描超时
	             while(!GetWifi && timeout > 0){
	            	 Log.i(TAG, "Do not get the WIFi::"+SSID);
	            	 startScan();
	     			mWifiList=getWifiList();
	     			/*
	     			 * 扫描
	     			 */
	     	    	if(mWifiList!=null){
	     	    		String ScanSSID="";
	     	    		int i;
	     	    		for(i=0;i<mWifiList.size();i++){
	     	    			mScanResult=mWifiList.get(i);
	     	    			ScanSSID = mScanResult.SSID;
	     	    			/*
	     	    			 * 搜索到指定wifi
	     	    			 */
	     	    			if(ScanSSID.trim().equals(SSID)){
	     	    				GetWifi = true;
	     	    				Toast.makeText(MyApplication.getContext(), "正在连接OBDWifi...", Toast.LENGTH_LONG).show();
	     	    				Log.i(TAG, "get specified SSID "+ScanSSID);
//	     	    				try {
//	     							Thread.sleep(5000);
//	     						} catch (InterruptedException e) {
//	     							// TODO Auto-generated catch block
//	     							e.printStackTrace();
//	     						}
	     	    				/*
	     	    				 * 连接OBDWifi
	     	    				 */
	     	    			     WifiAutoConnectManager wifiAutoConnect = new WifiAutoConnectManager(mWifiManager);
	     	    					wifiAutoConnect.connect(SSID, password, type);
		     	   		     		try {
		     							Thread.sleep(5000);
		     						} catch (InterruptedException e) {
		     							// TODO Auto-generated catch block
		     							e.printStackTrace();
		     						}
		     	   		     		
     	    						/*
     	    						 * 与OBD认证
     	    						 */
		     	   		     		if(mWifiManager.getWifiState()==3){					//确定搜到之后并且是连上的
		     	   		     			HttpManager httpManager = new HttpManager();
		     	   		     			String OBDresult=httpManager.ipcConnect(token);
		     	    					//Toast.makeText(MyApplication.getContext(), "OBD认证结果"+OBDresult, Toast.LENGTH_LONG).show();
     	    							return;	
		     	   		     		}
	     	    						
	     	    			}
	     	    		}
	     	    	}
	     	    	/*
	     	    	 * 未扫描到，延迟后再扫描
	     	    	 */
	     	    	try {
							Thread.sleep(1000);
							timeout = timeout-1000;
							Log.i(TAG, "timeout=="+timeout);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	             }
				if(timeout < 0){
					Toast.makeText(MyApplication.getContext(), "未找到指定Wifi", 2);
					Log.i(TAG, "未找到指定Wifi");
				}
	         } 
	     }; 
		     handler.postDelayed(runnable, 0);// 0秒后执行runnable

	}

	//查看扫描结果
	public StringBuffer lookUpScan(){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<mWifiList.size();i++){
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			 // 将ScanResult信息转换成一个字符串包  
            // 其中把包括：BSSID、SSID、capabilities、frequency、level  
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;	
	}
	public String getMacAddress(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.getMacAddress();
	}
	public String getBSSID(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.getBSSID();
	}
	public int getIpAddress(){
		return (mWifiInfo==null)?0:mWifiInfo.getIpAddress();
	}
	//得到连接的ID
	public int getNetWordId(){
		return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
	}
	//得到wifiInfo的所有信息
	public String getWifiInfo(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.toString();
	}
	//添加一个网络并连接
	public void addNetWork(WifiConfiguration configuration){
		int wcgId=mWifiManager.addNetwork(configuration);
		mWifiManager.enableNetwork(wcgId, true);
	}
	//断开指定ID的网络
	public void disConnectionWifi(int netId){
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
}
