package com.neoway.vehiclebeta1.data;

/**
 * Created by Administrator on 2014/12/15.
 */


public class ObdAuthResponse extends ObdMessage{

    final int COMMAND_ID = 0x0004;
    //娑堟伅浣撶粨鏋�
    public byte RESULT;


    //浠庢牴绫讳腑鍒涘�?
    public ObdAuthResponse(ObdMessage msg ) {
        super(msg);
        buildParams();
    }

    //浠庡疄闄呬腑鍒涘�?
    public ObdAuthResponse(byte result){
        super();
        commandId = COMMAND_ID;

        RESULT = result;
    }

    public boolean parseParams(){

        if(commandParams==null || commandParams.length != 1) {
            //error!
            return false;
        }

        RESULT = commandParams[0];
        return true;
    }

    public boolean buildParams(){

        if(commandParams == null){
            commandParams = new byte[1];
        }

        commandParams[0] = RESULT;
        return true;
    }

}
