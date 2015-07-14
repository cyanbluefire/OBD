package com.obdphoneipc.data;

import java.io.ByteArrayOutputStream;
import com.obdphoneipc.data.ObdMessage;

import android.util.Log;
import com.obdphoneipc.util.TextUtil;

/**
 * Created by Administrator on 2014/12/11.
 */
public class ObdFrame {
   static final String TAG = "ObdFrame";

    public final static int AUTH_COMMAND_ID = 0x01;
    public final static int BIND_COMMAND_ID = 0x02;
    public final static int UNBIND_COMMAND_ID = 0x03;
    public final static int OBDAUTH_COMMAND_ID = 0x04; //zhuan yong
    public final static int IPCMESSAGE_COMMAND_ID = 0x05;
    public final static int NAME_COMMAND_ID = 0x06;
    public final static int ECHO_COMMAND_ID = 0x07;

    ObdMessage messageBody;

    public ObdFrame(){

    }

    //从message来
    public ObdFrame(ObdMessage body){
        messageBody = body;
    }

    //从未解析的帧来
    public ObdFrame(byte[] rawBody){
        decode(rawBody);
    }


    public ObdMessage getMessage(){
        return messageBody;
    }


    //假设传入的就是一个完整的7e头尾的帧
    public boolean  decode(byte[]  bytes){

        if( bytes[0] !=0x7e || bytes[bytes.length -1] !=0x7e  ) {
            Log.d(TAG, "fame error");
            return false;
        }

        byte[] unescapedMessage = new byte[bytes.length -2];//去掉两个0x7e
        System.arraycopy(bytes, 1, unescapedMessage, 0, unescapedMessage.length );

        byte[] messageBytes = unEscape(unescapedMessage);

        byte checksum = messageBytes[messageBytes.length -1];

        //计算checksum
        if(!checkCheckSum(messageBytes,messageBytes.length -1, checksum )) {
            Log.d(TAG, "fame checksum error");
            return false;// failed
        }

        //Log.d(TAG, "messageBytes " + TextUtil.toHexString(messageBytes));
        //ObdFrame frame = new ObdFrame(messageBytes ,messageBytes.length -1 );
        messageBody = new ObdMessage(messageBytes ,messageBytes.length -1);
        //到此完成填充，后面转义
        //Log.d(TAG, "messageBody ID:  " + messageBody.commandId);

        return true;
    }

    //每个消息都可以转化成byte[]
    public byte[] encode(){
        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        byte[] message = messageBody.toBytes();

        msgBody.write(message,0,message.length);

        //校验码
        byte cs = calculateCheckSum( msgBody.toByteArray());
        msgBody.write(cs);

        //到此完成填充，后面转义
        byte[] escapeBytes = escape(msgBody.toByteArray());

        ByteArrayOutputStream total = new ByteArrayOutputStream();
        total.write(0x7e);  //START
        total.write(escapeBytes, 0, escapeBytes.length); //ESCAPED BYTEs
        total.write(0x7e);  //END

        return total.toByteArray();
    }


    //计算校验码
    private byte calculateCheckSum(byte[] byteArray){
        int checksum = 0;

        checksum = byteArray[0];
        for(int i = 1; i < byteArray.length; i++){

            checksum ^= (int)(byteArray[i]);
        }

        return (byte)checksum;
    }


    private boolean checkCheckSum(byte[] byteArray, int length, byte byteChecksum){
        int checksum = 0;

        checksum = byteArray[0];
        for(int i = 1; i < length; i++){

            checksum ^= (int)(byteArray[i]);
        }

        Log.d(TAG, "checksum calculate: " + checksum + " checksum received:" + byteChecksum);
        return (checksum == byteChecksum);
    }

    //转码
    private byte[] escape(byte[] byteArray){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for(int i = 0; i < byteArray.length; i++){

            if(byteArray[i] == 0x7e) {
                baos.write(0x7d);
                baos.write((byte)(0x7e^0x20));
            }else if (byteArray[i] == 0x7d) {
                baos.write(0x7d);
                baos.write((byte)(0x7d^0x20));
            }else{
                baos.write(byteArray[i]);
            }
        }

        return baos.toByteArray();
    }

    private byte[] unEscape(byte[] byteArray){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for(int i = 0; i < byteArray.length; i++){

            if(byteArray[i] == 0x7e) {
                //error!

            }else if (byteArray[i] == 0x7d) {
                if(i < byteArray.length - 1){
                    if(byteArray[i+1] == (byte)(0x7d^0x20)){ //0x7d^0x20
                        i++;
                        baos.write(0x7d);
                    }else if(byteArray[i+1] == (byte)(0x7e^0x20)){ //0x7e^0x20
                        i++;
                        baos.write(0x7e);
                    }else{
                        baos.write(byteArray[i]);
                    }
                }else {
                    baos.write(byteArray[i]);
                }
            }else{
                baos.write(byteArray[i]);
            }
        }
        return baos.toByteArray();
    }

}

