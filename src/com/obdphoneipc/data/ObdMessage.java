package com.obdphoneipc.data;

import android.util.Log;
import com.obdphoneipc.util.TextUtil;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2014/12/11.
 */



public class ObdMessage{
    static final String TAG = "ObdMessage";

    public short commandId;
    public byte[] commandParams; //消息体，这里只有一个指针，具体的消息格式不同都要求实现 toBytes 实现统一接口。


    public boolean parseParams(){

        return true;
    }

    public boolean buildParams(){

        return true;
    }


    public ObdMessage(){

    }

    public ObdMessage(ObdMessage clone){
        commandId = clone.commandId;
        if(clone.commandParams != null) {
            commandParams = new byte[clone.commandParams.length];
            System.arraycopy(clone.commandParams, 0, commandParams, 0, commandParams.length);
        }

        parseParams();
    }

    //来自网络侧
    public ObdMessage(byte[] data, int length){

        if(length < 2)
            return;

        //Log.d(TAG, "ObdMessage " + TextUtil.toHexString(data) + "length:" + length);
        commandId = (short)((data[0]<<8) + data[1]);

        if(length > 2) {
            commandParams = new byte[length - 2];
            TextUtil.clearBytes(commandParams);
            System.arraycopy(data, 2, commandParams, 0, commandParams.length);
        }

        parseParams();//如果有解析器的就进行解析
    }


    void writeStreamShort(ByteArrayOutputStream io, short data){
        io.write((byte)((data & 0xFF00) >>8)); //high
        io.write((byte)(data & 0x00FF)); //low
    }

    //消息串行化
    public byte[] toBytes(){

        //首先把消息参数部分拼装起来
        buildParams();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        writeStreamShort(baos, commandId);
        if(commandParams != null) {
            baos.write(commandParams, 0, commandParams.length);
        }

       // baos.write(canData, 0, 8);
        return baos.toByteArray();
    }
}

