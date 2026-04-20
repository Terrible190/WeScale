package com.example.demo.api.model.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class MessageBody {
    
    @JsonIgnore 
    public abstract String getMessageType();
}
