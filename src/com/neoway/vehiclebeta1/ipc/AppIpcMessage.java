package com.neoway.vehiclebeta1.ipc;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import com.obdphoneipc.util.TextUtil;

import android.util.Log;

public class AppIpcMessage {
	short cmd;
	byte[] data;
	
	byte type;
	int longitude;
	int latitude;
	String dest_name;
	
	
	public AppIpcMessage (byte[] rawdata){
		decode(rawdata);
	}
	
	
	public AppIpcMessage (short cmd,byte[] data){
		this.cmd = cmd;
		this.data = data;
		
	}
	public AppIpcMessage(short cmd, byte type,int longitude,int latitude,String dest_name){
		this.cmd = cmd;
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
		this.dest_name = dest_name;
	}
	
	public short getCmd(){
		Log.i("AppIpcMessage", "getCmd=="+cmd);
		return cmd;
	}
	
	public String getDataStr(){
		try {
			return new String(data,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * app进程间通信 编码
	 */
	public  byte[] encode() {
		
		byte[] CMD = new byte[2];
		CMD[0] = (byte) ( (cmd >> 8) & 0x00FF );
		CMD[1] = (byte) ( (cmd ) & 0x00FF );

		
		ByteArrayOutputStream total = new ByteArrayOutputStream();

			total.write(CMD,0, CMD.length);
			total.write(data,0,data.length);
			
			
			System.out.println("encode=="+total.toString());
			
		return total.toByteArray();
		
	}
	/**
	 * 发送 导航信息编码
	 */
	public byte[] encodeNavi(){
		Log.i("encodeNavi"," cmd=="+cmd+" type=="+type+" longitude=="+longitude+" latitude=="+latitude+" dest_name=="+dest_name);
		byte[] CMD = new byte[2];
		CMD[0] = (byte) ( (cmd >> 8) & 0x00FF );
		CMD[1] = (byte) ( (cmd ) & 0x00FF );
		
		ByteArrayOutputStream total = new ByteArrayOutputStream();
		total.write(CMD,0, CMD.length);
		total.write(type);
		byte[] longitude_byte = TextUtil.int2Byte(longitude);
		byte[] latitude_byte = TextUtil.int2Byte(latitude);
		
		
		Log.i("encodeNavi","longitude_int=="+TextUtil.byte2Int(longitude_byte, 0));
		
		total.write(longitude_byte,0,longitude_byte.length);
		total.write(latitude_byte,0,latitude_byte.length);
		
		try {
			byte[] dest_name_byte = dest_name.getBytes("UTF-8");
			total.write(dest_name_byte, 0 , dest_name_byte.length);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		total.write(0x0);	//以0x0结尾
		
		System.out.println("encodeNavi=="+total.toString());

		return total.toByteArray();
	}
	
	/**
	 * app进程间通信 解码
	 */
	public void decode(byte[] appImcMessage){
		
		data =new byte[appImcMessage.length -2 ];
		Log.i("decode","appImcMessage[0]=="+appImcMessage[0]);
		Log.i("decode","appImcMessage[1]"+appImcMessage[1]);

		cmd = (short) (((appImcMessage[0] & 0xff) << 8) | (appImcMessage[1] & 0xff)); 
		//cmd = (short)(((appImcMessage[0] << 8) & 0xFF00) + appImcMessage[1]);
		Log.i("decode","appImcMessage[0]=="+appImcMessage[0]);
		Log.i("decode","appImcMessage[0]<<8 =="+((appImcMessage[0] << 8) & 0xFF00));

		Log.i("decode","cmd decode=="+cmd);
				
		System.arraycopy(appImcMessage, 2, data, 0, (appImcMessage.length-2));

	//	return ipcMsg;
		
	}
	
	
	
	
	
	
	
	
}
