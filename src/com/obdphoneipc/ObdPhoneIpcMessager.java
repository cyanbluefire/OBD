package com.obdphoneipc;

import android.content.IntentFilter;
import android.net.LocalSocket;
import android.util.Log;

import com.obdphoneipc.data.*;
import com.obdphoneipc.util.TextUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.*;

/**
 * Created by Administrator on 2014/12/17.
 */
public class ObdPhoneIpcMessager {

    private static final String TAG = "ObdPhoneIpcMessager";
    //鐢ㄤ簬鏈湴app鍜宱bdservice閫氫俊锛屼富瑕侊紝鍙湁socket鎵嶆槸姣旇緝濂界殑瀹炵幇鑷繁鎯宠鐨勭簿鍑嗕紶閫�
    //<REG>  //<IPC>  //<ECH>
    public interface IpcMessageHandler{

        public void handleMessage(String srcAppId, String destAppId, byte[] ipcMessage);
    }


    //姝よ繛鎺ョ粰瀹㈡埛绔娇鐢ㄣ�銆傘�
    private final String ipcServerIp = "192.168.43.1"; //OBD WIFI IP
    private final int ipcServerPort =  31313; //OBD WIFI PORT
    private Socket ipcClientSocket;
    private boolean isIpcClientSocketConnected = false;
    
    private socketConnectThread socketConnectThread = null;
    private SocketReceiveThread socketReceiveThread = null;
    private int reconnectUnSuccessCount = 0;
    private boolean isReconnectTimerOn = false;
    

    private Set<String>  echoWaitingSet;

    private  String myAppId;
    IpcMessageHandler messageHandler;

    private boolean isObdAuthOk = false;
    private boolean isRegisterOK = false;
    private int obdAuthRetryTime = 0;
    private String obdAuthAccount;
    private String obdAuthToken;



    public ObdPhoneIpcMessager(String account, String token){
        myAppId = null;
        echoWaitingSet = new HashSet<String>();

        obdAuthAccount = account;
        obdAuthToken = token;
        
        //start connect OBD 
        socketConnectThread = new socketConnectThread();
        socketConnectThread.start();
    }


    private void reconnectDelayed(){
        int delayTime = 5 * 1000;

        if (reconnectUnSuccessCount >= 1) {
            delayTime = 10 * 1000;

        } else if (reconnectUnSuccessCount >= 10) {
            delayTime = 30 * 1000;
        }

        if(isReconnectTimerOn){
            Log.d(TAG, "socket service: reconnect time is on, ignore ");
        }else {
		    isReconnectTimerOn = true;
		    Log.d(TAG, "socket service: reconnect delayed: " + delayTime);
			new java.util.Timer().schedule(new TimerTask(){
		       public void run() {
		    	   
		    	 //actually do this
		        socketConnectThread = new socketConnectThread();
		        socketConnectThread.start();
		        reconnectUnSuccessCount++;
		     
		        isReconnectTimerOn = false;
		     
		  }}, delayTime);
       }
       
    }  

