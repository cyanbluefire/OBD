package com.obdphoneipc.data;

import java.util.Arrays;

/**
 * Created by Administrator on 2014/12/18.
 */
public class IpcMessage extends ObdMessage{

    final int COMMAND_ID = 0x0005;
    //消息体结构
    public byte[] IPC_MESSAGE;  //有可能为空


    //从根类中创建
    public IpcMessage(ObdMessage msg ) {
        super(msg);
        buildParams();
    }

    //从实际中创建
    public IpcMessage(byte[] ipcMessage ){
        commandId = COMMAND_ID;

        if(ipcMessage != null) {
            IPC_MESSAGE = new byte[ipcMessage.length];
            System.arraycopy(ipcMessage, 0, IPC_MESSAGE, 0, ipcMessage.length);
        }

        buildParams();
    }


    //每个扩展类实现对参数的重新分解和组装
    public boolean parseParams(){

        if(commandParams == null ||commandParams.length < 20 ) {
            //error!
            return false;
        }

        IPC_MESSAGE =  Arrays.copyOfRange(commandParams, 0, commandParams.length );
        return true;
    }

    public boolean buildParams(){

        if(commandParams == null){
            if(IPC_MESSAGE != null) {
                commandParams = new byte[IPC_MESSAGE.length];
            }else{
                commandParams = new byte[20];
            }
        }

        System.arraycopy(IPC_MESSAGE, 0, commandParams, 0, IPC_MESSAGE.length); //8
        return true;
    }



}
