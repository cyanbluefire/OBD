package com.obdphoneipc.data;

import java.util.Arrays;

/**
 * Created by Administrator on 2014/12/15.
 */
//public class ObdAuthCommand


public class ObdAuthCommand extends ObdMessage{

    final static int COMMAND_ID = 0x0004;
    //消息体结构
    public byte[] ACCOUNT;  //20 bytes
    public byte[] TOKEN; // 20 bytes

    //从根类中创建
    public ObdAuthCommand(ObdMessage msg ) {
        super(msg);
        parseParams();
    }

    //从实际中创建
    public ObdAuthCommand( String account, String token ){
        super();
        commandId = COMMAND_ID;


        ACCOUNT = new byte[20];
        System.arraycopy(account.getBytes(), 0, ACCOUNT, 0, account.getBytes().length > ACCOUNT.length ?
                ACCOUNT.length : account.getBytes().length);

        TOKEN =  new byte[20];
        System.arraycopy(token.getBytes(), 0, TOKEN, 0, token.getBytes().length > TOKEN.length?
                TOKEN.length : token.getBytes().length);

        buildParams();
    }


    public boolean parseParams(){

        if(commandParams == null ||commandParams.length != 40) {
            //error!
            return false;
        }


        ACCOUNT = Arrays.copyOfRange(commandParams, 0, 20);
        TOKEN =  Arrays.copyOfRange(commandParams, 20, 40);
        return true;
    }

    public boolean buildParams(){

        if(commandParams == null){
            commandParams = new byte[40];
        }


        System.arraycopy(ACCOUNT, 0, commandParams, 0, ACCOUNT.length); //20
        System.arraycopy(TOKEN, 0, commandParams, 20, TOKEN.length); //20
        return true;
    }











}