	private int requestObdAuth(){
        int result = -1;

        
        ObdAuthCommand obdauth = new ObdAuthCommand(obdAuthAccount, obdAuthToken);

        if((ipcClientSocket != null) && isIpcClientSocketConnected){

            sendMessageToObd((new ObdFrame(obdauth)).encode());
            obdAuthRetryTime++;

            //wait here some time
            int timeout = 5 * 1000;
            while (timeout > 0) {
                if (!isObdAuthOk) { //auth
                    try {
                        Thread.sleep(1000);
                        timeout -= 1000;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    result = 0;
                    break;
                }
            }

        }else{
            Log.d(TAG, "ipcClientSocket is not connected, return false");
        }

        return result;
    }


    private class socketConnectThread extends Thread
    {
        @Override
        public void run()
        {
            Log.d(TAG, "socket service - socketConnectThread::run");
            try {
            	isIpcClientSocketConnected = false;
                ipcClientSocket = new Socket();
                ipcClientSocket.connect(new InetSocketAddress(ipcServerIp, ipcServerPort), 5000);
                isIpcClientSocketConnected = true;
                reconnectUnSuccessCount = 0;
            } catch (SocketTimeoutException to) {
                Log.d(TAG, "socket connect timeout, retry later");
                to.printStackTrace();

                ipcClientSocket = null;
                reconnectDelayed();
                return;

            }catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "socket connect error, retry later");
                e.printStackTrace();

                ipcClientSocket = null;
                reconnectDelayed();
                return;
            }

            socketReceiveThread = new SocketReceiveThread(ipcClientSocket);
            socketReceiveThread.start();
            requestObdAuth();
        }
    }



    private class SocketReceiveThread extends Thread
    {
        //private InputStream mInputStream = null;
        private String str = null;
        //private Socket sock;
        private byte[] readBuff;
        private boolean stop = false;

        private byte[] receiveBuffer;
        private int receivePos;


        public SocketReceiveThread(Socket s)
        {
            Log.d(TAG, "socket service - SocketReceiveThread");
            //sock = s;
            readBuff = new byte[1024];

            receiveBuffer = new byte[1024 * 2];
            receivePos = 0;
        }

        @Override
        public void run()
        {
            Log.d(TAG, "socket service - SocketReceiveThread::run");
            while( !stop )
            {
                try {
                    int readLen = ipcClientSocket.getInputStream().read(readBuff);

                    if(readLen > 0) {
                        Log.d(TAG, "socket service - receive:" + TextUtil.toHexString(readBuff, readLen));
                        parseReceivedRawData(readBuff,readLen);
                    }

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();

                    if((ipcClientSocket!= null)) {
                        disconnect(ipcClientSocket);
                    	ipcClientSocket = null;
                    }
                    reconnectDelayed();
                    stop = true;
                    break;

                }

            }
        }

        private  void parseReceivedRawData(byte[] buff, int buffLength){
            Log.d(TAG, "parseReceivedRawData:" + buffLength);
            //byte[] temp=new byte[1024*4];
            //byte[] msg=new byte[1024];
            int start=-1,end=-1;
            int tag=0;

            int len = buffLength;
            System.arraycopy(buff, 0, receiveBuffer, receivePos, len);
            int length = receivePos + len;
            for(int i=0;i<length;i++){
                if(receiveBuffer[i]==0x7e){
                    if(tag%2==0){
                        start=i;
                    }else if(tag%2==1){
                        end=i;
                        //System.out.println(start+","+end);
                        byte[] b=Arrays.copyOfRange(receiveBuffer, start, end+1);
                        Log.d(TAG, "ReceivedMessage:" + TextUtil.toHexString(b));
                        HandleReceivedMessage(b);

                        //pos=end+1;
                        start=-1;end=-1;
                        if((i+1)<length && receiveBuffer[i+1]!=0x7e){
                            start=i;
                            tag++;
                        }
                    }
                    tag++;
                }
            }
            if(start>=0){
                byte[] restore=Arrays.copyOfRange(receiveBuffer, start, length);
                System.arraycopy(restore, 0, receiveBuffer, 0, restore.length);  //濡傛灉涓嶈兘鏋勬垚涓�釜甯э紝鍒欎繚瀛樺湪鏁扮粍涓�
                receivePos=restore.length;
            }else{
                receivePos=0;
                end=0;
            }
        }

    }
    
    
    void disconnect(Socket sock){
        Log.d(TAG, "disconnect socket: " + sock);
        try {
            sock.close();
        } catch (SocketTimeoutException aa) {

        } catch (IOException ioe){

        }
    }
    
    
    //API
    public boolean isIpcReady(){
    	
    	//socket is create , and connect is ok, means ipc ready.
    	//then can do registerApp
        //and need obdAuthOk first
    	return (ipcClientSocket!= null) && isIpcClientSocketConnected && isObdAuthOk;
    	
    }

    private boolean isSocketConnected(){
    	
    	//socket is create , and connect is ok, means ipc ready.
    	//then can do registerApp
        //and need obdAuthOk first
    	return (ipcClientSocket!= null) && isIpcClientSocketConnected;
    	
    }
    
    
    
    //API
    public int registerApp(String appId) {
        byte[] readBuff = new byte[64];
        int result = -1;
        boolean ret = false;

        NameCommand namecommmand = new NameCommand(appId);
        sendMessageToObd((new ObdFrame(namecommmand)).encode());

        int timeout = 10 * 1000;

        isRegisterOK = false;
        while (timeout > 0) {
            if (!isRegisterOK) {
                try {
                    Thread.sleep(2000);
                    timeout -= 2000;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                result = 0;
                break;
            }

        }

        Log.d(TAG, "registerApp result: " + result);
       // dsock.close();
        myAppId = appId;
        return result;
    }


    void sendMessageToObd(byte[] data){
        Log.d(TAG, "sendMessageToObd: " + TextUtil.toHexString(data));
        if((ipcClientSocket == null) ||  !isIpcClientSocketConnected){
            Log.d(TAG, "ipcClientSocket is not connected , ignore" );
            return;
        }

        try {
            OutputStream ou = ipcClientSocket.getOutputStream();
            ou.write(data);
            ou.flush();

        }catch (IOException e) {
            Log.d(TAG, "send Message To Obd failed");
            e.printStackTrace();
            
            disconnect(ipcClientSocket);
            ipcClientSocket = null;
            reconnectDelayed();

        }
    }

    public void HandleReceivedMessage(byte[] data){
        ObdFrame  frame = new ObdFrame(data);
        ObdMessage msg = frame.getMessage();

        Log.d(TAG, "HandleReceivedMessage " + msg.commandId);

        switch (msg.commandId) {

            case ObdFrame.OBDAUTH_COMMAND_ID:
                ObdAuthResponse obdauthresponse = new ObdAuthResponse(msg);
                
                if(obdauthresponse.RESULT == 0){
                    //success
                    isObdAuthOk = true;
                    obdAuthRetryTime = 0; //clear
 
                }else{
                    //failed
                    isObdAuthOk = false;
                    if(obdAuthRetryTime > 2){
                        //do not reconnect here !!!!!!!!!!!!!!!!!
                        disconnect(ipcClientSocket);
                        ipcClientSocket = null;
                    }
                }
                break;

            case ObdFrame.NAME_COMMAND_ID:
                if(!isObdAuthOk){
                    Log.d(TAG, "not Auth, only Handle Auth COMMAND, ignore ");
                    return;
                }

                NameResponse nameresponse = new NameResponse(msg);
                String name = TextUtil.bytesToString(nameresponse.NAME);

                if(!name.isEmpty()) {

                    isRegisterOK = (nameresponse.RESULT == 0);
                    Log.d(TAG, "isRegisterOK : " + isRegisterOK);
                }else{
                    Log.d(TAG, "myAppId != " + myAppId + " name " + name + ", ignore");
                }
                break;

            //from other
            case ObdFrame.IPCMESSAGE_COMMAND_ID:
                if(!isObdAuthOk  || !isRegisterOK){
                    Log.d(TAG, "not Auth/register ignore " + isObdAuthOk + " " + isRegisterOK);
                    return;
                }
                IpcMessage ipcmessage = new IpcMessage(msg);

                dispatchIpcMessage(ipcmessage.IPC_MESSAGE);
                break;

            case ObdFrame.ECHO_COMMAND_ID:
                if(!isObdAuthOk  || !isRegisterOK){
                    Log.d(TAG, "not Auth/register ignore " + isObdAuthOk + " " + isRegisterOK);
                    return;
                }

                EchoCommand echocommand = new EchoCommand(msg);
                String echoName = TextUtil.bytesToHexString(echocommand.NAME);
                //鍥炲簲锛岄渶瑕佺‘璁ゆ槸鑷繁鐨刟ppid锛屾墠鍥炲锛屽惁鍒欏拷鐣ャ�

                if(echoName.equals(myAppId)) {
                    EchoResponse echoresponse = new EchoResponse(myAppId);

                    sendMessageToObd( (new ObdFrame(echoresponse)).encode());
                }else{
                    Log.d(TAG, "myAppId != " + myAppId + " name" + echoName + ", ignore");
                }
                break;



            default:

                break;
        }


    }

    void dispatchIpcMessage(byte[] data){

        String srcID =  TextUtil.bytesToString(Arrays.copyOfRange(data, 0, 10));
        String dstID =  TextUtil.bytesToString(Arrays.copyOfRange(data, 10, 20));
        byte[] ipcMessage = Arrays.copyOfRange(data, 20, data.length);

        //棣栧厛鍐嶆纭锛岀殑纭槸鍙戦�缁欒嚜宸辩殑娑堟伅锛屽鏋滀笉鏄紝蹇界暐
        if(myAppId.compareTo(dstID.toString()) != 0){
            Log.d(TAG, "dispatchIpcMessage: " + myAppId + " != " +dstID.toString() + " ,ignore" );
            return;
        }

        if(messageHandler != null){
            messageHandler.handleMessage(srcID, dstID, ipcMessage);
        }
    }


    //API
    public void registerReceivedMessageHandler(IpcMessageHandler handler){
        messageHandler = handler;
    }

    
    //API
    public int sendMessage(String destAppId, byte[] ipcMessage){
        if(myAppId == null){
            Log.d(TAG, "sendMessage: but app not registered, do nothing" );
            return -1;
        }

        if(ipcMessage == null){
            Log.d(TAG, "sendMessage: ipcMessage is null, do nothing" );
            return -1;
        }

        Log.d(TAG, "sendMessage: to " + destAppId +" Message:" + TextUtil.toHexString(ipcMessage) );
        final byte[] buf = new byte[20 + ipcMessage.length];
        TextUtil.clearBytes(buf);
        System.arraycopy(myAppId.getBytes(), 0, buf, 0, myAppId.getBytes().length); //10
        if(destAppId != null) {
            System.arraycopy(destAppId.getBytes(), 0, buf, 10, destAppId.getBytes().length); //10
        }else{
            // 00
        }
        System.arraycopy(ipcMessage, 0, buf, 20, ipcMessage.length); //10

        Log.d(TAG, "sendMessageToObd:  IPC:" + TextUtil.toHexString(buf) );

        final IpcMessage rawipcMessage = new IpcMessage(buf);

        //线程化
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToObd((new ObdFrame(rawipcMessage)).encode());
            }
        }
        ).start();

        return 0;
    }


}
