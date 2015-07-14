package com.neoway.vehiclebeta1.constant;

public class Constant {
	public static final String ADDRESS = "http://172.24.21.17:8080/VehicleBaby/";
	public static final String REG_URL = ADDRESS+"account";
	public static final String LOGIN_URL = ADDRESS+"account/login";
	public static final String Exit_URL = ADDRESS+"account/logout";
	public static final String BIND_URL = ADDRESS+"account/bind";
	public static final String UNBIND_URL = ADDRESS+"account/unbind";
	public static final String START_NAVI = ADDRESS+"navigation/start";
	public static final String CANCEL_NAVI = ADDRESS+"navigation/stop";
//	public static final String BIND_URL = "http://172.24.21.5:8080/VehicleBaby/account/bind";
	public static final String IDENTIFYOBD_URL = "192.168.43.1";
	public static final int IDENTIFYOBD_PORT= 31313;
	
	
	
	public static final byte[] EMPTY_BYTE_ARRAY_8BITS =new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
	public static final short URA_DEVICE_TYPE_OBD = 0x0001;
	
	
	public static final String APPID = "PhoneApp";
	public static final String NAVI_APP_ID = "10010";
	public static final short EVENT_DESTINATION  = 101;
	public static final short EVENT_ACK_INFORMATION = 111;
	public static final byte LATLNG_TYPE_BD09_MC  = 2;

	
}
