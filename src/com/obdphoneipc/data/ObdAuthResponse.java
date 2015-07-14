package com.obdphoneipc.data;

/**
 * Created by Administrator on 2014/12/15.
 */


public class ObdAuthResponse extends ObdMessage{

    final int COMMAND_ID = 0x0004;
    //消息体结构
    public byte RESULT;


    //从根类中创建
    public ObdAuthResponse(ObdMessage msg ) {
        super(msg);
        parseParams();
    }

    //从实际中创建
    public ObdAuthResponse(byte result){
        super();
        commandId = COMMAND_ID;

        RESULT = result;
        buildParams();
    }

    public boolean parseParams(){

        if(commandParams==null || commandParams.length != 1) {
            //error!
            return false;
        }

        RESULT = commandParams[0];
        return true;
    }

    public boolean buildParams(){

        if(commandParams == null){
            commandParams = new byte[1];
        }

        commandParams[0] = RESULT;
        return true;
    }

}
