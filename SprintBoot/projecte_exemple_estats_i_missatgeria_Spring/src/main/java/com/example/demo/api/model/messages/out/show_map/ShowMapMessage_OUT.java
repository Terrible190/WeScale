package com.example.demo.api.model.messages.out.show_map;

import com.example.demo.api.model.messages.MessageBody;

public class ShowMapMessage_OUT extends MessageBody{

    public final static String TYPE = "SHOW_MAP";
    

    @Override
    public String getMessageType(){
        return TYPE;
    }

    public String map="Aquí o en d'altres variables, haurieu de posar info del mapa i que es pot triar.";
}
