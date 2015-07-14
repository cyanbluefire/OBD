package com.neoway.vehiclebeta1.ipc;






public class UseCase {
	
	short cmd = 100;
	String data = "shenzhen";
	AppIpcMessage ipcMsg = new AppIpcMessage(cmd, data.getBytes());
	byte[] encodeMsg = ipcMsg.encode();

}
