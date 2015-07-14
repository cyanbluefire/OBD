package com.obdphoneipc.data;
import com.obdphoneipc.util.TextUtil;

import java.util.Arrays;

/**
 * Created by Administrator on 2014/12/18.
 */

public class NameResponse extends ObdMessage{

    final int COMMAND_ID = 0x0006;
    //消息体结构
    public byte[] NAME;  //10 bytes
    public byte RESULT;


    //从根类中创建
    public NameResponse(ObdMessage msg ) {
        super(msg);
        parseParams();
    }

    //从实际中创建
    public NameResponse(String name ,byte result){
        super();
        commandId = COMMAND_ID;

        NAME = new byte[10];
        TextUtil.clearBytes(NAME);
        System.arraycopy(name.getBytes(), 0, NAME, 0, name.getBytes().length > NAME.length ?
                NAME.length : name.getBytes().length);

        RESULT = result;
        buildParams();
    }

    public boolean parseParams(){

        if(commandParams==null || commandParams.length != 11) {
            //error!
            return false;
        }

        NAME = Arrays.copyOfRange(commandParams, 0, 10);
        RESULT = commandParams[10];;
        return true;
    }

    public boolean buildParams(){

        if(commandParams == null){
            commandParams = new byte[11];
            TextUtil.clearBytes(commandParams);
        }

        System.arraycopy(NAME, 0, commandParams, 0, NAME.length); //10
        commandParams[10] = RESULT;

        return true;
    }

}
