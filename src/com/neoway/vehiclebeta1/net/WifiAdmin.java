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
	//����һ��WifiManager����
	private WifiManager mWifiManager;
	//����һ��WifiInfo����
	private WifiInfo mWifiInfo;
	//ɨ��������������б�
	private List<ScanResult> mWifiList;
	private ScanResult mScanResult;
	//���������б�
	private List<WifiConfiguration> mWifiConfigurations;
	private WifiLock mWifiLock;
	
	public WifiAdmin(Context context){
		//ȡ��WifiManager����
		mWifiManager=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		//ȡ��WifiInfo����
		mWifiInfo=mWifiManager.getConnectionInfo();
	}
	//��wifi
	public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
            Log.v(TAG,"openWifi");
        }
        return bRet;
    }
	//�ر�wifi
	public void closeWifi(){
		if(mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
		}
	}
	 // ��鵱ǰwifi״̬  
    public int checkState() {  
        return mWifiManager.getWifiState();  
    }  
	//����wifiLock
	public void acquireWifiLock(){
		mWifiLock.acquire();
	}
	//����wifiLock
	public void releaseWifiLock(){
		//�ж��Ƿ�����
		if(mWifiLock.isHeld()){
			mWifiLock.acquire();
		}
	}
	//����һ��wifiLock
	public void createWifiLock(){
		mWifiLock=mWifiManager.createWifiLock("test");
	}
	//�õ����úõ�����
	public List<WifiConfiguration> getConfiguration(){
		return mWifiConfigurations;
	}
	//ָ�����úõ������������
	public void connetionConfiguration(int index){
		if(index>mWifiConfigurations.size()){
			return ;
		}
		//�������ú�ָ��ID������
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
	}
	
	public void startScan(){
		//Log.v(TAG,"startScan()");
		mWifiManager.startScan();
		//�õ�ɨ����
		mWifiList=mWifiManager.getScanResults();
		//�õ����úõ���������                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		mWifiConfigurations=mWifiManager.getConfiguredNetworks();
	}
	//�õ������б�
	public List<ScanResult> getWifiList(){
		//Log.v(TAG,"getWifiList");
		return mWifiList;
	}
	
	/*
	 * ����ָ��WIFI(�Ѿ�������)
	 */
	public void connectSpecifiedWIFI(String SSID,String PassWord,int Type){
			WifiConfiguration tempConfig = this.IsExistSSID(SSID);
			Log.v(TAG, "IsexistSSID tempConfig "+tempConfig);
			mWifiManager.enableNetwork(tempConfig.networkId, true);			   
	}
	public WifiConfiguration IsExistSSID(String SSID){
		List<WifiConfiguration> list_existConfig = mWifiManager.getConfiguredNetworks();	//*****************���������ɨ�����ͬǰ׺�Ķ��WIFI�������
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
	        	 * ����wifi,���ӳ�2��
	        	 */
	     		Log.i(TAG, "WIFIState=="+mWifiManager.getWifiState());
	     		if(mWifiManager.getWifiState() != mWifiManager.WIFI_STATE_ENABLED){
	     			Toast.makeText(MyApplication.getContext(), "Wifi���ڴ�", Toast.LENGTH_LONG).show();
	     			openWifi();
	     			
		     		try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	     		}
	     		/*
	     		 * ÿ0.1����һ��wifi�Ƿ���
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
	     		 * ��ָ��wifi
	     		 */
     			//Log.i(TAG, "wifi list=="+getWifiList().toString());
	         	boolean GetWifi = false;
	         	int timeout = 5*1000;			//ɨ�賬ʱ
	             while(!GetWifi && timeout > 0){
	            	 Log.i(TAG, "Do not get the WIFi::"+SSID);
	            	 startScan();
	     			mWifiList=getWifiList();
	     			/*
	     			 * ɨ��
	     			 */
	     	    	if(mWifiList!=null){
	     	    		String ScanSSID="";
	     	    		int i;
	     	    		for(i=0;i<mWifiList.size();i++){
	     	    			mScanResult=mWifiList.get(i);
	     	    			ScanSSID = mScanResult.SSID;
	     	    			/*
	     	    			 * ������ָ��wifi
	     	    			 */
	     	    			if(ScanSSID.trim().equals(SSID)){
	     	    				GetWifi = true;
	     	    				Toast.makeText(MyApplication.getContext(), "��������OBDWifi...", Toast.LENGTH_LONG).show();
	     	    				Log.i(TAG, "get specified SSID "+ScanSSID);
//	     	    				try {
//	     							Thread.sleep(5000);
//	     						} catch (InterruptedException e) {
//	     							// TODO Auto-generated catch block
//	     							e.printStackTrace();
//	     						}
	     	    				/*
	     	    				 * ����OBDWifi
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
     	    						 * ��OBD��֤
     	    						 */
		     	   		     		if(mWifiManager.getWifiState()==3){					//ȷ���ѵ�֮���������ϵ�
		     	   		     			HttpManager httpManager = new HttpManager();
		     	   		     			String OBDresult=httpManager.ipcConnect(token);
		     	    					//Toast.makeText(MyApplication.getContext(), "OBD��֤���"+OBDresult, Toast.LENGTH_LONG).show();
     	    							return;	
		     	   		     		}
	     	    						
	     	    			}
	     	    		}
	     	    	}
	     	    	/*
	     	    	 * δɨ�赽���ӳٺ���ɨ��
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
					Toast.makeText(MyApplication.getContext(), "δ�ҵ�ָ��Wifi", 2);
					Log.i(TAG, "δ�ҵ�ָ��Wifi");
				}
	         } 
	     }; 
		     handler.postDelayed(runnable, 0);// 0���ִ��runnable

	}

	//�鿴ɨ����
	public StringBuffer lookUpScan(){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<mWifiList.size();i++){
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			 // ��ScanResult��Ϣת����һ���ַ�����  
            // ���аѰ�����BSSID��SSID��capabilities��frequency��level  
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
	//�õ����ӵ�ID
	public int getNetWordId(){
		return (mWifiInfo==null)?0:mWifiInfo.getNetworkId();
	}
	//�õ�wifiInfo��������Ϣ
	public String getWifiInfo(){
		return (mWifiInfo==null)?"NULL":mWifiInfo.toString();
	}
	//���һ�����粢����
	public void addNetWork(WifiConfiguration configuration){
		int wcgId=mWifiManager.addNetwork(configuration);
		mWifiManager.enableNetwork(wcgId, true);
	}
	//�Ͽ�ָ��ID������
	public void disConnectionWifi(int netId){
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
}
