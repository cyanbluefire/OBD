package com.neoway.vehiclebeta1.data;

public class GlobalVariable {
	public static boolean ipcState= false;
	
	public void setIPCReady(boolean ret){
		this.ipcState = ret;
	}
	
	public boolean getIsIPCReady(){
		return ipcState;
	}

}
