package com.neoway.vehiclebeta1.utils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtil {
	public static String md5(String str){
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] result = md.digest(str.getBytes("utf-8"));
			return parseByte2HexStr(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static String parseByte2HexStr(byte[] buf) {
		StringBuffer builder = new StringBuffer();
		for(int i=0;i<buf.length;i++){
			String hex =Integer.toHexString(buf[i] & 0xFF);
			if(hex.length() == 1){
				hex = '0' + hex;
			}
			builder.append(hex.toLowerCase());
		}		
		return builder.toString();
	}
	
	public static String decrypt(String after_aes_content, String password){
		try{
			byte[] content = parseHexStr2Byte(after_aes_content);
			SecretKey secretKey = getKey(password);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] result = cipher.doFinal(content);
			String bef_aes = new String(result);
			return bef_aes;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static byte[] parseHexStr2Byte(String hexStr){
		if(hexStr.length() <1)
			return null;
		byte[] result = new byte[hexStr.length()/2];
		for(int i=0;i < hexStr.length()/2;i++){
			int value = Integer.parseInt(hexStr.substring(i * 2,i *2 +2),16);
			result[i] = (byte)value;
		}
		return result;
	}
	
	public static SecretKey getKey(String strKey) { 
		byte[] thedigest = new byte[0];
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			thedigest = md.digest(strKey.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SecretKeySpec(thedigest, "AES");
	}
	

}
