package com.example.demo.api.model.messages.out;

/**
 *
 * @author Usuari
 */
import com.example.demo.api.model.messages.MessageBody;

public class GAME_LOSE_OUT extends MessageBody {

    public static final String TYPE = "GAME_LOSE_OUT";

    @Override
    public String getMessageType() {
        return TYPE;
    }

}
