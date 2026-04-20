package com.example.demo.api.model.messages.out.generic;

import com.example.demo.api.model.messages.MessageBody;


public class ActionResult_OUT extends MessageBody{

    public final static String TYPE = "RESULT";
    
    public ActionResult_OUT(boolean success, int errorCode) {
        this.success = success;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessageType(){
        return TYPE;
    }

    public boolean success;
    public int errorCode;





}
