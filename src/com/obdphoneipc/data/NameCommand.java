package com.obdphoneipc.data;

import java.util.Arrays;
import android.util.Log;
import com.obdphoneipc.util.TextUtil;

/**
 * Created by Administrator on 2014/12/18.
 */


public class NameCommand extends ObdMessage {

    final static int COMMAND_ID = 0x0006;
    //消息体结构
    public byte[] NAME;  //10 bytes

    //从根类中创建
    public NameCommand(ObdMessage msg) {
        super(msg);
        parseParams();
    }

    //从实际中创建
    public NameCommand(String name) {
        super();
        commandId = COMMAND_ID;

        NAME = new byte[10];
        TextUtil.clearBytes(NAME);
        System.arraycopy(name.getBytes(), 0, NAME, 0, name.getBytes().length > NAME.length ?
                NAME.length : name.getBytes().length);

        buildParams();
    }


    public boolean parseParams() {

        if (commandParams == null || commandParams.length != 10) {
            //error!
            Log.e("NameCommand", "parseParams error");
            return false;
        }

        NAME = Arrays.copyOfRange(commandParams, 0, 10);
        return true;
    }

    public boolean buildParams() {

        if (commandParams == null) {
            commandParams = new byte[10];
            TextUtil.clearBytes(commandParams);
        }

        System.arraycopy(NAME, 0, commandParams, 0, NAME.length); //10
        return true;
    }

}