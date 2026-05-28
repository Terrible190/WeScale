package com.example.demo.api.model.messages.out;

/**
 *
 * @author Usuari
 */
import com.example.demo.api.model.messages.MessageBody;

public class GAME_WIN_OUT extends MessageBody {

    public static final String TYPE = "GAME_WIN_OUT";

    @Override
    public String getMessageType() {
        return TYPE;
    }

}
