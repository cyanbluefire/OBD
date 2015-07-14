
package com.obdphoneipc.util;

public class TextUtil {

	public static String toHexString(byte data){
		return "0x"+Integer.toHexString(data);
	}
	
	public static String toHexString(byte[] data){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0;i<data.length;i++){
			sb.append("0x"+Integer.toHexString(data[i])).append(",");
		}
		
		return sb.substring(0, sb.length()-1)+"]";
	}

	public static String toHexString(byte[] data, int length){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0;i<length;i++){
			sb.append("0x"+Integer.toHexString(data[i])).append(",");
		}

		return sb.substring(0, sb.length()-1)+"]";
	}

	public static void clearBytes(byte[] data){

		for(int i=0;i<data.length;i++){
			data[i] = 0;
		}
	}
	public static String bytesToString(byte[] data){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<data.length;i++){
			if(data[i]!=0) {
				sb.append((char)data[i]);
			}else{
				break;
			}
		}

		return sb.toString();
	}
    public static byte[] int2Byte(int a) {  
        byte[] b = new byte[4];  
        b[0] = (byte) (a >> 24);  
        b[1] = (byte) (a >> 16);  
        b[2] = (byte) (a >> 8);  
        b[3] = (byte) (a);  
  
        return b;  
    } 
    public static int byte2Int(byte[] b, int offset) {  
        return ((b[offset++] & 0xff) << 24) | ((b[offset++] & 0xff) << 16)  
                | ((b[offset++] & 0xff) << 8) | (b[offset++] & 0xff);  
    } 
    
	public static byte[] bytesAddBytes(byte[] bytes1, byte[] bytes2){
        byte[] newbytes = new byte[bytes1.length + bytes2.length];
		System.arraycopy(bytes1, 0, newbytes, 0, bytes1.length);
		System.arraycopy(bytes2, 0, newbytes, bytes1.length, bytes2.length);
		return newbytes;
	}


	/**  default:   linux   */
	public static String getLineSeparator(){
		return System.getProperty("line.separator", "\n"); 
	}
	
	public static String getLineSeparator(String defaultSeparator){
		if(defaultSeparator.equals("\r\n") || 
				defaultSeparator.equals("\r") ||
				defaultSeparator.equals("\n")){
			return System.getProperty("line.separator", defaultSeparator); 
		}
		throw new IllegalArgumentException();
	}
	
	
	/*add by chh*/
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	
	private static byte charToByte(char c) {  
	    return (byte) "0123456789ABCDEF".indexOf(c);  
	}  
	
	/** 
     * 锟斤拷16位锟斤拷short转锟斤拷锟斤拷byte锟斤拷锟斤拷 
     *  
     * @param s short 
     * @return byte[] 锟斤拷锟斤拷为2 
     * */  
  	public static byte[] shortToByteArray(short s) {  
        byte[] targets = new byte[2];  
        for (int i = 0; i < 2; i++) {  
            int offset = (targets.length - 1 - i) * 8;  
            targets[i] = (byte) ((s >>> offset) & 0xff);  
        }  
        return targets;  
    }  
    
  	/**
	 * byte锟斤拷锟斤拷转锟斤拷锟斤拷short
	 * @param b byte[]
	 * @return
	 **/
    public static short bytesToShort(byte[] b) {
    	return (short) (b[1] & 0xff
    	| (b[0] & 0xff) << 8);
    }
    
    /**
     * 拼锟斤拷锟斤拷息头
     * @param hex  锟斤拷息id
     * @param length  锟斤拷息锟斤拷锟捷筹拷锟斤拷
     * @return
     */
    public static byte[] setHead(String hex){
		byte[] head=new byte[12];
		byte[] id=TextUtil.hexStringToBytes(hex);
		
		System.arraycopy(id, 0, head, 0, 2);
		return head;
	}
    
    public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i =0; i<src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	} 
    
    public static String getMD5(byte[] source) {
		  String s = null;
		  char hexDigits[] = {       // 锟斤拷锟斤拷锟斤拷锟街斤拷转锟斤拷锟斤拷 16 锟斤拷锟狡憋拷示锟斤拷锟街凤拷
		     '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'}; 
		   try
		   {
		    java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );
		    md.update( source );
		    byte tmp[] = md.digest();          // MD5 锟侥硷拷锟斤拷锟斤拷锟斤拷一锟斤拷 128 位锟侥筹拷锟斤拷锟斤拷锟斤拷
		                                                // 锟斤拷锟街节憋拷示锟斤拷锟斤拷 16 锟斤拷锟街斤拷
		    char str[] = new char[16 * 2];   // 每锟斤拷锟街斤拷锟斤拷 16 锟斤拷锟狡憋拷示锟侥伙拷锟斤拷使锟斤拷锟斤拷锟斤拷锟街凤拷锟斤拷
		                                                 // 锟斤拷锟皆憋拷示锟斤拷 16 锟斤拷锟斤拷锟斤拷要 32 锟斤拷锟街凤拷
		    int k = 0;                                // 锟斤拷示转锟斤拷锟斤拷锟斤拷卸锟接︼拷锟斤拷址锟轿伙拷锟�
		    for (int i = 0; i < 16; i++) {          // 锟接碉拷一锟斤拷锟街节匡拷始锟斤拷锟斤拷 MD5 锟斤拷每一锟斤拷锟街斤拷
		                                                 // 转锟斤拷锟斤拷 16 锟斤拷锟斤拷锟街凤拷锟斤拷转锟斤拷
		     byte byte0 = tmp[i];                 // 取锟斤拷 i 锟斤拷锟街斤拷
		     str[k++] = hexDigits[byte0 >>> 4 & 0xf];  // 取锟街斤拷锟叫革拷 4 位锟斤拷锟斤拷锟斤拷转锟斤拷, 
		                                                             // >>> 为锟竭硷拷锟斤拷锟狡ｏ拷锟斤拷锟斤拷锟斤拷位一锟斤拷锟斤拷锟斤拷
		     str[k++] = hexDigits[byte0 & 0xf];            // 取锟街斤拷锟叫碉拷 4 位锟斤拷锟斤拷锟斤拷转锟斤拷
		    } 
		    s = new String(str);                                 // 锟斤拷锟斤拷慕锟斤拷转锟斤拷为锟街凤拷锟斤拷

		   }catch( Exception e )
		   {
		    e.printStackTrace();
		   }
		   return s;
		 }
    
    
    
    
    /**
	public static void Decode(InputStream in,MessageRecieveListener ml){
		
		byte[] temp=new byte[1024*4];
		byte[] msg=new byte[1024];
		int start=0,end=0;
		int tag=0;
		int pos=0;
		int len;
		while(true){
			try {
				if((len=in.read(msg))>0){
					System.arraycopy(msg, 0, temp, pos, len);
					int length=pos+len;
					for(int i=0;i<length;i++){
						if(msg[i]==0x7e){
							
							if(tag%2==0){
								start=i;
							}else if(tag%2==1){
								end=i;
								System.out.println(start+","+end);
								byte[] b=Arrays.copyOfRange(msg, start, end+1);
								ml.HavaRecieveMsg(b);
								start=0;end=0;
								if((i+1)<length && msg[i+1]!=0x7e){
									start=i;
									tag++;
								}
							}
							tag++;
						}
					}
					if(start!=0){
						pos=length-start;
						System.arraycopy(msg, start, temp, 0, pos-1);  //锟斤拷锟斤拷锟斤拷芄锟斤拷锟揭伙拷锟街★拷锟斤拷虮４锟斤拷锟斤拷锟斤拷锟斤拷锟�
					}else{
						pos=0;
						end=0;
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
    
}
