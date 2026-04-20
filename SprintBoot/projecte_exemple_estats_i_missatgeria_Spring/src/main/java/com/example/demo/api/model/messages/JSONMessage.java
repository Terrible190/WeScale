package com.example.demo.api.model.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Estructura genèrica dels missatges rebuts/enviats
 * 
 * Tenen la forma:
 *  {
    "messageType":"MESAGE_CODE",
    "gameId":"aa1b74d3-db4e-4a53-bc26-abf257a71881",
    "data":{
            WHATEVER IN YOUR MIND
        }
    }
 */

@JsonPropertyOrder({ "messageType", "gameId", "data" }) // volem ordenar de certa forma el JSON

public class JSONMessage {


    @JsonCreator
    public JSONMessage(){} // Obligatori per a que funcioni a deserialització

    /**
     * Constructor per enviar missatges donat un objecte de dades que hem preparat
     * @gameId id del joc
     * @param messageBodyObject les dades que s'envien del missatge
     */
    public JSONMessage(String gameId, MessageBody messageBodyObject){
        ObjectMapper mapper = new ObjectMapper();
        this.messageType = messageBodyObject.getMessageType();
        this.data = mapper.valueToTree(messageBodyObject);
        this.gameId = gameId;
    }

    public String messageType;
    public String gameId;
    public JsonNode data; // node genèric (pot ser qualevol objecte complex)

    
}
