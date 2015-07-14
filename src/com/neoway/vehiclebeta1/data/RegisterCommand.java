package com.neoway.vehiclebeta1.data;

import java.util.Arrays;

import android.annotation.SuppressLint;

import com.baidu.platform.comapi.map.A;
import com.neoway.vehiclebeta1.constant.Constant;
import com.obdphoneipc.util.TextUtil;

/**
 * Created by Administrator on 2014/12/15.
 */
//public class ObdAuthCommand


@SuppressLint("NewApi")
public class RegisterCommand extends ObdMessage{

    final static int COMMAND_ID = 0x0001;
    final static byte CHINESE_AREA_NUM = (byte) 0x56;	//十进制为86
    //娑堟伅浣撶粨鏋�
    public byte[] ACCOUNT;  //11 bytes
    public byte[] AREA_NUM;		//4 bytes
    public byte[] PASSWORD; // 20 bytes

    //浠庢牴绫讳腑鍒涘�?
    public RegisterCommand(ObdMessage msg ) {
        super(msg);
        buildParams();
    }

    //浠庡疄闄呬腑鍒涘�?
    public RegisterCommand( String account, String psw ){
        super();
        commandId = COMMAND_ID;
        
        AREA_NUM = new byte[4];
        AREA_NUM[0] =0x0;
        AREA_NUM[1] =0x0;
        AREA_NUM[2] =0x0;
        AREA_NUM[3] =CHINESE_AREA_NUM;
     
        ACCOUNT = new byte[11];
        System.arraycopy(account.getBytes(), 0, ACCOUNT, 0, account.getBytes().length > ACCOUNT.length ?
                ACCOUNT.length : account.getBytes().length);
             
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
            commandParams = new byte[PASSWORD.length+15];							//
        }
    	
        System.arraycopy(AREA_NUM, 0, commandParams, 0, AREA_NUM.length); //4
        System.arraycopy(ACCOUNT, 0, commandParams, 4, ACCOUNT.length); //11
        System.arraycopy(PASSWORD, 0, commandParams, 15, PASSWORD.length); //
        return true;
    }



    	











}


