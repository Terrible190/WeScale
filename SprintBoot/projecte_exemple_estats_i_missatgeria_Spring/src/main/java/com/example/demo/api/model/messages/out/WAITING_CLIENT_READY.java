package com.example.demo.api.model.messages.out;

/**
 *
 * @author Usuari
 */
import com.example.demo.api.model.messages.MessageBody;

public class WAITING_CLIENT_READY extends MessageBody {

    public static final String TYPE = "WAITING_CLIENT_READY";

    @Override
    public String getMessageType() {
        return TYPE;
    }

}
