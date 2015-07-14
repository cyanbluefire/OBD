package com.neoway.vehiclebeta1.data;

import java.io.ByteArrayOutputStream;

import com.neoway.vehiclebeta1.data.*;

import android.util.Log;

/**
 * Created by Administrator on 2014/12/11.
 */
public class ObdFrame {
   static final String TAG = "ObdFrame";

    //public final static short REGISTER_COMMAND_ID = 0x0001;
    public final static short LOGIN_COMMAND_ID = 0x0001;
    public final static short BIND_COMMAND_ID = 0x0002;
    public final static short UNBIND_COMMAND_ID = 0x0003;

    ObdMessage messageBody;

    public ObdFrame(){

    }

    //浠巑essage鏉�
    public ObdFrame(ObdMessage body){
        messageBody = body;
    }

    //浠庢湭瑙ｆ�?鐨勫抚鏉�?    
    public ObdFrame(byte[] rawBody){
        decode(rawBody);
    }


    public ObdMessage getMessage(){
        return messageBody;
    }


    //鍋囪浼犲叆鐨勫氨鏄竴涓畬鏁寸殑7e澶村熬鐨勫抚
    public boolean  decode(byte[]  bytes){

        if( bytes[0] !=0x7e || bytes[bytes.length -1] !=0x7e  ) {
            Log.d(TAG, "fame error");
            return false;
        }

        byte[] unescapedMessage = new byte[bytes.length -2];//鍘绘�?��や釜0x7e
        System.arraycopy(bytes, 1, unescapedMessage, 0, unescapedMessage.length );

        byte[] messageBytes = unEscape(unescapedMessage);

        byte checksum = messageBytes[messageBytes.length -1];

        //璁＄畻checksum
        if(!checkCheckSum(messageBytes,messageBytes.length -1, checksum )) {
            Log.d(TAG, "fame checksum error");
            return false;// failed
        }

        //Log.d(TAG, "messageBytes " + TextUtil.toHexString(messageBytes));
        //ObdFrame frame = new ObdFrame(messageBytes ,messageBytes.length -1 );
        messageBody = new ObdMessage(messageBytes ,messageBytes.length -1);
        //鍒版�?屾垚濉厖锛屽悗闈㈣浆涔�?        //Log.d(TAG, "messageBody ID:  " + messageBody.commandId);

        return true;
    }

    //姣忎釜娑堟伅閮藉彲浠ヨ浆鍖栨垚byte[]
    public byte[] encode(){
        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();
        byte[] message = messageBody.toBytes();

        msgBody.write(message,0,message.length);

        //鏍￠獙鐮�?        
        byte cs = calculateCheckSum( msgBody.toByteArray());
        msgBody.write(cs);

        //鍒版�?屾垚濉厖锛屽悗闈㈣浆涔�?       
        byte[] escapeBytes = escape(msgBody.toByteArray());

        ByteArrayOutputStream total = new ByteArrayOutputStream();
        total.write(0x7e);  //START
        total.write(escapeBytes, 0, escapeBytes.length); //ESCAPED BYTEs
        total.write(0x7e);  //END

        return total.toByteArray();
    }
    
    public byte[] encode(byte[] message){
        ByteArrayOutputStream msgBody = new ByteArrayOutputStream();

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

    //璁＄畻鏍￠獙鐮�
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

    //杞�?
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

