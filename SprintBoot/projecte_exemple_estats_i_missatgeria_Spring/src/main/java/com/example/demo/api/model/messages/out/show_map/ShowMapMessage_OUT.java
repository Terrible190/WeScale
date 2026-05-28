package com.example.demo.api.model.messages.out.show_map;

import com.example.demo.api.model.messages.MessageBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShowMapMessage_OUT extends MessageBody {

    public final static String TYPE = "SHOW_MAP";

    public String map;

    public int roomId;

    public List<Integer> completedFloors;

    public ShowMapMessage_OUT() {
    }

    public ShowMapMessage_OUT(
            Set<Integer> completed)
    {
        this.completedFloors =
            new ArrayList<>(completed);
    }

    @Override
    public String getMessageType(){
        return TYPE;
    }
}