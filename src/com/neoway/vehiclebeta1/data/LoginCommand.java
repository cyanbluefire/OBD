package com.neoway.vehiclebeta1.data;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.util.Log;

import com.baidu.platform.comapi.map.A;
import com.neoway.vehiclebeta1.constant.Constant;
import com.obdphoneipc.util.TextUtil;

/**
 * Created by Administrator on 2014/12/15.
 */
//public class ObdAuthCommand


@SuppressLint("NewApi")
public class LoginCommand extends ObdMessage{

    final static short COMMAND_ID = 0x0001;
    //娑堟伅浣撶粨鏋�
    public byte[] ACCOUNT;  	//11 bytes
    public byte[] AREA_NUM;		//4 bytes
    public byte[] PASSWORD; 	// 20 bytes
    public byte[] IMEI;			//15 
    
    //浠庢牴绫讳腑鍒涘�?
    public LoginCommand(ObdMessage msg ) {
        super(msg);
        buildParams();
    }

    public void encodeAll (String...str){
    	
    }
    //浠庡疄闄呬腑鍒涘�?
    public LoginCommand( String account,String imei, String psw ){
        super();
        commandId = COMMAND_ID;
        Log.i(TAG, "account=="+account);
        AREA_NUM = new byte[4];
        AREA_NUM =("0086").getBytes();
        
        
        ACCOUNT = new byte[11];
        System.arraycopy(account.getBytes(), 0, ACCOUNT, 0, account.getBytes().length > ACCOUNT.length ?
                ACCOUNT.length : account.getBytes().length);
         
        IMEI = new byte[15];
        System.arraycopy(imei.getBytes(), 0, IMEI, 0, imei.getBytes().length > IMEI.length ?
        		IMEI.length : imei.getBytes().length);
        
        PASSWORD =  psw.getBytes();

        buildParams();
    }
    
    //public ObdAuthCommand(String Appid,String destID,)


    
	public boolean parseParams(){

        if(commandParams == null) {
            //error!
            return false;
        }

        AREA_NUM = Arrays.copyOfRange(commandParams, 0, 3);
        ACCOUNT = Arrays.copyOfRange(commandParams, 4, 14);
        PASSWORD =  Arrays.copyOfRange(commandParams, 15, PASSWORD.length);
        return true;
    }

    public boolean buildParams(){

        if(commandParams == null){
            commandParams = new byte[PASSWORD.length+30];							//
        }
    	
        System.arraycopy(IMEI, 0, commandParams, 0, IMEI.length); //15
        System.arraycopy(AREA_NUM, 0, commandParams, 15, AREA_NUM.length); //4
        System.arraycopy(ACCOUNT, 0, commandParams, 19, ACCOUNT.length); //11
        System.arraycopy(PASSWORD, 0, commandParams, 30, PASSWORD.length); //
        
        toBytes();
        return true;
    }
    //消息串行�?
    public byte[] toBytes(){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        writeStreamShort(baos, COMMAND_ID);
        if(commandParams != null) {
            baos.write(commandParams, 0, commandParams.length);
        }

       // baos.write(canData, 0, 8);
        return baos.toByteArray();
    }



    	











}


